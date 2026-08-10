package com.idoceb00.eventory.backend.infrastructure.web;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.idoceb00.eventory.backend.domain.model.Event;
import com.idoceb00.eventory.backend.infrastructure.persistence.EventRepository;
import com.idoceb00.eventory.backend.infrastructure.web.dto.CreateEventRequest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EventControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private EventRepository eventRepository;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    eventRepository.save(
        new Event(
            "Concert",
            LocalDateTime.of(2026, 8, 1, 10, 0),
            LocalDateTime.of(2026, 8, 3, 22, 0),
            "Main Hall",
            true,
            "VIP area available"));
  }

  @Test
  void findAll_returnsAllEvents() throws Exception {
    mockMvc
        .perform(get("/api/events"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void findById_existingId_returnsEvent() throws Exception {
    Event saved = eventRepository.findAll().getFirst();
    mockMvc
        .perform(get("/api/events/" + saved.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Concert"))
        .andExpect(jsonPath("$.address").value("Main Hall"))
        .andExpect(jsonPath("$.transport").value(true))
        .andExpect(jsonPath("$.extraInfo").value("VIP area available"))
        .andExpect(jsonPath("$.performances").isArray());
  }

  @Test
  void findById_nonExistingId_returns404() throws Exception {
    mockMvc.perform(get("/api/events/999")).andExpect(status().isNotFound());
  }

  @Test
  void create_validRequest_returns201() throws Exception {
    CreateEventRequest request =
        new CreateEventRequest(
            "Festival",
            LocalDateTime.of(2026, 9, 1, 9, 0),
            LocalDateTime.of(2026, 9, 5, 23, 0),
            "Open Air",
            false,
            null);

    mockMvc
        .perform(
            post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Festival"))
        .andExpect(jsonPath("$.address").value("Open Air"))
        .andExpect(jsonPath("$.transport").value(false));
  }

  @Test
  void create_blankName_returns400() throws Exception {
    CreateEventRequest request =
        new CreateEventRequest(
            "",
            LocalDateTime.of(2026, 9, 1, 9, 0),
            LocalDateTime.of(2026, 9, 5, 23, 0),
            "Open Air",
            false,
            null);

    mockMvc
        .perform(
            post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
