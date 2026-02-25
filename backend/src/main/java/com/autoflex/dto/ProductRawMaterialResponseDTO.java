package com.autoflex.dto;

import java.math.BigDecimal;

public record ProductRawMaterialResponseDTO(
        Long id,
        Long rawMaterialId,
        String rawMaterialName,
        BigDecimal quantity
) {}