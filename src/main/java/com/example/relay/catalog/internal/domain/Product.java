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
        this.slug = slugify(name) + "-" + Generators.randomAlphanumeric(8);
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

    //region Helpers
    private String slugify(String name) {
        String transformed = name
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "") // strip punctuation
                .replaceAll("\\s+", "-")          // whitespace -> hyphen
                .replaceAll("-+", "-")            // collapse repeated hyphens
                .replaceAll("^-|-$", "");         // trim leading/trailing hyphen
        if (transformed.isEmpty())
            transformed = "product";
        return transformed.substring(0, Math.min(transformed.length(), 50));
    }
    //endregion
}