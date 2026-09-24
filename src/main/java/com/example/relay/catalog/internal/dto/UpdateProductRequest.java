package com.example.relay.catalog.internal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateProductRequest(
        @NotEmpty(message = "Name cannot be empty")
        @NotBlank(message = "Name cannot be empty")
        @Size(message = "Name length should be between 1 and 255.", min = 1, max = 255)
        String name,
        String description,
        @NotNull(message = "Category is required")
        UUID categoryId,
        @NotNull(message = "Brand is required")
        UUID brandId) {
}
