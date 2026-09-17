package com.example.relay.catalog.internal.domain;

import com.example.relay.shared.domain.AuditableEntity;
import com.example.relay.shared.helpers.Generators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/// Product is the Idea of a particular item. A grouping concept.
/// SKU is specific sellable variant of it.
@Entity
@Table(name = "product", schema = "catalog")
public class Product extends AuditableEntity {
    @Column(name = "public_id", nullable = false, unique = true)
    @Getter
    private UUID publicId = UUID.randomUUID();

    @NotEmpty
    @Getter
    @Column(nullable = false, unique = true, length = 256)
    private String name;

    @Getter
    @Setter
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotEmpty
    @Getter
    @Column(nullable = false, unique = true, length = 60)
    private String slug;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    /// Orphaned SKU's will get removed. Ensure updates are in the same transaction or persistence context.
    /// BatchSize turns N per-product sku SELECTs into ceil(N / batchSize) batched IN-clause SELECTs.
    @Getter
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @BatchSize(size = 2)
    private List<Sku> skus = new ArrayList<>();

    // Product status?

    public void setName(String name) {
        if (this.slug == null || this.slug.isEmpty())
            generateSlug(name);
        this.name = name;
    }

    private void generateSlug(String name) {
        this.slug = Generators.slug(name, "product", 8);
    }

    public void addSku(Sku sku) {
        this.skus.add(sku);
        sku.setProduct(this);
    }
}