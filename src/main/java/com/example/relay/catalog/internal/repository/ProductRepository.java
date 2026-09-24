package com.example.relay.catalog.internal.repository;

import com.example.relay.catalog.internal.domain.Product;
import com.example.relay.catalog.internal.dto.ProductListView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    public Product findByPublicId(UUID publicId);

    @Query("""
            select
                p.createdAt as createdAt,
                p.updatedAt as updatedAt,
                p.createdBy as createdBy,
                p.lastModifiedBy as lastModifiedBy,
                p.publicId as id,
                p.name as name,
                p.slug as slug,
                p.description as description,
                p.category.publicId as categoryId,
                p.brand.publicId as brandId
            from Product p
            """)
    List<ProductListView> findAllProjectedBy();
}