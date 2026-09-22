package com.example.relay.catalog.internal.dto;

import java.util.UUID;

public record ChangeCategoryParentRequest(
        UUID parentId
) {
}
