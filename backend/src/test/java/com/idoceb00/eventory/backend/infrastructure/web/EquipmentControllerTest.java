package com.idoceb00.eventory.backend.infrastructure.web;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.idoceb00.eventory.backend.domain.model.Equipment;
import com.idoceb00.eventory.backend.domain.model.EquipmentCategory;
import com.idoceb00.eventory.backend.domain.model.EquipmentStatus;
import com.idoceb00.eventory.backend.infrastructure.persistence.EquipmentRepository;
import com.idoceb00.eventory.backend.infrastructure.web.dto.AdjustStockRequest;
import com.idoceb00.eventory.backend.infrastructure.web.dto.CreateEquipmentRequest;
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
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class EquipmentControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private EquipmentRepository equipmentRepository;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    equipmentRepository.save(
        new Equipment("Speakers", EquipmentCategory.SOUND, EquipmentStatus.CATALOGUED, 10));
    equipmentRepository.save(
        new Equipment("Old Lights", EquipmentCategory.LIGHTING, EquipmentStatus.DECATALOGUED, 5));
  }

  @Test
  void findAll_returnsAllEquipment() throws Exception {
    mockMvc
        .perform(get("/api/equipment"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  void findById_existingId_returnsEquipment() throws Exception {
    Equipment saved = equipmentRepository.findAll().getFirst();
    mockMvc
        .perform(get("/api/equipment/" + saved.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Speakers"))
        .andExpect(jsonPath("$.category").value("SOUND"))
        .andExpect(jsonPath("$.status").value("CATALOGUED"))
        .andExpect(jsonPath("$.stock").value(10));
  }

  @Test
  void findById_nonExistingId_returns404() throws Exception {
    mockMvc.perform(get("/api/equipment/999")).andExpect(status().isNotFound());
  }

  @Test
  void findCatalogued_returnsOnlyCatalogued() throws Exception {
    mockMvc
        .perform(get("/api/equipment/catalogue"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Speakers"));
  }

  @Test
  void create_validRequest_returns201() throws Exception {
    CreateEquipmentRequest request =
        new CreateEquipmentRequest("Microphones", EquipmentCategory.SOUND, 20);

    mockMvc
        .perform(
            post("/api/equipment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Microphones"))
        .andExpect(jsonPath("$.category").value("SOUND"))
        .andExpect(jsonPath("$.status").value("CATALOGUED"))
        .andExpect(jsonPath("$.stock").value(20));
  }

  @Test
  void create_blankName_returns400() throws Exception {
    CreateEquipmentRequest request = new CreateEquipmentRequest("", EquipmentCategory.SOUND, 20);

    mockMvc
        .perform(
            post("/api/equipment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_duplicateName_returns409() throws Exception {
    CreateEquipmentRequest request =
        new CreateEquipmentRequest("Speakers", EquipmentCategory.SOUND, 20);

    mockMvc
        .perform(
            post("/api/equipment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value("Equipment with name 'Speakers' already exists"));
  }

  @Test
  void adjustStock_positiveDelta_returnsUpdatedStock() throws Exception {
    Equipment saved = equipmentRepository.findByStatus(EquipmentStatus.CATALOGUED).getFirst();
    AdjustStockRequest request = new AdjustStockRequest(5);

    mockMvc
        .perform(
            patch("/api/equipment/" + saved.getId() + "/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(saved.getId()))
        .andExpect(jsonPath("$.name").value("Speakers"))
        .andExpect(jsonPath("$.stock").value(15));
  }

  @Test
  void adjustStock_nonExistingId_returns404() throws Exception {
    AdjustStockRequest request = new AdjustStockRequest(5);

    mockMvc
        .perform(
            patch("/api/equipment/999/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void adjustStock_zeroDelta_returns400() throws Exception {
    Equipment saved = equipmentRepository.findByStatus(EquipmentStatus.CATALOGUED).getFirst();
    AdjustStockRequest request = new AdjustStockRequest(0);

    mockMvc
        .perform(
            patch("/api/equipment/" + saved.getId() + "/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
