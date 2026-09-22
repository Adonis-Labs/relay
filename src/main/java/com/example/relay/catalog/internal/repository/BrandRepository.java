package com.example.relay.catalog.internal.repository;

import com.example.relay.catalog.internal.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    public Brand findByPublicId(UUID publicId);
}