package com.idoceb00.eventory.backend.infrastructure.web.dto;

import com.idoceb00.eventory.backend.domain.model.UserRole;

public record AuthResponse(Long id, String username, UserRole role) {}
