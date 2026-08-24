package com.idoceb00.eventory.backend.infrastructure.web.dto;

import com.idoceb00.eventory.backend.domain.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
    @NotBlank String username,
    String name,
    @NotBlank String password,
    @NotNull UserRole role,
    boolean active) {}
