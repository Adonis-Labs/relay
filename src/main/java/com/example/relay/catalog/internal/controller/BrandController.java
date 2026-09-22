package com.example.relay.catalog.internal.controller;

import com.example.relay.catalog.internal.dto.CreateBrandRequest;
import com.example.relay.catalog.internal.service.BrandService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog/brand")
@AllArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBrand(@Valid  CreateBrandRequest req) {
        return null;
    }
}
