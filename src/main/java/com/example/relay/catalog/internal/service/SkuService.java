package com.example.relay.catalog.internal.service;

import com.example.relay.catalog.internal.domain.Sku;
import com.example.relay.catalog.internal.repository.SkuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SkuService {
    private final SkuRepository skuRepository;

    @Retryable(value = ObjectOptimisticLockingFailureException.class)
    @Transactional
    public void decrementStock(Long skuId, int count) {
        Sku sku = skuRepository.findById(skuId).orElseThrow();
        sku.decrementStock(count);
    }
}
