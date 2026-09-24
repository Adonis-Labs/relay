package com.example.relay.catalog.internal.dto;

import java.time.Instant;
import java.util.UUID;

/// Spring Data projection for the products list endpoint. Getter names are chosen to
/// match ProductResponse's record components exactly, so ProductMapper needs no
/// @Mapping overrides. Backed by ProductRepository#findAllProjectedBy, which selects
/// only these columns (including category/brand publicId via a join) - Category and
/// Brand are never loaded as entities, so there's nothing left to N+1 on.
public interface ProductListView {
    Instant getCreatedAt();
    Instant getUpdatedAt();
    String getCreatedBy();
    String getLastModifiedBy();
    UUID getId();
    String getName();
    String getSlug();
    String getDescription();
    UUID getCategoryId();
    UUID getBrandId();
}
