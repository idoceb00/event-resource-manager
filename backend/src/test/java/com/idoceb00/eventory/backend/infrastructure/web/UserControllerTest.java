package com.idoceb00.eventory.backend.infrastructure.web;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.idoceb00.eventory.backend.domain.model.User;
import com.idoceb00.eventory.backend.domain.model.UserRole;
import com.idoceb00.eventory.backend.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class UserControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.save(new User("admin", "hash123", UserRole.ADMINISTRATOR));
    userRepository.save(new User("employee1", "hash456", UserRole.EMPLOYEE));
  }

  @Test
  void findAll_returnsAllUsers_withoutPasswordHash() throws Exception {
    mockMvc
        .perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].username").value("admin"))
        .andExpect(jsonPath("$[0].role").value("ADMINISTRATOR"))
        .andExpect(jsonPath("$[0].active").value(true))
        .andExpect(jsonPath("$[0].passwordHash").doesNotExist());
  }

  @Test
  void findById_existingId_returnsUser() throws Exception {
    User saved = userRepository.findByUsername("admin").orElseThrow();
    mockMvc
        .perform(get("/api/users/" + saved.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("admin"))
        .andExpect(jsonPath("$.role").value("ADMINISTRATOR"))
        .andExpect(jsonPath("$.passwordHash").doesNotExist());
  }

  @Test
  void findById_nonExistingId_returns404() throws Exception {
    mockMvc.perform(get("/api/users/999")).andExpect(status().isNotFound());
  }
}
