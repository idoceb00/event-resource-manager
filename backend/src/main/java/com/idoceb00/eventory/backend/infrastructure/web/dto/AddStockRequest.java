package com.idoceb00.eventory.backend.infrastructure.web.dto;

import jakarta.validation.constraints.Positive;

public record AddStockRequest(@Positive int quantityToAdd) {}
