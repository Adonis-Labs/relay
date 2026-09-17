package com.example.relay.catalog.internal.domain;

public class DuplicateSubcategoryException extends IllegalArgumentException {
    public DuplicateSubcategoryException(Category subcategory) {
        super("%s is already a subcategory of this category".formatted(subcategory.getName()));
    }
}