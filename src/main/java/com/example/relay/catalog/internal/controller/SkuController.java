package com.example.relay.catalog.internal.controller;

import com.example.relay.catalog.internal.dto.CreateSkuRequest;
import com.example.relay.catalog.internal.dto.SkuResponse;
import com.example.relay.catalog.internal.dto.UpdateSkuRequest;
import com.example.relay.catalog.internal.service.SkuService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/products/{productId}/skus", version = "v1+")
@AllArgsConstructor
public class SkuController {
    private final SkuService skuService;

    @PostMapping
    public ResponseEntity<SkuResponse> createSku(@PathVariable UUID productId, @Valid @RequestBody CreateSkuRequest req) {
        SkuResponse sku = skuService.createSku(productId, req);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(sku.id())
                .toUri();
        return ResponseEntity.created(location).body(sku);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkuResponse> getSku(@PathVariable UUID productId, @PathVariable UUID id) {
        SkuResponse sku = skuService.findSkuByPublicId(productId, id);
        return ResponseEntity.ok(sku);
    }

    @GetMapping
    public ResponseEntity<List<SkuResponse>> getAllSkus(@PathVariable UUID productId) {
        List<SkuResponse> skus = skuService.findAllSkusForProduct(productId);
        return ResponseEntity.ok(skus);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkuResponse> updateSku(@PathVariable UUID productId, @PathVariable UUID id, @Valid @RequestBody UpdateSkuRequest req) {
        SkuResponse sku = skuService.updateSku(productId, id, req);
        return ResponseEntity.ok(sku);
    }
}
