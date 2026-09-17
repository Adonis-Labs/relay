package com.example.relay.catalog.internal.domain;

import com.example.relay.shared.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Entity
@Table(schema = "catalog", name="brand")
public class Brand extends AuditableEntity {
    @NotEmpty
    @Column(unique = true, nullable = false, length = 36)
    @Getter
    private String code;

    @NotEmpty
    @Column(unique = true, nullable = false, length = 256)
    @Getter
    private String name;

    @Column(columnDefinition = "TEXT")
    @Getter
    private String description;

    @Column(unique = true, columnDefinition = "TEXT")
    @Getter
    private String logoUrl;
}