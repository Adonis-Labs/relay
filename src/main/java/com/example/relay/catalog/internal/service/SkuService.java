package com.example.relay.catalog.internal.service;

import com.example.relay.catalog.internal.domain.Product;
import com.example.relay.catalog.internal.domain.Sku;
import com.example.relay.catalog.internal.dto.CreateSkuRequest;
import com.example.relay.catalog.internal.dto.SkuResponse;
import com.example.relay.catalog.internal.dto.UpdateSkuRequest;
import com.example.relay.catalog.internal.exceptions.EntityNotFoundException;
import com.example.relay.catalog.internal.mapper.SkuMapper;
import com.example.relay.catalog.internal.repository.ProductRepository;
import com.example.relay.catalog.internal.repository.SkuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkuService {
    private final SkuRepository skuRepository;
    private final ProductRepository productRepository;
    private final SkuMapper skuMapper;

    @Transactional
    public SkuResponse createSku(UUID productPublicId, CreateSkuRequest request) {
        Product product = productRepository.findByPublicId(productPublicId);
        if (product == null) {
            throw new EntityNotFoundException("No product found with id '" + productPublicId + "'");
        }

        Sku newSku = skuMapper.toEntity(request);
        product.addSku(newSku);

        Sku createdSku = skuRepository.save(newSku);
        return skuMapper.toSkuResponse(createdSku);
    }

    public SkuResponse findSkuByPublicId(UUID productPublicId, UUID skuPublicId) {
        Sku sku = findScopedSku(productPublicId, skuPublicId);
        return skuMapper.toSkuResponse(sku);
    }

    public List<SkuResponse> findAllSkusForProduct(UUID productPublicId) {
        Product product = productRepository.findByPublicId(productPublicId);
        if (product == null) {
            throw new EntityNotFoundException("No product found with id '" + productPublicId + "'");
        }

        List<Sku> skus = skuRepository.findByProduct_PublicId(productPublicId);
        return skuMapper.toSkuResponseList(skus);
    }

    @Transactional
    public SkuResponse updateSku(UUID productPublicId, UUID skuPublicId, UpdateSkuRequest request) {
        Sku sku = findScopedSku(productPublicId, skuPublicId);

        skuMapper.updateEntityFromRequest(request, sku);
        Sku updatedSku = skuRepository.save(sku);
        return skuMapper.toSkuResponse(updatedSku);
    }

    private Sku findScopedSku(UUID productPublicId, UUID skuPublicId) {
        Sku sku = skuRepository.findByPublicId(skuPublicId);
        if (sku == null || !sku.getProduct().getPublicId().equals(productPublicId)) {
            throw new EntityNotFoundException("No SKU found with id '" + skuPublicId + "' for product '" + productPublicId + "'");
        }
        return sku;
    }
}
