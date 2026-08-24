package com.idoceb00.eventory.backend.infrastructure.web;

import com.idoceb00.eventory.backend.domain.model.User;
import com.idoceb00.eventory.backend.domain.service.EntityNotFoundException;
import com.idoceb00.eventory.backend.domain.service.InsufficientStockException;
import com.idoceb00.eventory.backend.infrastructure.persistence.ReservationRepository;
import com.idoceb00.eventory.backend.infrastructure.persistence.UserRepository;
import com.idoceb00.eventory.backend.infrastructure.web.dto.CreateUserRequest;
import com.idoceb00.eventory.backend.infrastructure.web.dto.UpdateUserRequest;
import com.idoceb00.eventory.backend.infrastructure.web.dto.UserResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final ReservationRepository reservationRepository;
  private final PasswordEncoder passwordEncoder;

  public UserController(
      UserRepository userRepository,
      ReservationRepository reservationRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.reservationRepository = reservationRepository;
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
    if (request.role() != null) user.setRole(request.role());
    if (request.active() != null) user.setActive(request.active());

    User saved = userRepository.save(user);
    return UserResponse.fromEntity(saved);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

    if (reservationRepository.existsLineByUserId(id)) {
      throw new InsufficientStockException(
          String.format(
              "Cannot delete user '%s': they have active reservation lines",
              user.getName() != null ? user.getName() : user.getUsername()));
    }

    userRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
