package com.idoceb00.eventory.backend.infrastructure.web;

import com.idoceb00.eventory.backend.application.usecase.EventService;
import com.idoceb00.eventory.backend.domain.model.Event;
import com.idoceb00.eventory.backend.domain.model.Performance;
import com.idoceb00.eventory.backend.domain.service.EntityNotFoundException;
import com.idoceb00.eventory.backend.infrastructure.persistence.EventRepository;
import com.idoceb00.eventory.backend.infrastructure.persistence.ReservationRepository;
import com.idoceb00.eventory.backend.infrastructure.web.dto.CreateEventRequest;
import com.idoceb00.eventory.backend.infrastructure.web.dto.CreatePerformanceRequest;
import com.idoceb00.eventory.backend.infrastructure.web.dto.EventResponse;
import com.idoceb00.eventory.backend.infrastructure.web.dto.PerformanceResponse;
import com.idoceb00.eventory.backend.infrastructure.web.dto.ReservationResponse;
import com.idoceb00.eventory.backend.infrastructure.web.dto.UpdateEventRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventController {

  private final EventRepository eventRepository;
  private final EventService eventService;
  private final ReservationRepository reservationRepository;

  public EventController(
      EventRepository eventRepository,
      EventService eventService,
      ReservationRepository reservationRepository) {
    this.eventRepository = eventRepository;
    this.eventService = eventService;
    this.reservationRepository = reservationRepository;
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

  @PatchMapping("/{id}")
  public EventResponse update(
      @PathVariable Long id, @Valid @RequestBody UpdateEventRequest request) {
    Event updated = eventService.updateEvent(id, request);
    return EventResponse.fromEntity(updated);
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

  @PostMapping("/{eventId}/performances")
  public ResponseEntity<PerformanceResponse> createPerformance(
      @PathVariable Long eventId, @Valid @RequestBody CreatePerformanceRequest request) {
    Performance savedPerformance = eventService.createPerformance(eventId, request);
    return ResponseEntity.created(
            URI.create("/api/events/" + eventId + "/performances/" + savedPerformance.getId()))
        .body(PerformanceResponse.fromEntity(savedPerformance));
  }

  @GetMapping("/{eventId}/performances")
  public List<PerformanceResponse> findPerformances(@PathVariable Long eventId) {
    Event event =
        eventRepository
            .findById(eventId)
            .orElseThrow(() -> new EntityNotFoundException("Event not found: " + eventId));
    return event.getPerformances().stream().map(PerformanceResponse::fromEntity).toList();
  }

  @DeleteMapping("/{eventId}/performances/{performanceId}")
  public ResponseEntity<Void> deletePerformance(
      @PathVariable Long eventId, @PathVariable Long performanceId) {
    Event event =
        eventRepository
            .findById(eventId)
            .orElseThrow(() -> new EntityNotFoundException("Event not found: " + eventId));

    Performance performance =
        event.getPerformances().stream()
            .filter(p -> p.getId().equals(performanceId))
            .findFirst()
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Performance not found: " + performanceId + " in event: " + eventId));

    event.removePerformance(performance);
    eventRepository.save(event);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    eventService.deleteEvent(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{eventId}/reservations")
  public List<ReservationResponse> findReservations(@PathVariable Long eventId) {
    eventRepository
        .findById(eventId)
        .orElseThrow(() -> new EntityNotFoundException("Event not found: " + eventId));
    return reservationRepository.findByEventId(eventId).stream()
        .map(ReservationResponse::fromEntity)
        .toList();
  }
}
