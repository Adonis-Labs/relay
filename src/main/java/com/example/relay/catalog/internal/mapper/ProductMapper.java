package com.example.relay.catalog.internal.mapper;

import com.example.relay.catalog.internal.domain.Product;
import com.example.relay.catalog.internal.dto.CreateProductRequest;
import org.mapstruct.*;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface ProductMapper {

//    Product toEntity(CreateProductRequest createProductRequest);
}