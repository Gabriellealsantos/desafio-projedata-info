package com.autoflex.mapper;

import com.autoflex.dto.ProductRawMaterialResponseDTO;
import com.autoflex.entity.ProductRawMaterial;

public final class ProductRawMaterialMapper {

    private ProductRawMaterialMapper() {}

    public static ProductRawMaterialResponseDTO toResponse(ProductRawMaterial entity) {
        if (entity == null) return null;

        return new ProductRawMaterialResponseDTO(
                entity.getId(),
                entity.getRawMaterial().getId(),
                entity.getRawMaterial().getName(),
                entity.getQuantity()
        );
    }
}