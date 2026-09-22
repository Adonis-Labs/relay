package com.example.relay.catalog.internal.mapper;

import com.example.relay.catalog.internal.domain.Brand;
import com.example.relay.catalog.internal.dto.BrandResponse;
import com.example.relay.catalog.internal.dto.CreateBrandRequest;
import com.example.relay.catalog.internal.dto.UpdateBrandRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface BrandMapper {
    @Mapping(target = "logoUrl", ignore = true)
    public Brand toEntity(CreateBrandRequest request);

    @Mapping(target = "logoUrl", ignore = true)
    public void updateEntityFromRequest(UpdateBrandRequest request, @MappingTarget Brand brand);

    @Mapping(target = "id", source = "publicId")
    public BrandResponse toBrandResponse(Brand brand);

    public List<BrandResponse> toBrandResponseList(List<Brand> brands);
}
