package com.idoceb00.eventory.backend.infrastructure.web;

import com.idoceb00.eventory.backend.domain.model.User;
import com.idoceb00.eventory.backend.domain.model.UserRole;
import com.idoceb00.eventory.backend.domain.service.DuplicateReservationException;
import com.idoceb00.eventory.backend.domain.service.EntityNotFoundException;
import com.idoceb00.eventory.backend.infrastructure.persistence.UserRepository;
import com.idoceb00.eventory.backend.infrastructure.web.dto.ActivationRequest;
import com.idoceb00.eventory.backend.infrastructure.web.dto.ChangePasswordRequest;
import com.idoceb00.eventory.backend.infrastructure.web.dto.CreateUserRequest;
import com.idoceb00.eventory.backend.infrastructure.web.dto.UpdateUserRequest;
import com.idoceb00.eventory.backend.infrastructure.web.dto.UserResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping
  public List<UserResponse> findAll() {
    return userRepository.findAll().stream().map(UserResponse::fromEntity).toList();
  }

  @GetMapping("/{id}")
  public UserResponse findById(@PathVariable Long id) {
    return userRepository
        .findById(id)
        .map(UserResponse::fromEntity)
        .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
  }

  @PostMapping
  public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
    if (userRepository.findByUsername(request.username()).isPresent()) {
      throw new DuplicateReservationException(
          String.format("Username '%s' already exists", request.username()));
    }

    User user =
        new User(
            request.username(),
            request.name(),
            passwordEncoder.encode(request.password()),
            request.role());
    user.setActive(request.active());
    User saved = userRepository.save(user);
    return ResponseEntity.created(URI.create("/api/users/" + saved.getId()))
        .body(UserResponse.fromEntity(saved));
  }

  @PutMapping("/{id}")
  public UserResponse update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

    if (request.name() != null) user.setName(request.name());

    if (request.role() != null) {
      if (request.role() != user.getRole()
          && user.getRole() == UserRole.ADMINISTRATOR
          && request.role() == UserRole.EMPLOYEE) {
        long adminCount = userRepository.countByRoleAndActive(UserRole.ADMINISTRATOR, true);
        if (adminCount <= 1) {
          throw new DuplicateReservationException("Cannot demote the last active administrator");
        }
      }
      user.setRole(request.role());
    }

    User saved = userRepository.save(user);
    return UserResponse.fromEntity(saved);
  }

  @PatchMapping("/{id}/password")
  public ResponseEntity<Void> changePassword(
      @PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

    user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    userRepository.save(user);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/activation")
  public ResponseEntity<Void> setActivation(
      @PathVariable Long id, @Valid @RequestBody ActivationRequest request) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

    if (!request.active() && user.getRole() == UserRole.ADMINISTRATOR) {
      if (request.confirmPassword() == null || request.confirmPassword().isBlank()) {
        throw new DuplicateReservationException(
            "Re-authentication required to deactivate an administrator");
      }
      String actingUsername = SecurityContextHolder.getContext().getAuthentication().getName();
      User actingUser =
          userRepository
              .findByUsername(actingUsername)
              .orElseThrow(
                  () -> new EntityNotFoundException("Acting user not found: " + actingUsername));
      if (!passwordEncoder.matches(request.confirmPassword(), actingUser.getPasswordHash())) {
        throw new DuplicateReservationException("Incorrect password for re-authentication");
      }
    }

    if (!request.active()) {
      long adminCount = userRepository.countByRoleAndActive(UserRole.ADMINISTRATOR, true);
      if (adminCount <= 1 && user.getRole() == UserRole.ADMINISTRATOR && user.isActive()) {
        throw new DuplicateReservationException("Cannot deactivate the last active administrator");
      }
    }

    user.setActive(request.active());
    userRepository.save(user);
    return ResponseEntity.noContent().build();
  }
}
