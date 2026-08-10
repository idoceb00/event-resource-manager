package com.idoceb00.eventory.backend.infrastructure.web.dto;

import com.idoceb00.eventory.backend.domain.model.Performance;
import java.time.LocalTime;

public record PerformanceResponse(
    Long id, String name, LocalTime startTime, long duration, LocalTime rehearsalTime) {

  public static PerformanceResponse fromEntity(Performance performance) {
    return new PerformanceResponse(
        performance.getId(),
        performance.getName(),
        performance.getStartTime(),
        performance.getDuration(),
        performance.getRehearsalTime());
  }
}
