package com.idoceb00.eventory.backend.infrastructure.web.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EndDateAfterStartDateValidator
    implements ConstraintValidator<EndDateAfterStartDate, EndDateAware> {

  @Override
  public boolean isValid(EndDateAware value, ConstraintValidatorContext context) {
    LocalDateTime startDate = value.getStartDate();
    LocalDateTime endDate = value.getEndDate();
    if (startDate == null || endDate == null) {
      return true;
    }
    return endDate.isAfter(startDate);
  }
}
