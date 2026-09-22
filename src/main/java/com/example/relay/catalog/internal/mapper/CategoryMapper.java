package com.example.relay.catalog.internal.mapper;

import com.example.relay.catalog.internal.domain.Category;
import com.example.relay.catalog.internal.dto.CategoryResponse;
import com.example.relay.catalog.internal.dto.CategoryTreeResponse;
import com.example.relay.catalog.internal.dto.UpdateCategoryRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface CategoryMapper {
    @Mapping(target = "id", source = "publicId")
    @Mapping(target = "parentId", expression = "java(category.getParentCategory() != null ? category.getParentCategory().getPublicId() : null)")
    public CategoryResponse toCategoryResponse(Category category);

    public List<CategoryResponse> toCategoryResponseList(List<Category> categories);

    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "parentCategory", ignore = true)
    @Mapping(target = "subcategories", ignore = true)
    public void updateEntityFromRequest(UpdateCategoryRequest request, @MappingTarget Category category);

    @Mapping(target = "id", source = "publicId")
    public CategoryTreeResponse toTreeResponse(Category category);
}
