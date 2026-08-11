package com.idoceb00.eventory.backend.infrastructure.web;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.idoceb00.eventory.backend.domain.model.Event;
import com.idoceb00.eventory.backend.infrastructure.persistence.EventRepository;
import com.idoceb00.eventory.backend.infrastructure.web.dto.CreateEventRequest;
import com.idoceb00.eventory.backend.infrastructure.web.dto.CreatePerformanceRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

  @Test
  void createPerformance_validRequest_returns201() throws Exception {
    Event saved = eventRepository.findAll().getFirst();
    CreatePerformanceRequest request =
        new CreatePerformanceRequest("Band A", LocalTime.of(20, 0), 120, LocalTime.of(14, 0));

    mockMvc
        .perform(
            post("/api/events/" + saved.getId() + "/performances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Band A"))
        .andExpect(jsonPath("$.startTime").value("20:00:00"))
        .andExpect(jsonPath("$.duration").value(120))
        .andExpect(jsonPath("$.rehearsalTime").value("14:00:00"));
  }

  @Test
  void createPerformance_duplicatePerformance_returns409() throws Exception {
    Event saved = eventRepository.findAll().getFirst();
    CreatePerformanceRequest request =
        new CreatePerformanceRequest("Band A", LocalTime.of(20, 0), 120, LocalTime.of(14, 0));

    mockMvc
        .perform(
            post("/api/events/" + saved.getId() + "/performances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post("/api/events/" + saved.getId() + "/performances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  void createPerformance_nonExistingEvent_returns404() throws Exception {
    CreatePerformanceRequest request =
        new CreatePerformanceRequest("Band A", LocalTime.of(20, 0), 120, LocalTime.of(14, 0));

    mockMvc
        .perform(
            post("/api/events/999/performances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void findPerformances_existingEvent_returnsList() throws Exception {
    Event saved = eventRepository.findAll().getFirst();
    CreatePerformanceRequest request =
        new CreatePerformanceRequest("Band A", LocalTime.of(20, 0), 120, LocalTime.of(14, 0));

    mockMvc
        .perform(
            post("/api/events/" + saved.getId() + "/performances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(get("/api/events/" + saved.getId() + "/performances"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Band A"));
  }

  @Test
  void findPerformances_nonExistingEvent_returns404() throws Exception {
    mockMvc.perform(get("/api/events/999/performances")).andExpect(status().isNotFound());
  }

  @Test
  void deletePerformance_existingPerformance_returns204() throws Exception {
    Event saved = eventRepository.findAll().getFirst();
    CreatePerformanceRequest request =
        new CreatePerformanceRequest("Band A", LocalTime.of(20, 0), 120, LocalTime.of(14, 0));

    String response =
        mockMvc
            .perform(
                post("/api/events/" + saved.getId() + "/performances")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Long performanceId = objectMapper.readTree(response).get("id").asLong();

    mockMvc
        .perform(delete("/api/events/" + saved.getId() + "/performances/" + performanceId))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(get("/api/events/" + saved.getId() + "/performances"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void deletePerformance_nonExistingPerformance_returns404() throws Exception {
    Event saved = eventRepository.findAll().getFirst();

    mockMvc
        .perform(delete("/api/events/" + saved.getId() + "/performances/999"))
        .andExpect(status().isNotFound());
  }

  @Test
  void deletePerformance_nonExistingEvent_returns404() throws Exception {
    mockMvc.perform(delete("/api/events/999/performances/1")).andExpect(status().isNotFound());
  }
}
