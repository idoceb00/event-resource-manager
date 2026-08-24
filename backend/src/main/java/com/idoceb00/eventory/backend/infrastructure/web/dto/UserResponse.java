package com.idoceb00.eventory.backend.infrastructure.web.dto;

import com.idoceb00.eventory.backend.domain.model.User;
import com.idoceb00.eventory.backend.domain.model.UserRole;

public record UserResponse(Long id, String username, String name, UserRole role, boolean active) {

  public static UserResponse fromEntity(User user) {
    return new UserResponse(
        user.getId(), user.getUsername(), user.getName(), user.getRole(), user.isActive());
  }
}
