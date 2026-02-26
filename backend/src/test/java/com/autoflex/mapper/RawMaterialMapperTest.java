package com.autoflex.mapper;

import com.autoflex.dto.RawMaterialCreateDTO;
import com.autoflex.dto.RawMaterialResponseDTO;
import com.autoflex.entity.RawMaterial;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RawMaterialMapper")
class RawMaterialMapperTest {

    /**
     * Testa o mapeamento de uma matéria-prima para seu DTO de saída.
     */
    @Test
    @DisplayName("toResponse should map entity to DTO correctly")
    void toResponseShouldMapCorrectly() {
        RawMaterial entity = new RawMaterial(1L, "Steel Sheet", new BigDecimal("500.0000"));

        RawMaterialResponseDTO dto = RawMaterialMapper.toResponse(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Steel Sheet", dto.name());
        assertEquals(new BigDecimal("500.0000"), dto.stockQuantity());
    }

    /**
     * Valida a resiliência do mapper de resposta contra valores nulos.
     */
    @Test
    @DisplayName("toResponse should return null for null entity")
    void toResponseShouldReturnNullForNull() {
        assertNull(RawMaterialMapper.toResponse(null));
    }

    /**
     * Verifica a conversão dos dados de entrada (DTO) para a entidade de banco de dados.
     */
    @Test
    @DisplayName("toEntity should map DTO to entity correctly")
    void toEntityShouldMapCorrectly() {
        RawMaterialCreateDTO dto = new RawMaterialCreateDTO("Copper Wire", new BigDecimal("200.0000"));

        RawMaterial entity = RawMaterialMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("Copper Wire", entity.getName());
        assertEquals(new BigDecimal("200.0000"), entity.getStockQuantity());
        assertNull(entity.getId());
    }

    /**
     * Valida a resiliência do mapper de persistência contra valores nulos.
     */
    @Test
    @DisplayName("toEntity should return null for null DTO")
    void toEntityShouldReturnNullForNull() {
        assertNull(RawMaterialMapper.toEntity(null));
    }

    /**
     * Garante que a lógica de atualização sobrescreve os valores corretos na entidade existente.
     */
    @Test
    @DisplayName("updateEntity should modify existing entity fields")
    void updateEntityShouldModifyFields() {
        RawMaterial entity = new RawMaterial(1L, "Old", new BigDecimal("100.0000"));
        RawMaterialCreateDTO dto = new RawMaterialCreateDTO("Updated", new BigDecimal("999.0000"));

        RawMaterialMapper.updateEntity(entity, dto);

        assertEquals("Updated", entity.getName());
        assertEquals(new BigDecimal("999.0000"), entity.getStockQuantity());
        assertEquals(1L, entity.getId());
    }
}
