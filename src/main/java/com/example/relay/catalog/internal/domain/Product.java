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
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    private Long id;

    @Column(name = "product_code", nullable = false, unique = true)
    @Getter
    private String productCode;

    @Getter
    @Setter
    private String name;

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