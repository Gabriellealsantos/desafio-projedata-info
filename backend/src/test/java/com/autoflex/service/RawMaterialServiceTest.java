package com.autoflex.service;

import com.autoflex.dto.RawMaterialCreateDTO;
import com.autoflex.dto.RawMaterialResponseDTO;
import com.autoflex.entity.RawMaterial;
import com.autoflex.exception.BusinessException;
import com.autoflex.exception.ResourceNotFoundException;
import com.autoflex.repository.ProductRawMaterialRepository;
import com.autoflex.repository.RawMaterialRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RawMaterialService")
class RawMaterialServiceTest {

    @Mock
    RawMaterialRepository rawMaterialRepository;

    @Mock
    ProductRawMaterialRepository productRawMaterialRepository;

    @InjectMocks
    RawMaterialService service;

    @Nested
    @DisplayName("findById")
    class FindById {

        /**
         * Verifica o retorno correto dos dados da matéria-prima ao buscar por um ID válido.
         */
        @Test
        @DisplayName("Should return raw material when found")
        void shouldReturnWhenFound() {
            RawMaterial rm = new RawMaterial(1L, "Steel Sheet", new BigDecimal("500.0000"));
            when(rawMaterialRepository.findByIdOptional(1L)).thenReturn(Optional.of(rm));

            RawMaterialResponseDTO result = service.findById(1L);

            assertEquals("Steel Sheet", result.name());
            assertEquals(new BigDecimal("500.0000"), result.stockQuantity());
        }

        /**
         * Valida o lançamento de exceção quando o insumo solicitado não existe.
         */
        @Test
        @DisplayName("Should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(rawMaterialRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.findById(999L));
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        /**
         * Testa o cadastro de um insumo e a execução da lógica de persistência.
         */
        @Test
        @DisplayName("Should create raw material successfully")
        void shouldCreateSuccessfully() {
            RawMaterialCreateDTO dto = new RawMaterialCreateDTO("Copper Wire", new BigDecimal("200.0000"));
            PanacheQuery<RawMaterial> emptyQuery = mock(PanacheQuery.class);

            when(rawMaterialRepository.find("name", "Copper Wire")).thenReturn(emptyQuery);
            when(emptyQuery.firstResultOptional()).thenReturn(Optional.empty());
            doNothing().when(rawMaterialRepository).persist(any(RawMaterial.class));

            RawMaterialResponseDTO result = service.create(dto);

            assertEquals("Copper Wire", result.name());
            verify(rawMaterialRepository).persist(any(RawMaterial.class));
        }

        /**
         * Valida a regra de negócio que proíbe matérias-primas com nomes repetidos.
         */
        @Test
        @DisplayName("Should throw BusinessException for duplicate name")
        void shouldThrowForDuplicateName() {
            RawMaterialCreateDTO dto = new RawMaterialCreateDTO("Steel Sheet", new BigDecimal("100.0000"));
            RawMaterial existing = new RawMaterial(1L, "Steel Sheet", new BigDecimal("500.0000"));
            PanacheQuery<RawMaterial> query = mock(PanacheQuery.class);

            when(rawMaterialRepository.find("name", "Steel Sheet")).thenReturn(query);
            when(query.firstResultOptional()).thenReturn(Optional.of(existing));

            BusinessException ex = assertThrows(BusinessException.class, () -> service.create(dto));
            assertTrue(ex.getMessage().contains("already exists"));
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        /**
         * Testa a exclusão lógica quando o insumo não possui nenhum vínculo ativo com produtos.
         */
        @Test
        @DisplayName("Should soft delete raw material with no active links")
        void shouldSoftDeleteWhenNoActiveLinks() {
            RawMaterial rm = new RawMaterial(1L, "Unused Material", new BigDecimal("100.0000"));
            when(rawMaterialRepository.findByIdOptional(1L)).thenReturn(Optional.of(rm));
            when(productRawMaterialRepository.count("rawMaterial.id = ?1 AND active = ?2", 1L, true)).thenReturn(0L);

            service.delete(1L);

            assertFalse(rm.isActive());
        }

        /**
         * Regra de Integridade: Impede a remoção de insumos que estão sendo usados na fabricação de produtos ativos.
         */
        @Test
        @DisplayName("Should throw BusinessException when raw material has active product links")
        void shouldThrowWhenLinkedToActiveProducts() {
            RawMaterial rm = new RawMaterial(1L, "Linked Material", new BigDecimal("100.0000"));
            when(rawMaterialRepository.findByIdOptional(1L)).thenReturn(Optional.of(rm));
            when(productRawMaterialRepository.count("rawMaterial.id = ?1 AND active = ?2", 1L, true)).thenReturn(3L);

            BusinessException ex = assertThrows(BusinessException.class, () -> service.delete(1L));
            assertTrue(ex.getMessage().contains("linked to"));
            assertTrue(ex.getMessage().contains("3"));
        }

        /**
         * Garante que o sistema informe erro ao tentar remover um ID de insumo inexistente.
         */
        @Test
        @DisplayName("Should throw ResourceNotFoundException for non-existent raw material")
        void shouldThrowWhenDeletingNonExistent() {
            when(rawMaterialRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.delete(999L));
        }
    }
}
