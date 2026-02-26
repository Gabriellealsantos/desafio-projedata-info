package com.autoflex.mapper;

import com.autoflex.dto.ProductCreateDTO;
import com.autoflex.dto.ProductResponseDTO;
import com.autoflex.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductMapper")
class ProductMapperTest {

    /**
     * Verifica se a conversão de Entidade para DTO de resposta mantém a integridade dos dados.
     */
    @Test
    @DisplayName("toResponse should map entity to DTO correctly")
    void toResponseShouldMapCorrectly() {
        Product product = new Product(1L, "Motor", new BigDecimal("500.00"));

        ProductResponseDTO dto = ProductMapper.toResponse(product);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Motor", dto.name());
        assertEquals(new BigDecimal("500.00"), dto.value());
        assertNotNull(dto.rawMaterials());
        assertTrue(dto.rawMaterials().isEmpty());
    }

    /**
     * Garante que o mapper lida corretamente com entradas nulas no fluxo de resposta.
     */
    @Test
    @DisplayName("toResponse should return null for null entity")
    void toResponseShouldReturnNullForNull() {
        assertNull(ProductMapper.toResponse(null));
    }

    /**
     * Valida se um DTO de criação é transformado corretamente em uma nova Entidade.
     */
    @Test
    @DisplayName("toEntity should map DTO to entity correctly")
    void toEntityShouldMapCorrectly() {
        ProductCreateDTO dto = new ProductCreateDTO("New Motor", new BigDecimal("750.00"));

        Product entity = ProductMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("New Motor", entity.getName());
        assertEquals(new BigDecimal("750.00"), entity.getValue());
        assertNull(entity.getId());
    }

    /**
     * Garante que o mapper não quebra ao receber um DTO nulo para criação.
     */
    @Test
    @DisplayName("toEntity should return null for null DTO")
    void toEntityShouldReturnNullForNull() {
        assertNull(ProductMapper.toEntity(null));
    }

    /**
     * Testa se a atualização de uma entidade existente preserva o ID e altera apenas os campos permitidos.
     */
    @Test
    @DisplayName("updateEntity should modify existing entity fields")
    void updateEntityShouldModifyFields() {
        Product entity = new Product(1L, "Old", new BigDecimal("100.00"));
        ProductCreateDTO dto = new ProductCreateDTO("Updated", new BigDecimal("999.00"));

        ProductMapper.updateEntity(entity, dto);

        assertEquals("Updated", entity.getName());
        assertEquals(new BigDecimal("999.00"), entity.getValue());
        assertEquals(1L, entity.getId()); // ID should not change
    }
}
