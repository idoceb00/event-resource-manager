package com.idoceb00.eventory.backend.infrastructure.web.dto;

import com.idoceb00.eventory.backend.domain.model.Equipment;
import com.idoceb00.eventory.backend.domain.model.EquipmentCategory;
import com.idoceb00.eventory.backend.domain.model.EquipmentStatus;

public record EquipmentResponse(
    Long id, String name, EquipmentCategory category, EquipmentStatus status, int stock) {

  public static EquipmentResponse fromEntity(Equipment equipment) {
    return new EquipmentResponse(
        equipment.getId(),
        equipment.getName(),
        equipment.getCategory(),
        equipment.getStatus(),
        equipment.getStock());
  }
}
