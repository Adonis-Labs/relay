package com.example.relay.catalog.internal.repository;

import com.example.relay.catalog.internal.domain.Sku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SkuRepository extends JpaRepository<Sku, Long> {
    public Sku findByPublicId(UUID publicId);
    public List<Sku> findByProduct_PublicId(UUID productPublicId);
}
