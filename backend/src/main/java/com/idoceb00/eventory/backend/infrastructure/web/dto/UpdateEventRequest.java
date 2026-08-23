package com.idoceb00.eventory.backend.infrastructure.web.dto;

import java.time.LocalDateTime;

/**
 * Partial update request for events. All fields are nullable: {@code null} means "keep current
 * value" for name, startDate, endDate, and transport. For address and extraInfo, {@code null}
 * explicitly clears the field (these are legitimately optional and can be empty).
 */
public record UpdateEventRequest(
    String name,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String address,
    Boolean transport,
    String extraInfo) {}
