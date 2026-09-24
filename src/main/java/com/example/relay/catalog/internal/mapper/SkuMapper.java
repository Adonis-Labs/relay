package com.example.relay.catalog.internal.mapper;

import com.example.relay.catalog.internal.domain.Sku;
import com.example.relay.catalog.internal.dto.CreateSkuRequest;
import com.example.relay.catalog.internal.dto.SkuResponse;
import com.example.relay.catalog.internal.dto.UpdateSkuRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface SkuMapper {
    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "product", ignore = true)
    Sku toEntity(CreateSkuRequest request);

    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "skuCode", ignore = true)
    void updateEntityFromRequest(UpdateSkuRequest request, @MappingTarget Sku sku);

    @Mapping(target = "id", source = "publicId")
    @Mapping(target = "productId", expression = "java(sku.getProduct().getPublicId())")
    SkuResponse toSkuResponse(Sku sku);

    List<SkuResponse> toSkuResponseList(List<Sku> skus);
}
