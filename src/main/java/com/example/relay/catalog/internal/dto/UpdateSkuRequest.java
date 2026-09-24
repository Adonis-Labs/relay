package com.example.relay.catalog.internal.dto;

import com.example.relay.catalog.internal.domain.SkuStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateSkuRequest(
        @NotNull(message = "Price is required")
        @PositiveOrZero(message = "Price cannot be negative")
        BigDecimal price,
        @NotNull(message = "Currency code cannot be empty")
        @Size(min = 3, max = 3, message = "Currency code must be a 3-letter ISO code")
        String currency,
        @NotNull(message = "Status is required")
        SkuStatus status,
        String color,
        String size
) {
}
