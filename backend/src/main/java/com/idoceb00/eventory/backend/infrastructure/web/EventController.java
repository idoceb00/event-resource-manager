package com.idoceb00.eventory.backend.infrastructure.web;

import com.idoceb00.eventory.backend.domain.model.Event;
import com.idoceb00.eventory.backend.domain.service.EntityNotFoundException;
import com.idoceb00.eventory.backend.infrastructure.persistence.EventRepository;
import com.idoceb00.eventory.backend.infrastructure.web.dto.CreateEventRequest;
import com.idoceb00.eventory.backend.infrastructure.web.dto.EventResponse;
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
@RequestMapping("/api/events")
public class EventController {

  private final EventRepository eventRepository;

  public EventController(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  @GetMapping
  public List<EventResponse> findAll() {
    return eventRepository.findAll().stream().map(EventResponse::fromEntity).toList();
  }

  @GetMapping("/{id}")
  public EventResponse findById(@PathVariable Long id) {
    Event event =
        eventRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Event not found: " + id));
    return EventResponse.fromEntity(event);
  }

  @PostMapping
  public ResponseEntity<EventResponse> create(@Valid @RequestBody CreateEventRequest request) {
    Event event =
        new Event(
            request.name(),
            request.startDate(),
            request.endDate(),
            request.address(),
            request.transport(),
            request.extraInfo());
    Event saved = eventRepository.save(event);
    return ResponseEntity.created(URI.create("/api/events/" + saved.getId()))
        .body(EventResponse.fromEntity(saved));
  }
}
