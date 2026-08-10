package com.idoceb00.eventory.backend.infrastructure.web;

import com.idoceb00.eventory.backend.domain.service.EntityNotFoundException;
import com.idoceb00.eventory.backend.infrastructure.persistence.UserRepository;
import com.idoceb00.eventory.backend.infrastructure.web.dto.UserResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
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
}
