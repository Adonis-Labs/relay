package com.example.relay.catalog.internal.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record UpdateBrandRequest(
        @NotEmpty
        @Size(min = 1, max = 255, message = "Brand name should be between 1 and 255 characters long")
        String name,
        String description,

        //TODO: Implement logo saving to external block storage and save link to db
        MultipartFile logo
) {}
