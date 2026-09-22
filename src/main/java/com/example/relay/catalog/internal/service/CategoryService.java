package com.example.relay.catalog.internal.service;

import com.example.relay.catalog.internal.domain.Category;
import com.example.relay.catalog.internal.dto.CategoryResponse;
import com.example.relay.catalog.internal.dto.CategoryTreeResponse;
import com.example.relay.catalog.internal.dto.UpdateCategoryRequest;
import com.example.relay.catalog.internal.exceptions.CategoryNotFoundException;
import com.example.relay.catalog.internal.mapper.CategoryMapper;
import com.example.relay.catalog.internal.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponse findCategoryByPublicId(UUID publicId) {
        Category category = categoryRepository.findByPublicId(publicId);
        if (category == null) {
            throw new CategoryNotFoundException("No category found with id '" + publicId + "'");
        }
        return categoryMapper.toCategoryResponse(category);
    }

    public List<CategoryResponse> findAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toCategoryResponseList(categories);
    }

    // Recursive tree walk lazy-loads `subcategories` one query per node (no @BatchSize/fetch-join
    // configured on Category) - fine for a shallow catalog tree, revisit only if profiling says so.
    @Transactional(readOnly = true)
    public CategoryTreeResponse findCategoryTreeByPublicId(UUID publicId) {
        Category category = categoryRepository.findByPublicId(publicId);
        if (category == null) {
            throw new CategoryNotFoundException("No category found with id '" + publicId + "'");
        }
        return categoryMapper.toTreeResponse(category);
    }

    public CategoryResponse updateCategory(UUID publicId, UpdateCategoryRequest request) {
        Category category = categoryRepository.findByPublicId(publicId);
        if (category == null) {
            throw new CategoryNotFoundException("No category found with id '" + publicId + "'");
        }

        categoryMapper.updateEntityFromRequest(request, category);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(updatedCategory);
    }

    @Transactional
    public CategoryResponse addSubcategory(UUID parentPublicId, UUID subcategoryPublicId) {
        Category parent = categoryRepository.findByPublicId(parentPublicId);
        if (parent == null) {
            throw new CategoryNotFoundException("No category found with id '" + parentPublicId + "'");
        }

        Category subcategory = categoryRepository.findByPublicId(subcategoryPublicId);
        if (subcategory == null) {
            throw new CategoryNotFoundException("No category found with id '" + subcategoryPublicId + "'");
        }

        parent.addSubcategory(subcategory);
        Category savedSubcategory = categoryRepository.save(subcategory);
        return categoryMapper.toCategoryResponse(savedSubcategory);
    }

    @Transactional
    public CategoryResponse changeParent(UUID publicId, UUID newParentPublicId) {
        Category category = categoryRepository.findByPublicId(publicId);
        if (category == null) {
            throw new CategoryNotFoundException("No category found with id '" + publicId + "'");
        }

        if (newParentPublicId == null) {
            category.setParentCategory(null);
        } else {
            Category newParent = categoryRepository.findByPublicId(newParentPublicId);
            if (newParent == null) {
                throw new CategoryNotFoundException("No category found with id '" + newParentPublicId + "'");
            }
            newParent.addSubcategory(category);
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(savedCategory);
    }
}
