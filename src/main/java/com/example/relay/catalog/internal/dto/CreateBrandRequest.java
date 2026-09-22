package com.example.relay.catalog.internal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record CreateBrandRequest(
        @NotEmpty
        @Size(min = 1, max = 255, message = "Brand name should be between 1 and 255 characters long")
        String name,
        String description,
        MultipartFile logo
) {}