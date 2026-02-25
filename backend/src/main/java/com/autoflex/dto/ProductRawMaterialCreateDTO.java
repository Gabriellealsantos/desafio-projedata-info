package com.autoflex.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ProductRawMaterialCreateDTO(
        @NotNull(message = "Raw material ID is required")
        Long rawMaterialId,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        BigDecimal quantity
) {}