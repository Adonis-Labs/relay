package com.example.relay.catalog.internal.domain;

import com.example.relay.catalog.internal.exceptions.CategoryCycleException;
import com.example.relay.catalog.internal.exceptions.DuplicateSubcategoryException;
import com.example.relay.shared.domain.AuditableEntity;
import com.example.relay.shared.helpers.Generators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/// Categories form a tree via self-referencing parent/child (adjacency list). A null
/// parentCategory means a top-level category.
@Entity
@Table(schema = "catalog", name = "category")
public class Category extends AuditableEntity {
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
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    @Getter
    @OneToMany(mappedBy = "parentCategory", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Category> subcategories = new ArrayList<>();

    public void setName(String name) {
        if (this.slug == null || this.slug.isEmpty())
            this.slug = Generators.slug(name, "category", 8);
        this.name = name;
    }

    public void addSubcategory(Category subcategory) {
        if (this.subcategories.contains(subcategory))
            throw new DuplicateSubcategoryException(subcategory);

        for (Category ancestor = this; ancestor != null; ancestor = ancestor.getParentCategory()) {
            if (ancestor.equals(subcategory))
                throw new CategoryCycleException(
                        "Cannot add '%s' as a subcategory of '%s' - '%s' is already an ancestor of '%s'"
                                .formatted(subcategory.getName(), this.getName(), subcategory.getName(), this.getName())
                );
        }

        this.subcategories.add(subcategory);
        subcategory.setParentCategory(this);
    }
}
