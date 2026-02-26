package com.autoflex.service;

import com.autoflex.dto.ProductionSuggestionResponseDTO;
import com.autoflex.dto.ProductionSuggestionResponseDTO.ProductionItem;
import com.autoflex.entity.Product;
import com.autoflex.entity.ProductRawMaterial;
import com.autoflex.entity.RawMaterial;
import com.autoflex.repository.ProductRepository;
import com.autoflex.repository.RawMaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductionSuggestionService")
class ProductionSuggestionServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    RawMaterialRepository rawMaterialRepository;

    @InjectMocks
    ProductionSuggestionService service;

    private RawMaterial steel;
    private RawMaterial copper;
    private RawMaterial plastic;

    /**
     * Inicializa os objetos de matéria-prima base para serem utilizados nos cenários de teste.
     */
    @BeforeEach
    void setUp() {
        steel = new RawMaterial(1L, "Steel Sheet", new BigDecimal("100.0000"));
        copper = new RawMaterial(2L, "Copper Wire", new BigDecimal("50.0000"));
        plastic = new RawMaterial(3L, "Plastic Resin", new BigDecimal("200.0000"));
    }


    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {
        /**
         * Garante que o serviço retorne uma resposta vazia quando não há produtos no catálogo.
         */
        @Test
        @DisplayName("Should return empty list when no products exist")
        void shouldReturnEmptyWhenNoProducts() {
            when(productRepository.findAllOrderByValueDesc()).thenReturn(Collections.emptyList());
            when(rawMaterialRepository.listAll()).thenReturn(List.of(steel, copper));

            ProductionSuggestionResponseDTO result = service.calculateSuggestion();

            assertTrue(result.items().isEmpty());
            assertEquals(BigDecimal.ZERO, result.totalValue());
        }

        /**
         * Verifica se o algoritmo ignora produtos que não possuem fórmula (composição) definida.
         */
        @Test
        @DisplayName("Should skip products without composition")
        void shouldSkipProductsWithoutComposition() {
            Product productNoComposition = new Product(1L, "Empty Product", new BigDecimal("500.00"));

            when(productRepository.findAllOrderByValueDesc()).thenReturn(List.of(productNoComposition));
            when(rawMaterialRepository.listAll()).thenReturn(List.of(steel));

            ProductionSuggestionResponseDTO result = service.calculateSuggestion();

            assertTrue(result.items().isEmpty());
            assertEquals(BigDecimal.ZERO, result.totalValue());
        }

        /**
         * Valida que a quantidade sugerida é zero quando o estoque disponível é insuficiente para a fórmula.
         */
        @Test
        @DisplayName("Should produce 0 units when stock is insufficient")
        void shouldProduceZeroWhenInsufficientStock() {
            Product product = new Product(1L, "Big Product", new BigDecimal("1000.00"));
            RawMaterial lowStock = new RawMaterial(1L, "Low Stock Material", new BigDecimal("1.0000"));

            ProductRawMaterial prm = new ProductRawMaterial(1L, product, lowStock, new BigDecimal("10.0000"));
            prm.setActive(true);
            product.getRawMaterials().add(prm);

            when(productRepository.findAllOrderByValueDesc()).thenReturn(List.of(product));
            when(rawMaterialRepository.listAll()).thenReturn(List.of(lowStock));

            ProductionSuggestionResponseDTO result = service.calculateSuggestion();

            assertTrue(result.items().isEmpty());
            assertEquals(BigDecimal.ZERO, result.totalValue());
        }
    }

    @Nested
    @DisplayName("Production calculation")
    class ProductionCalculation {

        /**
         * Testa o cálculo básico de produção máxima baseada no insumo limitante (gargalo).
         */
        @Test
        @DisplayName("Should calculate correct quantity for single product")
        void shouldCalculateCorrectQuantityForSingleProduct() {
            Product motor = new Product(1L, "Motor", new BigDecimal("500.00"));

            // Motor needs: 10 steel, 5 copper
            ProductRawMaterial prmSteel = new ProductRawMaterial(1L, motor, steel, new BigDecimal("10.0000"));
            prmSteel.setActive(true);
            ProductRawMaterial prmCopper = new ProductRawMaterial(2L, motor, copper, new BigDecimal("5.0000"));
            prmCopper.setActive(true);
            motor.getRawMaterials().addAll(List.of(prmSteel, prmCopper));

            // Stock: 100 steel, 50 copper → bottleneck = steel (100/10=10), copper
            // (50/5=10) → 10 units
            when(productRepository.findAllOrderByValueDesc()).thenReturn(List.of(motor));
            when(rawMaterialRepository.listAll()).thenReturn(List.of(steel, copper));

            ProductionSuggestionResponseDTO result = service.calculateSuggestion();

            assertEquals(1, result.items().size());

            ProductionItem item = result.items().get(0);
            assertEquals("Motor", item.productName());
            assertEquals(10, item.quantityToProduce());
            assertEquals(new BigDecimal("5000.00"), item.subtotal());
            assertEquals(new BigDecimal("5000.00"), result.totalValue());
        }

        /**
         * Regra de Negócio: Valida se o algoritmo consome o estoque primeiro para produtos de maior valor.
         */
        @Test
        @DisplayName("Should prioritize higher value products first")
        void shouldPrioritizeHigherValueProducts() {
            // Expensive product ($1000) needs 50 steel each
            Product expensive = new Product(1L, "Expensive", new BigDecimal("1000.00"));
            ProductRawMaterial prmExpensiveSteel = new ProductRawMaterial(1L, expensive, steel,
                    new BigDecimal("50.0000"));
            prmExpensiveSteel.setActive(true);
            expensive.getRawMaterials().add(prmExpensiveSteel);

            // Cheap product ($100) needs 10 steel each
            Product cheap = new Product(2L, "Cheap", new BigDecimal("100.00"));
            ProductRawMaterial prmCheapSteel = new ProductRawMaterial(2L, cheap, steel, new BigDecimal("10.0000"));
            prmCheapSteel.setActive(true);
            cheap.getRawMaterials().add(prmCheapSteel);

            // Stock: 100 steel. Expensive first → 100/50 = 2 units, consumes 100 steel.
            // Cheap → 0.
            when(productRepository.findAllOrderByValueDesc()).thenReturn(List.of(expensive, cheap));
            when(rawMaterialRepository.listAll()).thenReturn(List.of(steel));

            ProductionSuggestionResponseDTO result = service.calculateSuggestion();

            assertEquals(1, result.items().size());
            assertEquals("Expensive", result.items().get(0).productName());
            assertEquals(2, result.items().get(0).quantityToProduce());
            assertEquals(new BigDecimal("2000.00"), result.totalValue());
        }

        /**
         * Verifica se o estoque "em memória" é abatido corretamente ao processar múltiplos produtos em sequência.
         */
        @Test
        @DisplayName("Should deduct shared raw materials sequentially")
        void shouldDeductSharedMaterialsSequentially() {
            // Product A ($500): needs 30 steel
            Product productA = new Product(1L, "Product A", new BigDecimal("500.00"));
            ProductRawMaterial prmA = new ProductRawMaterial(1L, productA, steel, new BigDecimal("30.0000"));
            prmA.setActive(true);
            productA.getRawMaterials().add(prmA);

            // Product B ($200): needs 20 steel
            Product productB = new Product(2L, "Product B", new BigDecimal("200.00"));
            ProductRawMaterial prmB = new ProductRawMaterial(2L, productB, steel, new BigDecimal("20.0000"));
            prmB.setActive(true);
            productB.getRawMaterials().add(prmB);

            // Stock: 100 steel. A first (higher value) → 100/30 = 3 units → consumes 90. B
            // → 10/20 = 0.
            when(productRepository.findAllOrderByValueDesc()).thenReturn(List.of(productA, productB));
            when(rawMaterialRepository.listAll()).thenReturn(List.of(steel));

            ProductionSuggestionResponseDTO result = service.calculateSuggestion();

            assertEquals(1, result.items().size());
            assertEquals("Product A", result.items().get(0).productName());
            assertEquals(3, result.items().get(0).quantityToProduce());
            assertEquals(new BigDecimal("1500.00"), result.totalValue());
        }


        /**
         * Testa a sugestão de produção para múltiplos produtos distintos quando há insumos para todos.
         */
        @Test
        @DisplayName("Should produce multiple products when stock allows")
        void shouldProduceMultipleProductsWhenStockAllows() {
            // Product A ($500): needs 10 steel
            Product productA = new Product(1L, "Product A", new BigDecimal("500.00"));
            ProductRawMaterial prmA = new ProductRawMaterial(1L, productA, steel, new BigDecimal("10.0000"));
            prmA.setActive(true);
            productA.getRawMaterials().add(prmA);

            // Product B ($200): needs 5 copper
            Product productB = new Product(2L, "Product B", new BigDecimal("200.00"));
            ProductRawMaterial prmB = new ProductRawMaterial(2L, productB, copper, new BigDecimal("5.0000"));
            prmB.setActive(true);
            productB.getRawMaterials().add(prmB);

            // Stock: 100 steel, 50 copper. No shared materials → both produced
            // independently.
            when(productRepository.findAllOrderByValueDesc()).thenReturn(List.of(productA, productB));
            when(rawMaterialRepository.listAll()).thenReturn(List.of(steel, copper));

            ProductionSuggestionResponseDTO result = service.calculateSuggestion();

            assertEquals(2, result.items().size());
            assertEquals(10, result.items().get(0).quantityToProduce()); // A: 100/10
            assertEquals(10, result.items().get(1).quantityToProduce()); // B: 50/5
            assertEquals(new BigDecimal("7000.00"), result.totalValue()); // 5000 + 2000
        }
    }

    /**
     * Garante que matérias-primas marcadas como inativas sejam ignoradas no cálculo de produção.
     */
    @Nested
    @DisplayName("Inactive items handling")
    class InactiveItems {

        @Test
        @DisplayName("Should ignore inactive raw materials in composition")
        void shouldIgnoreInactiveRawMaterials() {
            Product product = new Product(1L, "Product", new BigDecimal("500.00"));

            // Active material with enough stock
            ProductRawMaterial activePrm = new ProductRawMaterial(1L, product, steel, new BigDecimal("10.0000"));
            activePrm.setActive(true);

            // Inactive material — should be ignored
            RawMaterial inactiveMaterial = new RawMaterial(99L, "Inactive", BigDecimal.ZERO);
            inactiveMaterial.setActive(false);
            ProductRawMaterial inactivePrm = new ProductRawMaterial(2L, product, inactiveMaterial,
                    new BigDecimal("100.0000"));
            inactivePrm.setActive(true);

            product.getRawMaterials().addAll(List.of(activePrm, inactivePrm));

            when(productRepository.findAllOrderByValueDesc()).thenReturn(List.of(product));
            when(rawMaterialRepository.listAll()).thenReturn(List.of(steel, inactiveMaterial));

            ProductionSuggestionResponseDTO result = service.calculateSuggestion();

            assertEquals(1, result.items().size());
            assertEquals(10, result.items().get(0).quantityToProduce()); // Only considers steel
        }

        /**
         * Garante que vínculos de composição inativados não interfiram na sugestão de produção.
         */
        @Test
        @DisplayName("Should ignore inactive associations")
        void shouldIgnoreInactiveAssociations() {
            Product product = new Product(1L, "Product", new BigDecimal("500.00"));

            ProductRawMaterial activePrm = new ProductRawMaterial(1L, product, steel, new BigDecimal("10.0000"));
            activePrm.setActive(true);

            // Inactive association — should be ignored
            ProductRawMaterial inactivePrm = new ProductRawMaterial(2L, product, copper, new BigDecimal("1000.0000"));
            inactivePrm.setActive(false);

            product.getRawMaterials().addAll(List.of(activePrm, inactivePrm));

            when(productRepository.findAllOrderByValueDesc()).thenReturn(List.of(product));
            when(rawMaterialRepository.listAll()).thenReturn(List.of(steel, copper));

            ProductionSuggestionResponseDTO result = service.calculateSuggestion();

            assertEquals(1, result.items().size());
            assertEquals(10, result.items().get(0).quantityToProduce()); // Ignores copper association
        }
    }
}
