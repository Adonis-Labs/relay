package com.example.relay.catalog.internal.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table (schema = "catalog", name = "sku")
public class Sku {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    private Long id; // id should be get only

    @Column(name = "sku_code", nullable = false, unique = true)
    @Getter
    private String skuCode; // there should be a sku generator


    /// Money should be a separate Value Object deferred for now
    @Column(
            nullable = false,
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
    @Column(nullable = false)
    @Getter
    @Setter
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Getter
    @Setter
    private SkuStatus status;

    // future lookup tables
    @Getter
    @Setter
    private String color;

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