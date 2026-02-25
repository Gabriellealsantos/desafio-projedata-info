package com.autoflex.service;

import com.autoflex.dto.ProductionSuggestionResponseDTO;
import com.autoflex.dto.ProductionSuggestionResponseDTO.ProductionItem;
import com.autoflex.entity.Product;
import com.autoflex.entity.ProductRawMaterial;
import com.autoflex.repository.ProductRepository;
import com.autoflex.repository.RawMaterialRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ProductionSuggestionService {

    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    public ProductionSuggestionService(ProductRepository productRepository,
                                       RawMaterialRepository rawMaterialRepository) {
        this.productRepository = productRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }

    /**
     * Prioriza os produtos de maior valor.
     */
    public ProductionSuggestionResponseDTO calculateSuggestion() {

        List<Product> products = productRepository.findAllOrderByValueDesc();

        Map<Long, BigDecimal> availableStock = new HashMap<>();
        rawMaterialRepository.listAll().forEach(rm -> availableStock.put(rm.getId(), rm.getStockQuantity()));

        List<ProductionItem> items = new ArrayList<>();
        BigDecimal totalValue = BigDecimal.ZERO;

        for (Product product : products) {
            List<ProductRawMaterial> composition = product.getRawMaterials();

            if (composition == null || composition.isEmpty()) {
                continue;
            }

            long maxProducible = calculateMaxProducibleQuantity(composition, availableStock);

            if (maxProducible <= 0) {
                continue;
            }

            // 4. Abate os materiais consumidos do nosso estoque em memória
            deductConsumedMaterials(composition, availableStock, maxProducible);

            // 5. Soma os totais
            BigDecimal subtotal = product.getValue().multiply(BigDecimal.valueOf(maxProducible));
            totalValue = totalValue.add(subtotal);

            items.add(new ProductionItem(
                    product.getId(),
                    product.getName(),
                    product.getValue(),
                    maxProducible,
                    subtotal));
        }

        return new ProductionSuggestionResponseDTO(items, totalValue);
    }

    private Long calculateMaxProducibleQuantity(List<ProductRawMaterial> composition, Map<Long, BigDecimal> availableStock) {
        long maxProducible = Long.MAX_VALUE;

        for (ProductRawMaterial prm : composition) {
            if (!prm.isActive() || !prm.getRawMaterial().isActive()) {
                continue;
            }

            BigDecimal available = availableStock.getOrDefault(prm.getRawMaterial().getId(), BigDecimal.ZERO);
            BigDecimal required = prm.getQuantity();

            if (required.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            long canProduce = available.divide(required, 0, RoundingMode.FLOOR).longValue();
            maxProducible = Math.min(maxProducible, canProduce);
        }

        return maxProducible == Long.MAX_VALUE ? 0 : maxProducible;
    }

    private void deductConsumedMaterials(List<ProductRawMaterial> composition, Map<Long, BigDecimal> availableStock, long quantityProduced) {
        for (ProductRawMaterial prm : composition) {
            if (!prm.isActive() || !prm.getRawMaterial().isActive()) {
                continue;
            }

            BigDecimal consumed = prm.getQuantity().multiply(BigDecimal.valueOf(quantityProduced));
            BigDecimal currentStock = availableStock.get(prm.getRawMaterial().getId());
            availableStock.put(prm.getRawMaterial().getId(), currentStock.subtract(consumed));
        }
    }
}