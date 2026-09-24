package com.example.relay.catalog.internal.dto;

import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String lastModifiedBy,
        UUID id,
        String name,
        String slug,
        String description,
        UUID categoryId,
        UUID brandId
) {
}
