package com.idoceb00.eventory.backend.infrastructure.seeding;

import com.idoceb00.eventory.backend.domain.model.User;
import com.idoceb00.eventory.backend.domain.model.UserRole;
import com.idoceb00.eventory.backend.infrastructure.persistence.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevUserSeeder implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(DevUserSeeder.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.seed.admin-username:admin}")
  private String adminUsername;

  @Value("${app.seed.admin-password:admin}")
  private String adminPassword;

  @Value("${app.seed.admin-display-name:Admin}")
  private String adminDisplayName;

  public DevUserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) {
    if (userRepository.count() > 0) {
      log.info("Users already exist — skipping dev seeder.");
      return;
    }

    User admin =
        new User(
            adminUsername,
            adminDisplayName,
            passwordEncoder.encode(adminPassword),
            UserRole.ADMINISTRATOR);
    admin.setActive(true);
    userRepository.save(admin);

    log.warn("====================================================");
    log.warn("DEV SEEDER: Created default administrator account");
    log.warn("  Username : {}", adminUsername);
    log.warn("  Password : {}", adminPassword);
    log.warn("  Role     : ADMINISTRATOR");
    log.warn("These are DEVELOPMENT-ONLY credentials — do NOT use in production!");
    log.warn("====================================================");
  }
}
