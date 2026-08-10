package com.idoceb00.eventory.backend.infrastructure.web.dto;

import com.idoceb00.eventory.backend.domain.model.Reservation;
import java.util.List;

public record ReservationResponse(
    Long id, EventResponse event, List<ReservationLineResponse> lines) {

  public static ReservationResponse fromEntity(Reservation reservation) {
    List<ReservationLineResponse> lines =
        reservation.getLines().stream().map(ReservationLineResponse::fromEntity).toList();
    return new ReservationResponse(
        reservation.getId(), EventResponse.fromEntity(reservation.getEvent()), lines);
  }
}
