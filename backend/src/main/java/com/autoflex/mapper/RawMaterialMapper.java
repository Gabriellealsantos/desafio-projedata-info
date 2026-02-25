package com.autoflex.mapper;

import com.autoflex.dto.RawMaterialCreateDTO;
import com.autoflex.dto.RawMaterialResponseDTO;
import com.autoflex.entity.RawMaterial;

public final class RawMaterialMapper {

    private RawMaterialMapper() {
    }

    public static RawMaterialResponseDTO toResponse(RawMaterial entity) {
        if (entity == null) {
            return null;
        }
        return new RawMaterialResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getStockQuantity()
        );
    }

    public static RawMaterial toEntity(RawMaterialCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        RawMaterial entity = new RawMaterial();
        entity.setName(dto.name());
        entity.setStockQuantity(dto.stockQuantity());
        return entity;
    }

    public static void updateEntity(RawMaterial entity, RawMaterialCreateDTO dto) {
        if (entity != null && dto != null) {
            entity.setName(dto.name());
            entity.setStockQuantity(dto.stockQuantity());
        }
    }
}