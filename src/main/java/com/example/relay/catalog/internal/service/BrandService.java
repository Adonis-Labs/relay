package com.example.relay.catalog.internal.service;

import com.example.relay.catalog.internal.domain.Brand;
import com.example.relay.catalog.internal.dto.BrandResponse;
import com.example.relay.catalog.internal.dto.CreateBrandRequest;
import com.example.relay.catalog.internal.dto.UpdateBrandRequest;
import com.example.relay.catalog.internal.exceptions.BrandNotFoundException;
import com.example.relay.catalog.internal.mapper.BrandMapper;
import com.example.relay.catalog.internal.repository.BrandRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public BrandResponse createBrand (CreateBrandRequest request) {
        Brand newBrand = brandMapper.toEntity(request);
        Brand createdBrand = brandRepository.save(newBrand);
        return brandMapper.toBrandResponse(createdBrand);
    }

    public BrandResponse updateBrand(UUID publicId, UpdateBrandRequest request) {
        Brand brand = brandRepository.findByPublicId(publicId);
        if (brand == null) {
            throw new BrandNotFoundException("No brand found with id '" + publicId + "'");
        }

        brandMapper.updateEntityFromRequest(request, brand);
        Brand updatedBrand = brandRepository.save(brand);
        return brandMapper.toBrandResponse(updatedBrand);
    }

    public BrandResponse findBrandByPublicId(UUID publicId) {
        Brand brand = brandRepository.findByPublicId(publicId);
        return brandMapper.toBrandResponse(brand);
    }

    public List<BrandResponse> findAllBrands() {
        List<Brand> brands = brandRepository.findAll();
        return brandMapper.toBrandResponseList(brands);
    }

    void deleteBrand(){}

}