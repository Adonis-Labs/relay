package com.example.relay.catalog.internal.service;

import com.example.relay.catalog.internal.domain.Brand;
import com.example.relay.catalog.internal.domain.Category;
import com.example.relay.catalog.internal.domain.Product;
import com.example.relay.catalog.internal.dto.CreateProductRequest;
import com.example.relay.catalog.internal.dto.ProductResponse;
import com.example.relay.catalog.internal.dto.UpdateProductRequest;
import com.example.relay.catalog.internal.exceptions.EntityNotFoundException;
import com.example.relay.catalog.internal.mapper.ProductMapper;
import com.example.relay.catalog.internal.repository.BrandRepository;
import com.example.relay.catalog.internal.repository.CategoryRepository;
import com.example.relay.catalog.internal.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    public ProductResponse createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findByPublicId(request.categoryId());
        if (category == null) {
            throw new EntityNotFoundException("No category found with id '" + request.categoryId() + "'");
        }

        Brand brand = brandRepository.findByPublicId(request.brandId());
        if (brand == null) {
            throw new EntityNotFoundException("No brand found with id '" + request.brandId() + "'");
        }

        Product newProduct = productMapper.toEntity(request);
        newProduct.setCategory(category);
        newProduct.setBrand(brand);

        Product createdProduct = productRepository.save(newProduct);
        return productMapper.toProductResponse(createdProduct);
    }

    public ProductResponse findProductByPublicId(UUID publicId) {
        Product product = productRepository.findByPublicId(publicId);
        if (product == null) {
            throw new EntityNotFoundException("No product found with id '" + publicId + "'");
        }
        return productMapper.toProductResponse(product);
    }

    public List<ProductResponse> findAllProducts() {
        List<Product> products = productRepository.findAll();
        return productMapper.toProductResponseList(products);
    }

    public ProductResponse updateProduct(UUID publicId, UpdateProductRequest request) {
        Product product = productRepository.findByPublicId(publicId);
        if (product == null) {
            throw new EntityNotFoundException("No product found with id '" + publicId + "'");
        }

        Category category = categoryRepository.findByPublicId(request.categoryId());
        if (category == null) {
            throw new EntityNotFoundException("No category found with id '" + request.categoryId() + "'");
        }

        Brand brand = brandRepository.findByPublicId(request.brandId());
        if (brand == null) {
            throw new EntityNotFoundException("No brand found with id '" + request.brandId() + "'");
        }

        productMapper.updateEntityFromRequest(request, product);
        product.setCategory(category);
        product.setBrand(brand);

        Product updatedProduct = productRepository.save(product);
        return productMapper.toProductResponse(updatedProduct);
    }
}
