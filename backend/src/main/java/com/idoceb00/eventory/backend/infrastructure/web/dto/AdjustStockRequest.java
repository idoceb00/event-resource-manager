package com.idoceb00.eventory.backend.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

public record AdjustStockRequest(@NotNull Integer delta) {}
