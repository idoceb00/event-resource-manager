package com.idoceb00.eventory.backend.infrastructure.web.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EndDateAfterStartDateValidator.class)
@Documented
public @interface EndDateAfterStartDate {

  String message() default "Event end date must be after start date";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
