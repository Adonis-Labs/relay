package com.example.relay.catalog.internal.domain;

import jakarta.persistence.*;
import jakarta.validation.Constraint;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import javax.naming.OperationNotSupportedException;
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

    @Min(value = 0)
    @Getter
    @Column(nullable = false, check = @CheckConstraint(name = "stock_non_negative", constraint = "stock >= 0"))
    private int stock;

    @ManyToOne
    @JoinColumn(nullable = false, name="product_id")
    @Getter
    @Setter
    private Product product;

    //region constructors
    public Sku() {
        setSkuCode();
    }
    //endregion

    //region setters
    private void setSkuCode() {
        // generate random alphanumeric SKU-Code
        this.skuCode = UUID.randomUUID().toString();
    }

    public void incrementStock(int count) {
        if(count<=0)
            throw new ValidationException("Increment count must be greater than 0");
        this.stock += count;
    }

    public void decrementStock(int count) {
        if(this.getStock() - count < 0)
            throw new ValidationException("Stock cannot be less than 0");
        this.stock -= count;
    }
    //endregion

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