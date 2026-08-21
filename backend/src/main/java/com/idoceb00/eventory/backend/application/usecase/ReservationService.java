package com.idoceb00.eventory.backend.application.usecase;

import com.idoceb00.eventory.backend.domain.model.Equipment;
import com.idoceb00.eventory.backend.domain.model.EquipmentStatus;
import com.idoceb00.eventory.backend.domain.model.Event;
import com.idoceb00.eventory.backend.domain.model.Reservation;
import com.idoceb00.eventory.backend.domain.model.ReservationLine;
import com.idoceb00.eventory.backend.domain.service.EntityNotFoundException;
import com.idoceb00.eventory.backend.domain.service.InsufficientStockException;
import com.idoceb00.eventory.backend.infrastructure.persistence.EquipmentRepository;
import com.idoceb00.eventory.backend.infrastructure.persistence.EventRepository;
import com.idoceb00.eventory.backend.infrastructure.persistence.ReservationRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final EventRepository eventRepository;
  private final EquipmentRepository equipmentRepository;

  public ReservationService(
      ReservationRepository reservationRepository,
      EventRepository eventRepository,
      EquipmentRepository equipmentRepository) {
    this.reservationRepository = reservationRepository;
    this.eventRepository = eventRepository;
    this.equipmentRepository = equipmentRepository;
  }

  @Transactional
  public Reservation reserveEquipment(Long eventId, Long equipmentId, int quantity) {
    Event event =
        eventRepository
            .findById(eventId)
            .orElseThrow(() -> new EntityNotFoundException("Event not found: " + eventId));

    // Pessimistic lock: serializes access to the equipment row to prevent
    // overbooking when two concurrent transactions read the same available stock.
    Equipment equipment =
        equipmentRepository
            .findByIdForUpdate(equipmentId)
            .orElseThrow(() -> new EntityNotFoundException("Equipment not found: " + equipmentId));

    if (equipment.getStatus() == EquipmentStatus.DECATALOGUED) {
      throw new InsufficientStockException(
          String.format(
              "Cannot reserve equipment '%s': equipment is decatalogued", equipment.getName()));
    }

    int alreadyReserved =
        reservationRepository.sumReservedQuantityByEquipmentAndOverlappingDates(
            equipmentId, event.getStartDate(), event.getEndDate());

    int available = equipment.getStock() - alreadyReserved;

    if (available < quantity) {
      throw new InsufficientStockException(
          String.format(
              "Insufficient stock for equipment '%s': requested %d, available %d (stock=%d, already reserved=%d)",
              equipment.getName(), quantity, available, equipment.getStock(), alreadyReserved));
    }

    Optional<Reservation> existingReservation = reservationRepository.findByEvent(event);

    Reservation reservation;
    if (existingReservation.isPresent()) {
      reservation = existingReservation.get();
    } else {
      reservation = new Reservation(event);
    }

    ReservationLine line = new ReservationLine(equipment, quantity);
    reservation.addLine(line);

    return reservationRepository.save(reservation);
  }
}
