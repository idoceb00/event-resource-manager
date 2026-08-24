package com.idoceb00.eventory.backend.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

public record ActivationRequest(@NotNull boolean active, String confirmPassword) {}
