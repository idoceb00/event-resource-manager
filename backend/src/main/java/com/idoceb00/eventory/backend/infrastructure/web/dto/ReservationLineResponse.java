package com.idoceb00.eventory.backend.infrastructure.web.dto;

import com.idoceb00.eventory.backend.domain.model.ReservationLine;

public record ReservationLineResponse(Long id, EquipmentResponse equipment, int quantity) {

  public static ReservationLineResponse fromEntity(ReservationLine line) {
    return new ReservationLineResponse(
        line.getId(), EquipmentResponse.fromEntity(line.getEquipment()), line.getQuantity());
  }
}
