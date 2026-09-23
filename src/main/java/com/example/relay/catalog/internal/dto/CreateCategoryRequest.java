package com.example.relay.catalog.internal.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCategoryRequest(
        @NotEmpty
        @Size(min = 1, max = 256, message = "Category name should be between 1 and 256 characters long")
        String name,
        UUID parentId
) {
}
