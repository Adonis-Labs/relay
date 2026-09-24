package com.example.relay.catalog.internal.dto;

import com.example.relay.catalog.internal.domain.SkuStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * skuCode is taken as-is for now - {@link com.example.relay.catalog.internal.domain.Sku}'s
 * auto-generator is still a TODO, so callers must supply their own unique code until that lands.
 */
public record CreateSkuRequest(
        @NotEmpty(message = "SKU code cannot be empty")
        @Size(max = 64, message = "SKU code cannot exceed 64 characters")
        String skuCode,
        @NotNull(message = "Price is required")
        @PositiveOrZero(message = "Price cannot be negative")
        BigDecimal price,
        @NotEmpty(message = "Currency code cannot be empty")
        @Size(min = 3, max = 3, message = "Currency code must be a 3-letter ISO code")
        String currency,
        @NotNull(message = "Status is required")
        SkuStatus status,
        String color,
        String size
) {
}
