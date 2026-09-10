package com.example.relay.catalog.internal.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(schema = "catalog", name="brand")
public class Brand extends BaseEntity {
    @Column(unique = true, nullable = false, length = 36)
    @Getter
    private String code;

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