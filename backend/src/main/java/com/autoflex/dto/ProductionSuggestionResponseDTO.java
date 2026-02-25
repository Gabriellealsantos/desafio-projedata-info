package com.autoflex.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductionSuggestionResponseDTO(
        List<ProductionItem> items,
        BigDecimal totalValue) {

    public record ProductionItem(
            Long productId,
            String productName,
            BigDecimal productValue,
            long quantityToProduce,
            BigDecimal subtotal) {
    }
}