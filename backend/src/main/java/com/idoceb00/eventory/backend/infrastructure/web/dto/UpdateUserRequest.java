package com.idoceb00.eventory.backend.infrastructure.web.dto;

import com.idoceb00.eventory.backend.domain.model.UserRole;

public record UpdateUserRequest(String name, UserRole role, Boolean active) {}
