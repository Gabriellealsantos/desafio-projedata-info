package com.autoflex.service;

import com.autoflex.dto.ProductCreateDTO;
import com.autoflex.dto.ProductResponseDTO;
import com.autoflex.entity.Product;
import com.autoflex.exception.BusinessException;
import com.autoflex.exception.ResourceNotFoundException;
import com.autoflex.repository.ProductRawMaterialRepository;
import com.autoflex.repository.ProductRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService")
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductRawMaterialRepository productRawMaterialRepository;

    @InjectMocks
    ProductService service;

    @Nested
    @DisplayName("findById")
    class FindById {
        /**
         * Verifica se o serviço retorna o DTO correto quando o produto existe no banco.
         */
        @Test
        @DisplayName("Should return product when found")
        void shouldReturnProductWhenFound() {
            Product product = new Product(1L, "Motor", new BigDecimal("500.00"));
            when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(product));

            ProductResponseDTO result = service.findById(1L);

            assertNotNull(result);
            assertEquals("Motor", result.name());
            assertEquals(new BigDecimal("500.00"), result.value());
        }

        /**
         * Garante que uma exceção de recurso não encontrado seja lançada para IDs inexistentes.
         */
        @Test
        @DisplayName("Should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(productRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.findById(999L));
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        /**
         * Valida o fluxo de persistência de um novo produto e a ativação automática do registro.
         */
        @Test
        @DisplayName("Should create product successfully")
        void shouldCreateProductSuccessfully() {
            ProductCreateDTO dto = new ProductCreateDTO("New Motor", new BigDecimal("750.00"));
            PanacheQuery<Product> emptyQuery = mock(PanacheQuery.class);

            when(productRepository.find("name", "New Motor")).thenReturn(emptyQuery);
            when(emptyQuery.firstResultOptional()).thenReturn(Optional.empty());
            doNothing().when(productRepository).persist(any(Product.class));

            ProductResponseDTO result = service.create(dto);

            assertNotNull(result);
            assertEquals("New Motor", result.name());
            assertEquals(new BigDecimal("750.00"), result.value());
            verify(productRepository).persist(any(Product.class));
        }

        /**
         * Impede a criação de produtos com nomes duplicados para manter a integridade do catálogo.
         */
        @Test
        @DisplayName("Should throw BusinessException for duplicate name")
        void shouldThrowForDuplicateName() {
            ProductCreateDTO dto = new ProductCreateDTO("Existing Motor", new BigDecimal("500.00"));
            Product existing = new Product(1L, "Existing Motor", new BigDecimal("500.00"));
            PanacheQuery<Product> query = mock(PanacheQuery.class);

            when(productRepository.find("name", "Existing Motor")).thenReturn(query);
            when(query.firstResultOptional()).thenReturn(Optional.of(existing));

            BusinessException exception = assertThrows(BusinessException.class, () -> service.create(dto));
            assertTrue(exception.getMessage().contains("already exists"));
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        /**
         * Testa a atualização bem-sucedida dos campos de um produto existente.
         */
        @Test
        @DisplayName("Should update product successfully")
        void shouldUpdateProductSuccessfully() {
            Product existing = new Product(1L, "Old Name", new BigDecimal("500.00"));
            ProductCreateDTO dto = new ProductCreateDTO("New Name", new BigDecimal("750.00"));
            PanacheQuery<Product> emptyQuery = mock(PanacheQuery.class);

            when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(existing));
            when(productRepository.find("name", "New Name")).thenReturn(emptyQuery);
            when(emptyQuery.firstResultOptional()).thenReturn(Optional.empty());

            ProductResponseDTO result = service.update(1L, dto);

            assertEquals("New Name", result.name());
            assertEquals(new BigDecimal("750.00"), result.value());
        }

        /**
         * Permite que um produto seja atualizado mantendo seu próprio nome atual (evita falso positivo de duplicidade).
         */
        @Test
        @DisplayName("Should allow updating product keeping same name")
        void shouldAllowSameNameOnUpdate() {
            Product existing = new Product(1L, "Same Name", new BigDecimal("500.00"));
            ProductCreateDTO dto = new ProductCreateDTO("Same Name", new BigDecimal("999.00"));
            PanacheQuery<Product> query = mock(PanacheQuery.class);

            when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(existing));
            when(productRepository.find("name", "Same Name")).thenReturn(query);
            when(query.firstResultOptional()).thenReturn(Optional.of(existing));

            ProductResponseDTO result = service.update(1L, dto);

            assertEquals("Same Name", result.name());
            assertEquals(new BigDecimal("999.00"), result.value());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        /**
         * Valida o soft delete do produto e a inativação em cascata de suas composições de matéria-prima.
         */
        @Test
        @DisplayName("Should soft delete product and cascade to associations")
        void shouldSoftDeleteProductAndCascade() {
            Product product = new Product(1L, "To Delete", new BigDecimal("500.00"));
            when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(product));

            service.delete(1L);

            verify(productRepository).softDelete(1L);
            verify(productRawMaterialRepository).update(eq("active = false WHERE product.id = ?1"), eq(1L));
        }

        /**
         * Garante erro ao tentar deletar um produto que não consta na base de dados.
         */
        @Test
        @DisplayName("Should throw ResourceNotFoundException for non-existent product")
        void shouldThrowWhenDeletingNonExistent() {
            when(productRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.delete(999L));
        }
    }
}
