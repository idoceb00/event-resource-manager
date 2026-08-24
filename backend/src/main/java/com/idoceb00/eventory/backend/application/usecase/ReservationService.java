package com.idoceb00.eventory.backend.application.usecase;

import com.idoceb00.eventory.backend.domain.model.Equipment;
import com.idoceb00.eventory.backend.domain.model.EquipmentStatus;
import com.idoceb00.eventory.backend.domain.model.Event;
import com.idoceb00.eventory.backend.domain.model.Reservation;
import com.idoceb00.eventory.backend.domain.model.ReservationLine;
import com.idoceb00.eventory.backend.domain.model.User;
import com.idoceb00.eventory.backend.domain.service.DuplicateReservationException;
import com.idoceb00.eventory.backend.domain.service.EntityNotFoundException;
import com.idoceb00.eventory.backend.domain.service.InsufficientStockException;
import com.idoceb00.eventory.backend.infrastructure.persistence.EquipmentRepository;
import com.idoceb00.eventory.backend.infrastructure.persistence.EventRepository;
import com.idoceb00.eventory.backend.infrastructure.persistence.ReservationLineRepository;
import com.idoceb00.eventory.backend.infrastructure.persistence.ReservationRepository;
import com.idoceb00.eventory.backend.infrastructure.persistence.UserRepository;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final ReservationLineRepository reservationLineRepository;
  private final EventRepository eventRepository;
  private final EquipmentRepository equipmentRepository;
  private final UserRepository userRepository;

  public ReservationService(
      ReservationRepository reservationRepository,
      ReservationLineRepository reservationLineRepository,
      EventRepository eventRepository,
      EquipmentRepository equipmentRepository,
      UserRepository userRepository) {
    this.reservationRepository = reservationRepository;
    this.reservationLineRepository = reservationLineRepository;
    this.eventRepository = eventRepository;
    this.equipmentRepository = equipmentRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public Reservation reserveEquipment(Long eventId, Long equipmentId, int quantity) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

    Event event =
        eventRepository
            .findById(eventId)
            .orElseThrow(() -> new EntityNotFoundException("Event not found: " + eventId));

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

    if (reservationRepository.existsLineByUserAndEquipmentAndReservation(
        user.getId(), equipmentId, reservation.getId() != null ? reservation.getId() : 0L)) {
      throw new DuplicateReservationException(
          String.format(
              "User '%s' already has a reservation for equipment '%s' on this event",
              user.getName() != null ? user.getName() : user.getUsername(), equipment.getName()));
    }

    ReservationLine line = new ReservationLine(equipment, user, quantity);
    reservation.addLine(line);

    return reservationRepository.save(reservation);
  }

  @Transactional
  public void deleteLine(Long lineId) {
    ReservationLine line =
        reservationLineRepository
            .findById(lineId)
            .orElseThrow(
                () -> new EntityNotFoundException("Reservation line not found: " + lineId));
    Reservation reservation = line.getReservation();
    reservation.removeLine(line);
    if (reservation.getLines().isEmpty()) {
      reservationRepository.delete(reservation);
    } else {
      reservationRepository.save(reservation);
    }
  }
}
