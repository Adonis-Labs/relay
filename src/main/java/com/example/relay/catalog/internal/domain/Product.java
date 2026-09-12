package com.example.relay.catalog.internal.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

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

    /// Orphaned SKU's will get removed. Ensure updates are in the same transaction or persistence context.
    /// BatchSize turns N per-product sku SELECTs into ceil(N / batchSize) batched IN-clause SELECTs.
    @Getter
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @BatchSize(size = 2)
    private List<Sku> skus = new ArrayList<>();

    // Product status?

    // TODO(human): implement addSku(Sku sku). `Sku.product` is the owning side of this
    // bidirectional relationship (it holds the product_id FK column), so just adding to
    // the `skus` list here is not enough — the sku's `product` reference must also be set,
    // or the FK will be written as null and the not-null constraint on product_id will fail.

    public Product() {
        assignProductCode();
    }

    public void assignProductCode() {
        this.productCode = UUID.randomUUID().toString();
    }

    public void addSku(Sku sku) {
        this.skus.add(sku);
        sku.setProduct(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Product other)) return false;
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}