package com.autoflex.dto;

import java.math.BigDecimal;

public record RawMaterialResponseDTO(
        Long id,
        String name,
        BigDecimal stockQuantity
) {}