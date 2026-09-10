package com.example.relay.catalog.internal.domain;

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
public class Sku extends BaseEntity {
    @Column(name = "code", nullable = false, unique = true, length = 36)
    @Getter
    private String skuCode; // there should be a sku generator


    /// Money should be a separate Value Object deferred for now
    @Column(
            nullable = false,
            precision = 12,
            scale = 2,
            check = @CheckConstraint(
                    name = "price_non_negative",
                    constraint = "price >= 0"
            )
    )
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

    // Created at
    // updated at
    // created by
    // last updated by

    // product foreign key owner
    @ManyToOne
    @JoinColumn(nullable = false, name="product_id")
    @Getter
    private Product product;

    public Sku() {
        setSkuCode();
    }

    private void setSkuCode() {
        // generate random alphanumeric SKU-Code
        this.skuCode = UUID.randomUUID().toString();
    }
}