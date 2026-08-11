package com.idoceb00.eventory.backend.infrastructure.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record CreatePerformanceRequest(
    @NotBlank String name,
    @NotNull LocalTime startTime,
    @Min(1) long duration,
    @NotNull LocalTime rehearsalTime) {}
