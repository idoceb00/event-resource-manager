package com.idoceb00.eventory.backend.infrastructure.web.dto;

import com.idoceb00.eventory.backend.domain.model.Event;
import java.time.LocalDateTime;
import java.util.List;

public record EventResponse(
    Long id,
    String name,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String address,
    boolean transport,
    String extraInfo,
    List<PerformanceResponse> performances) {

  public static EventResponse fromEntity(Event event) {
    List<PerformanceResponse> performances =
        event.getPerformances().stream().map(PerformanceResponse::fromEntity).toList();
    return new EventResponse(
        event.getId(),
        event.getName(),
        event.getStartDate(),
        event.getEndDate(),
        event.getAddress(),
        event.isTransport(),
        event.getExtraInfo(),
        performances);
  }
}
