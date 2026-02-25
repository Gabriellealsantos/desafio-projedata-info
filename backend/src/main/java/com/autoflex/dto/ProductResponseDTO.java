package com.autoflex.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponseDTO(
        Long id,
        String name,
        BigDecimal value,
        List<ProductRawMaterialResponseDTO> rawMaterials
) {}