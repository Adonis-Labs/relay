package com.example.relay.catalog.internal.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @NotEmpty
        @Size(min = 1, max = 255, message = "Category name should be between 1 and 255 characters long")
        String name
) {
}
