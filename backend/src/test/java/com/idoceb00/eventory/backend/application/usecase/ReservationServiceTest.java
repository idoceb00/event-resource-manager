package com.idoceb00.eventory.backend.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.idoceb00.eventory.backend.domain.model.Equipment;
import com.idoceb00.eventory.backend.domain.model.EquipmentCategory;
import com.idoceb00.eventory.backend.domain.model.EquipmentStatus;
import com.idoceb00.eventory.backend.domain.model.Event;
import com.idoceb00.eventory.backend.domain.model.Reservation;
import com.idoceb00.eventory.backend.domain.service.EntityNotFoundException;
import com.idoceb00.eventory.backend.domain.service.InsufficientStockException;
import com.idoceb00.eventory.backend.infrastructure.persistence.EquipmentRepository;
import com.idoceb00.eventory.backend.infrastructure.persistence.EventRepository;
import com.idoceb00.eventory.backend.infrastructure.persistence.ReservationRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ReservationServiceTest {

  @Autowired private ReservationService reservationService;
  @Autowired private EventRepository eventRepository;
  @Autowired private EquipmentRepository equipmentRepository;
  @Autowired private ReservationRepository reservationRepository;

  private Event eventA;
  private Event eventB;
  private Equipment speakers;

  @BeforeEach
  void setUp() {
    eventA =
        eventRepository.save(
            new Event(
                "Concert A",
                LocalDateTime.of(2026, 7, 1, 10, 0),
                LocalDateTime.of(2026, 7, 3, 22, 0)));

    eventB =
        eventRepository.save(
            new Event(
                "Concert B",
                LocalDateTime.of(2026, 7, 10, 10, 0),
                LocalDateTime.of(2026, 7, 12, 22, 0)));

    speakers =
        equipmentRepository.save(
            new Equipment("Speakers", EquipmentCategory.SOUND, EquipmentStatus.CATALOGUED, 5));
  }

  @Test
  void reserveEquipment_success() {
    Reservation reservation =
        reservationService.reserveEquipment(eventA.getId(), speakers.getId(), 3);

    assertThat(reservation).isNotNull();
    assertThat(reservation.getEvent()).isEqualTo(eventA);
    assertThat(reservation.getLines()).hasSize(1);
    assertThat(reservation.getLines().get(0).getEquipment()).isEqualTo(speakers);
    assertThat(reservation.getLines().get(0).getQuantity()).isEqualTo(3);
  }

  @Test
  void reserveEquipment_insufficientStock_overlappingEvents() {
    reservationService.reserveEquipment(eventA.getId(), speakers.getId(), 4);

    assertThatThrownBy(
            () -> reservationService.reserveEquipment(eventA.getId(), speakers.getId(), 2))
        .isInstanceOf(InsufficientStockException.class)
        .hasMessageContaining("Insufficient stock");
  }

  @Test
  void reserveEquipment_allowed_whenEventsDoNotOverlap() {
    reservationService.reserveEquipment(eventA.getId(), speakers.getId(), 5);

    Reservation reservation =
        reservationService.reserveEquipment(eventB.getId(), speakers.getId(), 5);

    assertThat(reservation).isNotNull();
    assertThat(reservation.getEvent()).isEqualTo(eventB);
    assertThat(reservation.getLines()).hasSize(1);
    assertThat(reservation.getLines().get(0).getQuantity()).isEqualTo(5);
  }

  @Test
  void reserveEquipment_eventNotFound() {
    assertThatThrownBy(() -> reservationService.reserveEquipment(999L, speakers.getId(), 1))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Event not found");
  }

  @Test
  void reserveEquipment_equipmentNotFound() {
    assertThatThrownBy(() -> reservationService.reserveEquipment(eventA.getId(), 999L, 1))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Equipment not found");
  }

  @Test
  void reserveEquipment_addsLineToExistingReservation() {
    reservationService.reserveEquipment(eventA.getId(), speakers.getId(), 2);
    Reservation updated = reservationService.reserveEquipment(eventA.getId(), speakers.getId(), 1);

    assertThat(updated.getLines()).hasSize(2);
    int totalQuantity = updated.getLines().stream().mapToInt(l -> l.getQuantity()).sum();
    assertThat(totalQuantity).isEqualTo(3);
  }
}
