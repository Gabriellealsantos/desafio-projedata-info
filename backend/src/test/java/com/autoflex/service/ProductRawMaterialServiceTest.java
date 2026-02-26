package com.autoflex.service;

import com.autoflex.dto.ProductRawMaterialCreateDTO;
import com.autoflex.dto.ProductRawMaterialResponseDTO;
import com.autoflex.entity.Product;
import com.autoflex.entity.ProductRawMaterial;
import com.autoflex.entity.RawMaterial;
import com.autoflex.exception.BusinessException;
import com.autoflex.exception.ResourceNotFoundException;
import com.autoflex.repository.ProductRawMaterialRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductRawMaterialService")
class ProductRawMaterialServiceTest {

    @Mock
    ProductRawMaterialRepository productRawMaterialRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    RawMaterialRepository rawMaterialRepository;

    @InjectMocks
    ProductRawMaterialService service;

    private Product product;
    private RawMaterial steel;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Motor", new BigDecimal("500.00"));
        steel = new RawMaterial(1L, "Steel Sheet", new BigDecimal("100.0000"));
    }

    @Nested
    @DisplayName("addRawMaterialToProduct")
    class AddRawMaterial {

        /**
         * Testa o vínculo bem-sucedido de um insumo a um produto.
         */
        @Test
        @DisplayName("Should add raw material to product successfully")
        void shouldAddSuccessfully() {
            ProductRawMaterialCreateDTO dto = new ProductRawMaterialCreateDTO(1L, new BigDecimal("10.0000"));

            when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(product));
            when(rawMaterialRepository.findByIdOptional(1L)).thenReturn(Optional.of(steel));
            when(productRawMaterialRepository.findByProductIdAndRawMaterialId(1L, 1L)).thenReturn(Optional.empty());
            doNothing().when(productRawMaterialRepository).persist(any(ProductRawMaterial.class));

            ProductRawMaterialResponseDTO result = service.addRawMaterialToProduct(1L, dto);

            assertNotNull(result);
            assertEquals("Steel Sheet", result.rawMaterialName());
            assertEquals(new BigDecimal("10.0000"), result.quantity());
        }

        /**
         * Impede que o mesmo insumo seja associado mais de uma vez ao mesmo produto (vínculo ativo).
         */
        @Test
        @DisplayName("Should throw BusinessException for duplicate active association")
        void shouldThrowForDuplicateAssociation() {
            ProductRawMaterialCreateDTO dto = new ProductRawMaterialCreateDTO(1L, new BigDecimal("10.0000"));
            ProductRawMaterial existing = new ProductRawMaterial(1L, product, steel, new BigDecimal("5.0000"));
            existing.setActive(true);

            when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(product));
            when(rawMaterialRepository.findByIdOptional(1L)).thenReturn(Optional.of(steel));
            when(productRawMaterialRepository.findByProductIdAndRawMaterialId(1L, 1L))
                    .thenReturn(Optional.of(existing));

            assertThrows(BusinessException.class, () -> service.addRawMaterialToProduct(1L, dto));
        }

        /**
         * Valida o erro ao tentar gerenciar insumos de um produto inexistente.
         */
        @Test
        @DisplayName("Should throw ResourceNotFoundException for non-existent product")
        void shouldThrowForNonExistentProduct() {
            ProductRawMaterialCreateDTO dto = new ProductRawMaterialCreateDTO(1L, new BigDecimal("10.0000"));
            when(productRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.addRawMaterialToProduct(999L, dto));
        }

        /**
         * Valida o erro ao tentar associar uma matéria-prima que não existe no banco.
         */
        @Test
        @DisplayName("Should throw ResourceNotFoundException for non-existent raw material")
        void shouldThrowForNonExistentRawMaterial() {
            ProductRawMaterialCreateDTO dto = new ProductRawMaterialCreateDTO(999L, new BigDecimal("10.0000"));
            when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(product));
            when(rawMaterialRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.addRawMaterialToProduct(1L, dto));
        }
    }

    @Nested
    @DisplayName("updateQuantity")
    class UpdateQuantity {

        /**
         * Testa a alteração da quantidade necessária de um insumo em uma composição existente.
         */
        @Test
        @DisplayName("Should update quantity successfully")
        void shouldUpdateQuantitySuccessfully() {
            ProductRawMaterialCreateDTO dto = new ProductRawMaterialCreateDTO(1L, new BigDecimal("25.0000"));
            ProductRawMaterial existing = new ProductRawMaterial(1L, product, steel, new BigDecimal("10.0000"));
            existing.setActive(true);

            when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(product));
            when(productRawMaterialRepository.findByProductIdAndRawMaterialId(1L, 1L))
                    .thenReturn(Optional.of(existing));

            ProductRawMaterialResponseDTO result = service.updateQuantity(1L, 1L, dto);

            assertEquals(new BigDecimal("25.0000"), result.quantity());
        }

        /**
         * Garante que não seja possível alterar a quantidade de um vínculo que já foi inativado.
         */
        @Test
        @DisplayName("Should throw BusinessException when updating inactive association")
        void shouldThrowWhenUpdatingInactiveAssociation() {
            ProductRawMaterialCreateDTO dto = new ProductRawMaterialCreateDTO(1L, new BigDecimal("25.0000"));
            ProductRawMaterial inactive = new ProductRawMaterial(1L, product, steel, new BigDecimal("10.0000"));
            inactive.setActive(false);

            when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(product));
            when(productRawMaterialRepository.findByProductIdAndRawMaterialId(1L, 1L))
                    .thenReturn(Optional.of(inactive));

            assertThrows(BusinessException.class, () -> service.updateQuantity(1L, 1L, dto));
        }
    }

    @Nested
    @DisplayName("removeRawMaterialFromProduct")
    class RemoveRawMaterial {

        /**
         * Verifica se a remoção de um insumo do produto utiliza a técnica de exclusão lógica (active = false).
         */
        @Test
        @DisplayName("Should soft delete the association")
        void shouldSoftDeleteAssociation() {
            ProductRawMaterial existing = new ProductRawMaterial(1L, product, steel, new BigDecimal("10.0000"));
            existing.setActive(true);

            when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(product));
            when(productRawMaterialRepository.findByProductIdAndRawMaterialId(1L, 1L))
                    .thenReturn(Optional.of(existing));

            service.removeRawMaterialFromProduct(1L, 1L);

            assertFalse(existing.isActive());
        }
    }

    @Nested
    @DisplayName("findByProductId")
    class FindByProductId {

        /**
         * Valida se a listagem de composição do produto filtra apenas os vínculos que estão ativos.
         */
        @Test
        @DisplayName("Should return only active associations")
        void shouldReturnOnlyActiveAssociations() {
            ProductRawMaterial active = new ProductRawMaterial(1L, product, steel, new BigDecimal("10.0000"));
            active.setActive(true);

            RawMaterial copper = new RawMaterial(2L, "Copper", new BigDecimal("50.0000"));
            ProductRawMaterial inactive = new ProductRawMaterial(2L, product, copper, new BigDecimal("5.0000"));
            inactive.setActive(false);

            when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(product));
            when(productRawMaterialRepository.findByProductId(1L)).thenReturn(List.of(active, inactive));

            List<ProductRawMaterialResponseDTO> result = service.findByProductId(1L);

            assertEquals(1, result.size());
            assertEquals("Steel Sheet", result.get(0).rawMaterialName());
        }
    }
}
