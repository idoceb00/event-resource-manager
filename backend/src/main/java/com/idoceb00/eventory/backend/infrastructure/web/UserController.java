package com.idoceb00.eventory.backend.infrastructure.web;

import com.idoceb00.eventory.backend.application.usecase.UserService;
import com.idoceb00.eventory.backend.domain.model.User;
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
  private final UserService userService;

  public UserController(UserRepository userRepository, UserService userService) {
    this.userRepository = userRepository;
    this.userService = userService;
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
    User saved =
        userService.createUser(
            request.username(),
            request.name(),
            request.password(),
            request.role(),
            request.active());
    return ResponseEntity.created(URI.create("/api/users/" + saved.getId()))
        .body(UserResponse.fromEntity(saved));
  }

  @PutMapping("/{id}")
  public UserResponse update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
    User saved = userService.updateUser(id, request.name(), request.role());
    return UserResponse.fromEntity(saved);
  }

  @PatchMapping("/{id}/password")
  public ResponseEntity<Void> changePassword(
      @PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
    userService.changePassword(id, request.newPassword());
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/activation")
  public ResponseEntity<Void> setActivation(
      @PathVariable Long id, @Valid @RequestBody ActivationRequest request) {
    String actingUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    userService.setActivation(id, request.active(), request.confirmPassword(), actingUsername);
    return ResponseEntity.noContent().build();
  }
}
