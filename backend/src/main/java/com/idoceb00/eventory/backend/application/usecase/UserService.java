package com.idoceb00.eventory.backend.application.usecase;

import com.idoceb00.eventory.backend.domain.model.User;
import com.idoceb00.eventory.backend.domain.model.UserRole;
import com.idoceb00.eventory.backend.domain.service.DuplicateReservationException;
import com.idoceb00.eventory.backend.domain.service.EntityNotFoundException;
import com.idoceb00.eventory.backend.infrastructure.persistence.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public User createUser(
      String username, String name, String password, UserRole role, boolean active) {
    if (userRepository.findByUsername(username).isPresent()) {
      throw new DuplicateReservationException(
          String.format("Username '%s' already exists", username));
    }

    User user = new User(username, name, passwordEncoder.encode(password), role);
    user.setActive(active);
    return userRepository.save(user);
  }

  @Transactional
  public User updateUser(Long id, String name, UserRole role) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

    if (name != null) user.setName(name);

    if (role != null) {
      if (role != user.getRole()
          && user.getRole() == UserRole.ADMINISTRATOR
          && role == UserRole.EMPLOYEE) {
        long adminCount = userRepository.countByRoleAndActive(UserRole.ADMINISTRATOR, true);
        if (adminCount <= 1) {
          throw new DuplicateReservationException("Cannot demote the last active administrator");
        }
      }
      user.setRole(role);
    }

    return userRepository.save(user);
  }

  @Transactional
  public void changePassword(Long id, String newPassword) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

    user.setPasswordHash(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  @Transactional
  public void setActivation(
      Long id, boolean active, String confirmPassword, String actingUsername) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

    if (!active && user.getRole() == UserRole.ADMINISTRATOR) {
      if (confirmPassword == null || confirmPassword.isBlank()) {
        throw new DuplicateReservationException(
            "Re-authentication required to deactivate an administrator");
      }
      User actingUser =
          userRepository
              .findByUsername(actingUsername)
              .orElseThrow(
                  () -> new EntityNotFoundException("Acting user not found: " + actingUsername));
      if (!passwordEncoder.matches(confirmPassword, actingUser.getPasswordHash())) {
        throw new DuplicateReservationException("Incorrect password for re-authentication");
      }
    }

    if (!active) {
      long adminCount = userRepository.countByRoleAndActive(UserRole.ADMINISTRATOR, true);
      if (adminCount <= 1 && user.getRole() == UserRole.ADMINISTRATOR && user.isActive()) {
        throw new DuplicateReservationException("Cannot deactivate the last active administrator");
      }
    }

    user.setActive(active);
    userRepository.save(user);
  }
}
