package com.autoflex.mapper;

import com.autoflex.dto.ProductCreateDTO;
import com.autoflex.dto.ProductRawMaterialResponseDTO;
import com.autoflex.dto.ProductResponseDTO;
import com.autoflex.entity.Product;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ProductMapper {

    private ProductMapper() {}

    public static ProductResponseDTO toResponse(Product entity) {
        if (entity == null) return null;

        List<ProductRawMaterialResponseDTO> prmResponses =
                (entity.getRawMaterials() == null) ? Collections.emptyList() :
                        entity.getRawMaterials().stream()
                                .filter(prm -> prm.isActive()) // Ignora matérias-primas inativas da lista
                                .map(ProductRawMaterialMapper::toResponse)
                                .collect(Collectors.toList());

        return new ProductResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getValue(),
                prmResponses
        );
    }

    public static Product toEntity(ProductCreateDTO dto) {
        if (dto == null) return null;
        Product entity = new Product();
        entity.setName(dto.name());
        entity.setValue(dto.value());
        return entity;
    }

    public static void updateEntity(Product entity, ProductCreateDTO dto) {
        if (entity != null && dto != null) {
            entity.setName(dto.name());
            entity.setValue(dto.value());
        }
    }
}