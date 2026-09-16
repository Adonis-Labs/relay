package com.example.relay.catalog.internal.controller;

import com.example.relay.catalog.internal.dto.CreateProductDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/catalog/product")
public class ProductController {
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") Long id) {
        return null;
    }

    @PostMapping("/")
    public ResponseEntity<?> post(@Valid @RequestBody CreateProductDto value) {
        return null;
    }
}