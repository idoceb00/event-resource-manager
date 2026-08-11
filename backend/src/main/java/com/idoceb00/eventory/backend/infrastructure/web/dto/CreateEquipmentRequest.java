package com.idoceb00.eventory.backend.infrastructure.web.dto;

import com.idoceb00.eventory.backend.domain.model.EquipmentCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateEquipmentRequest(
    @NotBlank String name, @NotNull EquipmentCategory category, @PositiveOrZero int stock) {}
