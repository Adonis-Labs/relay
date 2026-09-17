package com.example.relay.catalog.internal.domain;

import com.example.relay.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table (schema = "catalog", name = "sku")
public class Sku extends AuditableEntity {
    @Column(name = "public_id", nullable = false, unique = true)
    @Getter
    private UUID publicId = UUID.randomUUID();

    /// Human/warehouse-readable retail code (e.g. PV-DOG-KIBBLE-15LB-BLK-001), distinct
    /// from the opaque publicId. Not auto-derived yet — there should be a sku generator.
    @NotEmpty
    @Column(name = "sku_code", nullable = false, unique = true, length = 64)
    @Getter
    @Setter
    private String skuCode;

    /// Money should be a separate Value Object deferred for now
    @Column(nullable = false, precision = 12, scale = 2,
            check = @CheckConstraint(name = "price_non_negative", constraint = "price >= 0"))
    @PositiveOrZero
    @Getter
    @Setter
    private BigDecimal price; // should be non-negative

    @NotEmpty
    @Getter
    @Setter
    @Column(name = "currency_code", nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Getter
    @Setter
    @Column(nullable = false, columnDefinition = "sku_status")
    private SkuStatus status;

    // future lookup tables
    @Column(length = 30)
    @Getter
    @Setter
    private String color;

    @Column(length = 10)
    @Getter
    @Setter
    private String size;

    @ManyToOne
    @JoinColumn(nullable = false, name="product_id")
    @Getter
    @Setter
    private Product product;

    //region matchers
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Sku other)) return false;
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    //endregion
}