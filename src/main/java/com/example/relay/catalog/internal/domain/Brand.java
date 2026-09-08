package com.example.relay.catalog.internal.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(schema = "catalog", name="brand")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    private Long id;

    @Column(unique = true, nullable = false, length = 36)
    @Getter
    private String code;

    @Column(unique = true, nullable = false)
    @Getter
    private String name;

    @Getter
    private String description;

    @Column(unique = true)
    @Getter
    private String logoUrl;
}