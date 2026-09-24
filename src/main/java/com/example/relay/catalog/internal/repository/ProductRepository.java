package com.example.relay.catalog.internal.repository;

import com.example.relay.catalog.internal.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    public Product findByPublicId(UUID publicId);
}