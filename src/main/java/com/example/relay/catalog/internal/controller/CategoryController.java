package com.example.relay.catalog.internal.controller;

import com.example.relay.catalog.internal.dto.AddSubcategoryRequest;
import com.example.relay.catalog.internal.dto.CategoryResponse;
import com.example.relay.catalog.internal.dto.CategoryTreeResponse;
import com.example.relay.catalog.internal.dto.ChangeCategoryParentRequest;
import com.example.relay.catalog.internal.dto.CreateCategoryRequest;
import com.example.relay.catalog.internal.dto.UpdateCategoryRequest;
import com.example.relay.catalog.internal.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/categories", version = "v1+")
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest req) {
        CategoryResponse category = categoryService.createCategory(req);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(category.id())
                .toUri();
        return ResponseEntity.created(location).body(category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable UUID id) {
        CategoryResponse category = categoryService.findCategoryByPublicId(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.findAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}/tree")
    public ResponseEntity<CategoryTreeResponse> getCategoryTree(@PathVariable UUID id) {
        CategoryTreeResponse tree = categoryService.findCategoryTreeByPublicId(id);
        return ResponseEntity.ok(tree);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable UUID id, @Valid @RequestBody UpdateCategoryRequest req) {
        CategoryResponse updatedCategory = categoryService.updateCategory(id, req);
        return ResponseEntity.ok(updatedCategory);
    }

    @PostMapping("/{id}/subcategories")
    public ResponseEntity<CategoryResponse> addSubcategory(@PathVariable UUID id, @Valid @RequestBody AddSubcategoryRequest req) {
        CategoryResponse subcategory = categoryService.addSubcategory(id, req.subcategoryId());
        return ResponseEntity.ok(subcategory);
    }

    @PutMapping("/{id}/parent")
    public ResponseEntity<CategoryResponse> changeParent(@PathVariable UUID id, @RequestBody ChangeCategoryParentRequest req) {
        CategoryResponse category = categoryService.changeParent(id, req.parentId());
        return ResponseEntity.ok(category);
    }
}