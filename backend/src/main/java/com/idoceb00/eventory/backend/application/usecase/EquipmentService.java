package com.idoceb00.eventory.backend.application.usecase;

import com.idoceb00.eventory.backend.domain.model.Equipment;
import com.idoceb00.eventory.backend.domain.model.EquipmentCategory;
import com.idoceb00.eventory.backend.domain.model.EquipmentStatus;
import com.idoceb00.eventory.backend.domain.model.ReservationLine;
import com.idoceb00.eventory.backend.domain.service.DuplicateEquipmentException;
import com.idoceb00.eventory.backend.domain.service.EntityNotFoundException;
import com.idoceb00.eventory.backend.domain.service.EquipmentStateException;
import com.idoceb00.eventory.backend.domain.service.InsufficientStockException;
import com.idoceb00.eventory.backend.domain.service.InvalidStockAdjustmentException;
import com.idoceb00.eventory.backend.infrastructure.persistence.EquipmentRepository;
import com.idoceb00.eventory.backend.infrastructure.persistence.ReservationRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipmentService {

  private final EquipmentRepository equipmentRepository;
  private final ReservationRepository reservationRepository;

  public EquipmentService(
      EquipmentRepository equipmentRepository, ReservationRepository reservationRepository) {
    this.equipmentRepository = equipmentRepository;
    this.reservationRepository = reservationRepository;
  }

  @Transactional
  public Equipment createEquipment(String name, EquipmentCategory category, int stock) {
    if (equipmentRepository.existsByName(name)) {
      throw new DuplicateEquipmentException("Equipment with name '" + name + "' already exists");
    }
    Equipment equipment = new Equipment(name, category, EquipmentStatus.CATALOGUED, stock);
    return equipmentRepository.save(equipment);
  }

  @Transactional
  public Equipment adjustStock(Long id, int delta) {
    if (delta == 0) {
      throw new InvalidStockAdjustmentException("Stock adjustment delta must not be zero");
    }

    // Pessimistic lock: needed for negative adjustments to race against concurrent reservations.
    // Positive adjustments are a safe atomic increment, but we still need the lock to read the
    // current stock for validation in the negative path. Since the method handles both directions,
    // we always lock.
    Equipment equipment =
        equipmentRepository
            .findByIdForUpdate(id)
            .orElseThrow(() -> new EntityNotFoundException("Equipment not found: " + id));

    int currentStock = equipment.getStock();
    int newStock = currentStock + delta;

    if (newStock < 0) {
      throw new InvalidStockAdjustmentException(
          String.format(
              "Cannot adjust stock: resulting stock would be negative (current=%d, delta=%d)",
              currentStock, delta));
    }

    if (delta < 0) {
      int peakReserved = computePeakReservedQuantity(id);
      if (newStock < peakReserved) {
        throw new InsufficientStockException(
            String.format(
                "Cannot reduce stock for equipment '%s': resulting stock %d would be below"
                    + " %d units committed to active or future reservations",
                equipment.getName(), newStock, peakReserved));
      }
    }

    equipmentRepository.updateStock(id, newStock);
    return equipmentRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Equipment not found: " + id));
  }

  @Transactional
  public Equipment decatalogue(Long id) {
    Equipment equipment =
        equipmentRepository
            .findByIdForUpdate(id)
            .orElseThrow(() -> new EntityNotFoundException("Equipment not found: " + id));

    if (equipment.getStatus() == EquipmentStatus.DECATALOGUED) {
      throw new EquipmentStateException(
          String.format("Equipment '%s' is already decatalogued", equipment.getName()));
    }

    List<ReservationLine> affectedLines =
        reservationRepository.findAffectedLinesByEquipment(id, LocalDateTime.now());
    if (!affectedLines.isEmpty()) {
      throw new InsufficientStockException(
          String.format(
              "Cannot decatalogue equipment '%s': it is committed to %d active or future reservation(s)",
              equipment.getName(), countDistinctReservations(affectedLines)));
    }

    equipmentRepository.updateStatusAndStock(id, EquipmentStatus.DECATALOGUED, 0);
    return equipmentRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Equipment not found: " + id));
  }

  @Transactional
  public Equipment recatalogue(Long id) {
    Equipment equipment =
        equipmentRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Equipment not found: " + id));

    if (equipment.getStatus() == EquipmentStatus.CATALOGUED) {
      throw new EquipmentStateException(
          String.format("Equipment '%s' is already catalogued", equipment.getName()));
    }

    equipmentRepository.updateStatusAndStock(id, EquipmentStatus.CATALOGUED, equipment.getStock());
    return equipmentRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Equipment not found: " + id));
  }

  /**
   * Computes the peak concurrent reserved quantity across all affected reservations for the given
   * equipment. An affected reservation is one whose event has not finished yet (endDate > now).
   *
   * <p>Uses a sweep-line algorithm over the open-interval overlap condition (startDate < t &&
   * endDate > t), consistent with the JPQL overlap logic in ReservationRepository.
   */
  int computePeakReservedQuantity(Long equipmentId) {
    List<ReservationLine> affectedLines =
        reservationRepository.findAffectedLinesByEquipment(equipmentId, LocalDateTime.now());

    if (affectedLines.isEmpty()) {
      return 0;
    }

    // Build sweep events: (time, delta). Positive delta = reservation starts, negative = ends.
    List<SweepEvent> events = new ArrayList<>();
    for (ReservationLine line : affectedLines) {
      LocalDateTime startDate = line.getReservation().getEvent().getStartDate();
      LocalDateTime endDate = line.getReservation().getEvent().getEndDate();
      events.add(new SweepEvent(startDate, line.getQuantity()));
      events.add(new SweepEvent(endDate, -line.getQuantity()));
    }

    // Sort by time; at the same time, process ends (negative delta) before starts (positive delta)
    // to respect open-interval semantics: if A ends at t and B starts at t, they don't overlap,
    // so at time t the running total should reflect A ended before B starts.
    events.sort(Comparator.comparing(SweepEvent::time).thenComparingInt(SweepEvent::delta));

    int running = 0;
    int peak = 0;
    for (SweepEvent event : events) {
      running += event.delta();
      peak = Math.max(peak, running);
    }
    return peak;
  }

  private static int countDistinctReservations(List<ReservationLine> lines) {
    return (int) lines.stream().map(rl -> rl.getReservation().getId()).distinct().count();
  }

  private record SweepEvent(LocalDateTime time, int delta) {}
}
