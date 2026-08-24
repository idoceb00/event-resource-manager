package com.idoceb00.eventory.backend.infrastructure.web;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.idoceb00.eventory.backend.domain.model.Equipment;
import com.idoceb00.eventory.backend.domain.model.EquipmentCategory;
import com.idoceb00.eventory.backend.domain.model.EquipmentStatus;
import com.idoceb00.eventory.backend.domain.model.Event;
import com.idoceb00.eventory.backend.domain.model.User;
import com.idoceb00.eventory.backend.domain.model.UserRole;
import com.idoceb00.eventory.backend.infrastructure.persistence.EquipmentRepository;
import com.idoceb00.eventory.backend.infrastructure.persistence.EventRepository;
import com.idoceb00.eventory.backend.infrastructure.persistence.UserRepository;
import com.idoceb00.eventory.backend.infrastructure.web.dto.CreateReservationRequest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class ReservationControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private EventRepository eventRepository;
  @Autowired private EquipmentRepository equipmentRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private ObjectMapper objectMapper;

  private Event event;
  private Equipment speakers;
  private User user;

  @BeforeEach
  void setUp() {
    event =
        eventRepository.save(
            new Event(
                "Concert",
                LocalDateTime.of(2026, 8, 1, 10, 0),
                LocalDateTime.of(2026, 8, 3, 22, 0)));
    speakers =
        equipmentRepository.save(
            new Equipment("Speakers", EquipmentCategory.SOUND, EquipmentStatus.CATALOGUED, 5));
    user = userRepository.save(new User("carlos", "Carlos Ramirez", "hash123", UserRole.EMPLOYEE));

    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                "carlos", null, java.util.List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))));
  }

  @Test
  void create_validReservation_returns201() throws Exception {
    CreateReservationRequest request =
        new CreateReservationRequest(event.getId(), speakers.getId(), 3);

    mockMvc
        .perform(
            post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.event.name").value("Concert"))
        .andExpect(jsonPath("$.lines").isArray())
        .andExpect(jsonPath("$.lines", hasSize(1)))
        .andExpect(jsonPath("$.lines[0].equipment.name").value("Speakers"))
        .andExpect(jsonPath("$.lines[0].author.username").value("carlos"))
        .andExpect(jsonPath("$.lines[0].quantity").value(3));
  }

  @Test
  void create_insufficientStock_returns409() throws Exception {
    CreateReservationRequest request =
        new CreateReservationRequest(event.getId(), speakers.getId(), 10);

    mockMvc
        .perform(
            post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  void create_nonExistingEvent_returns404() throws Exception {
    CreateReservationRequest request = new CreateReservationRequest(999L, speakers.getId(), 1);

    mockMvc
        .perform(
            post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void create_nonExistingEquipment_returns404() throws Exception {
    CreateReservationRequest request = new CreateReservationRequest(event.getId(), 999L, 1);

    mockMvc
        .perform(
            post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void findAll_returnsAllReservations() throws Exception {
    mockMvc
        .perform(get("/api/reservations"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(0)));
  }
}
