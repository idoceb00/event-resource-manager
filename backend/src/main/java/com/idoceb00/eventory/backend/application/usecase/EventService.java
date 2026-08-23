package com.idoceb00.eventory.backend.application.usecase;

import com.idoceb00.eventory.backend.domain.model.Equipment;
import com.idoceb00.eventory.backend.domain.model.Event;
import com.idoceb00.eventory.backend.domain.service.EntityNotFoundException;
import com.idoceb00.eventory.backend.domain.service.InsufficientStockException;
import com.idoceb00.eventory.backend.infrastructure.persistence.EquipmentRepository;
import com.idoceb00.eventory.backend.infrastructure.persistence.EventRepository;
import com.idoceb00.eventory.backend.infrastructure.persistence.ReservationRepository;
import com.idoceb00.eventory.backend.infrastructure.web.dto.UpdateEventRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {

  private final EventRepository eventRepository;
  private final ReservationRepository reservationRepository;
  private final EquipmentRepository equipmentRepository;

  public EventService(
      EventRepository eventRepository,
      ReservationRepository reservationRepository,
      EquipmentRepository equipmentRepository) {
    this.eventRepository = eventRepository;
    this.reservationRepository = reservationRepository;
    this.equipmentRepository = equipmentRepository;
  }

  @Transactional
  public Event updateEvent(Long id, UpdateEventRequest request) {
    Event event =
        eventRepository
            .findByIdForUpdate(id)
            .orElseThrow(() -> new EntityNotFoundException("Event not found: " + id));

    boolean datesChanged = false;
    LocalDateTime newStartDate = event.getStartDate();
    LocalDateTime newEndDate = event.getEndDate();

    if (request.startDate() != null) {
      newStartDate = request.startDate();
      datesChanged = true;
    }
    if (request.endDate() != null) {
      newEndDate = request.endDate();
      datesChanged = true;
    }

    if (datesChanged && newStartDate.isAfter(newEndDate)) {
      throw new IllegalArgumentException("Event end date must be after start date");
    }

    if (datesChanged) {
      List<Long> equipmentIds = reservationRepository.findDistinctEquipmentIdsByEventId(id);

      for (Long equipmentId : equipmentIds) {
        Equipment equipment =
            equipmentRepository
                .findById(equipmentId)
                .orElseThrow(
                    () -> new EntityNotFoundException("Equipment not found: " + equipmentId));

        int reservedByOthers =
            reservationRepository.sumReservedQuantityByEquipmentAndOverlappingDates(
                equipmentId, newStartDate, newEndDate);

        if (reservedByOthers > equipment.getStock()) {
          throw new InsufficientStockException(
              String.format(
                  "Cannot update event dates: equipment '%s' would be over-committed "
                      + "(stock=%d, reserved by other events in the new window=%d)",
                  equipment.getName(), equipment.getStock(), reservedByOthers));
        }
      }
    }

    if (request.name() != null) event.setName(request.name());
    if (request.startDate() != null) event.setStartDate(request.startDate());
    if (request.endDate() != null) event.setEndDate(request.endDate());
    event.setAddress(request.address());
    if (request.transport() != null) event.setTransport(request.transport());
    event.setExtraInfo(request.extraInfo());

    return eventRepository.save(event);
  }
}
