package com.example.relay.catalog.internal.dto;

import com.example.relay.catalog.internal.domain.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * DTO for {@link Product} creation.
 */
public record CreateProductRequest(
        @NotEmpty(message = "Name cannot be empty")
        @NotBlank(message = "Name cannot be empty")
        @Size(message = "Name length should be between 1 and 255.", min = 1, max = 255)
        String name,
        String description) {
}