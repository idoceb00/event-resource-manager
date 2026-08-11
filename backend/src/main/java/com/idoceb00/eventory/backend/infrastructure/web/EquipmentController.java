package com.idoceb00.eventory.backend.infrastructure.web;

import com.idoceb00.eventory.backend.domain.model.Equipment;
import com.idoceb00.eventory.backend.domain.model.EquipmentStatus;
import com.idoceb00.eventory.backend.domain.service.EntityNotFoundException;
import com.idoceb00.eventory.backend.infrastructure.persistence.EquipmentRepository;
import com.idoceb00.eventory.backend.infrastructure.web.dto.CreateEquipmentRequest;
import com.idoceb00.eventory.backend.infrastructure.web.dto.EquipmentResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

  private final EquipmentRepository equipmentRepository;

  public EquipmentController(EquipmentRepository equipmentRepository) {
    this.equipmentRepository = equipmentRepository;
  }

  @GetMapping
  public List<EquipmentResponse> findAll() {
    return equipmentRepository.findAll().stream().map(EquipmentResponse::fromEntity).toList();
  }

  @GetMapping("/{id}")
  public EquipmentResponse findById(@PathVariable Long id) {
    Equipment equipment =
        equipmentRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Equipment not found: " + id));
    return EquipmentResponse.fromEntity(equipment);
  }

  @GetMapping("/catalogue")
  public List<EquipmentResponse> findCatalogued() {
    return equipmentRepository.findByStatus(EquipmentStatus.CATALOGUED).stream()
        .map(EquipmentResponse::fromEntity)
        .toList();
  }

  @PostMapping
  public ResponseEntity<EquipmentResponse> create(
      @Valid @RequestBody CreateEquipmentRequest request) {
    Equipment equipment =
        new Equipment(
            request.name(), request.category(), EquipmentStatus.CATALOGUED, request.stock());
    Equipment saved = equipmentRepository.save(equipment);
    return ResponseEntity.created(URI.create("/api/equipment/" + saved.getId()))
        .body(EquipmentResponse.fromEntity(saved));
  }
}
