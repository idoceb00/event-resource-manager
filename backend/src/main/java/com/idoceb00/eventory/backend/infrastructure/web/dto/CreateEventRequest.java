package com.idoceb00.eventory.backend.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@EndDateAfterStartDate
public record CreateEventRequest(
    @NotBlank String name,
    @NotNull LocalDateTime startDate,
    @NotNull LocalDateTime endDate,
    String address,
    boolean transport,
    String extraInfo)
    implements EndDateAware {

  @Override
  public LocalDateTime getStartDate() {
    return startDate;
  }

  @Override
  public LocalDateTime getEndDate() {
    return endDate;
  }
}
