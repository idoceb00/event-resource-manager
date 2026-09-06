package com.idoceb00.eventory.backend.infrastructure.web.dto;

import java.time.LocalDateTime;

public interface EndDateAware {

  LocalDateTime getStartDate();

  LocalDateTime getEndDate();
}
