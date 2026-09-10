package com.example.relay.catalog.internal.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/// Product is the Idea of a particular item. A grouping concept.
/// SKU is specific sellable variant of it.
@Entity
@Table(schema = "catalog", name = "product")
public class Product extends BaseEntity {
    @Column(name = "product_code", nullable = false, unique = true, length = 36)
    @Getter
    private String productCode;

    @Column(nullable = false, unique = true, length = 256)
    @Getter
    @Setter
    private String name;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Setter
    private String description;

    @OneToMany(mappedBy = "product")
    @Getter
    private List<Sku> skus = new ArrayList<>();

    // Product status?

    public Product() {
        assignProductCode();
    }

    public void assignProductCode() {
        this.productCode = UUID.randomUUID().toString();
    }
}