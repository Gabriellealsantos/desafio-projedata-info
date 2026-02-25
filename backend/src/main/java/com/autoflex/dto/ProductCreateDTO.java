package com.autoflex.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ProductCreateDTO(
        @NotBlank(message = "Product name is required")
        String name,

        @NotNull(message = "Product value is required")
        @Positive(message = "Product value must be positive")
        BigDecimal value
) {}