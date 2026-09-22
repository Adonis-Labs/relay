package com.example.relay.catalog.internal.service;

import com.example.relay.catalog.internal.domain.Product;
import com.example.relay.catalog.internal.dto.CreateProductRequest;
import com.example.relay.catalog.internal.mapper.ProductMapper;
import com.example.relay.catalog.internal.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

//    public UUID createProduct(CreateProductRequest request) {
////        Product newProduct = productMapper.toEntity(request);
////        productRepository.save(newProduct);
////        return newProduct.getPublicId();
//    }
}