package com.example.relay.catalog.internal.domain;

import com.example.relay.shared.domain.AuditableEntity;
import com.example.relay.shared.helpers.Generators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(schema = "catalog", name = "brand")
public class Brand extends AuditableEntity {
    @Column(name = "public_id", nullable = false, unique = true)
    @Getter
    private UUID publicId = UUID.randomUUID();

    @NotEmpty
    @Getter
    @Column(nullable = false, unique = true, length = 256)
    private String name;

    @NotEmpty
    @Getter
    @Column(nullable = false, unique = true, length = 60)
    private String slug;

    @Getter
    @Setter
    @Column(columnDefinition = "TEXT")
    private String description;

    @Getter
    @Setter
    @Column(unique = true, columnDefinition = "TEXT")
    private String logoUrl;

    public void setName(String name) {
        if (this.slug == null || this.slug.isEmpty())
            this.slug = Generators.slug(name, "brand", 8);
        this.name = name;
    }
}
