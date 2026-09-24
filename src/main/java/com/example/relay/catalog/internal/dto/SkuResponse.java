package com.example.relay.catalog.internal.dto;

import com.example.relay.catalog.internal.domain.SkuStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SkuResponse(
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String lastModifiedBy,
        UUID id,
        String skuCode,
        BigDecimal price,
        String currency,
        SkuStatus status,
        String color,
        String size,
        UUID productId
) {
}
