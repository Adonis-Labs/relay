package com.example.relay.catalog.internal.domain;

import jakarta.persistence.*;
import lombok.Getter;

@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Version
    @Getter
    private int version;
    // Created at
    // updated at
    // created by
    // last updated by
}