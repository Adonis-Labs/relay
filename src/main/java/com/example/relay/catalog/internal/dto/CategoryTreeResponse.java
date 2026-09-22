package com.example.relay.catalog.internal.dto;

import java.util.List;
import java.util.UUID;

public record CategoryTreeResponse(
        UUID id,
        String name,
        String slug,
        List<CategoryTreeResponse> subcategories
) {
}
