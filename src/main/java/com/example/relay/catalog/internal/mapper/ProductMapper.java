package com.example.relay.catalog.internal.mapper;

import com.example.relay.catalog.internal.domain.Product;
import com.example.relay.catalog.internal.dto.CreateProductRequest;
import com.example.relay.catalog.internal.dto.ProductResponse;
import com.example.relay.catalog.internal.dto.UpdateProductRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface ProductMapper {
    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "skus", ignore = true)
    Product toEntity(CreateProductRequest request);

    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "skus", ignore = true)
    void updateEntityFromRequest(UpdateProductRequest request, @MappingTarget Product product);

    @Mapping(target = "id", source = "publicId")
    @Mapping(target = "categoryId", expression = "java(product.getCategory().getPublicId())")
    @Mapping(target = "brandId", expression = "java(product.getBrand().getPublicId())")
    ProductResponse toProductResponse(Product product);

    List<ProductResponse> toProductResponseList(List<Product> products);
}
