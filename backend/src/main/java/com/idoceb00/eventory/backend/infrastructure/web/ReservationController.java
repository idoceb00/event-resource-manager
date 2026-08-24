package com.idoceb00.eventory.backend.infrastructure.web;

import com.idoceb00.eventory.backend.application.usecase.ReservationService;
import com.idoceb00.eventory.backend.domain.model.Reservation;
import com.idoceb00.eventory.backend.infrastructure.persistence.ReservationRepository;
import com.idoceb00.eventory.backend.infrastructure.web.dto.CreateReservationRequest;
import com.idoceb00.eventory.backend.infrastructure.web.dto.ReservationResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

  private final ReservationService reservationService;
  private final ReservationRepository reservationRepository;

  public ReservationController(
      ReservationService reservationService, ReservationRepository reservationRepository) {
    this.reservationService = reservationService;
    this.reservationRepository = reservationRepository;
  }

  @PostMapping
  public ResponseEntity<ReservationResponse> create(
      @Valid @RequestBody CreateReservationRequest request) {
    Reservation reservation =
        reservationService.reserveEquipment(
            request.eventId(), request.equipmentId(), request.quantity());
    return ResponseEntity.created(URI.create("/api/reservations/" + reservation.getId()))
        .body(ReservationResponse.fromEntity(reservation));
  }

  @GetMapping
  public List<ReservationResponse> findAll() {
    return reservationRepository.findAllWithLines().stream()
        .map(ReservationResponse::fromEntity)
        .toList();
  }

  @DeleteMapping("/lines/{lineId}")
  public ResponseEntity<Void> deleteLine(@PathVariable Long lineId) {
    reservationService.deleteLine(lineId);
    return ResponseEntity.noContent().build();
  }
}
