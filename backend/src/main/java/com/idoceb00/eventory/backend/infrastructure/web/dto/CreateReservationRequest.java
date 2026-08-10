package com.idoceb00.eventory.backend.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateReservationRequest(
    @NotNull Long eventId, @NotNull Long equipmentId, @Positive int quantity) {}
