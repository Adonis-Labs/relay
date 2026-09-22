package com.example.relay.catalog.internal.controller;

import com.example.relay.catalog.internal.dto.BrandResponse;
import com.example.relay.catalog.internal.dto.CreateBrandRequest;
import com.example.relay.catalog.internal.dto.UpdateBrandRequest;
import com.example.relay.catalog.internal.service.BrandService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/catalog/brand")
@AllArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBrand(@Valid  CreateBrandRequest req) throws URISyntaxException {
        BrandResponse createdBrand = brandService.createBrand(req);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdBrand.id())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(createdBrand);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrand(@PathVariable UUID id) {
        BrandResponse brand = brandService.findBrandByPublicId(id);
        return ResponseEntity.ok(brand);
    }

    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        List<BrandResponse> brands = brandService.findAllBrands();
        return ResponseEntity.ok(brands);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BrandResponse> updateBrand(@PathVariable UUID id, @Valid UpdateBrandRequest req) {
        BrandResponse updatedBrand = brandService.updateBrand(id, req);
        return ResponseEntity.ok(updatedBrand);
    }
}