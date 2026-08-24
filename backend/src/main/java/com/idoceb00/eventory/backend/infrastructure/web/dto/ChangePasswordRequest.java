package com.idoceb00.eventory.backend.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(@NotBlank String newPassword) {}
