package com.example.relay.catalog.internal.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddSubcategoryRequest(
        @NotNull
        UUID subcategoryId
) {
}
