package com.example.relay.catalog.internal.controller;

import com.example.relay.catalog.internal.dto.CreateProductRequest;
import com.example.relay.catalog.internal.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/products", version = "v1+")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") UUID id) {
        return null;
    }

//    @PostMapping
//    public ResponseEntity<UUID> post(@Valid @RequestBody CreateProductRequest productRequest) {
//        UUID id = productService.createProduct(productRequest);
//        return ResponseEntity.status(HttpStatus.CREATED).body(id);
//    }
}