package com.autoflex.service;

import com.autoflex.dto.PageResponseDTO;
import com.autoflex.dto.ProductCreateDTO;
import com.autoflex.dto.ProductResponseDTO;
import com.autoflex.entity.Product;
import com.autoflex.exception.BusinessException;
import com.autoflex.exception.ResourceNotFoundException;
import com.autoflex.mapper.ProductMapper;
import com.autoflex.repository.ProductRawMaterialRepository;
import com.autoflex.repository.ProductRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductRawMaterialRepository productRawMaterialRepository;

    public ProductService(ProductRepository productRepository,
                          ProductRawMaterialRepository productRawMaterialRepository) {
        this.productRepository = productRepository;
        this.productRawMaterialRepository = productRawMaterialRepository;
    }

    public PageResponseDTO<ProductResponseDTO> findAll(int pageIndex, int pageSize) {
        PanacheQuery<Product> query = productRepository.findAll(Sort.by("name").ascending())
                .page(Page.of(pageIndex, pageSize));

        List<ProductResponseDTO> items = query.list().stream()
                .map(ProductMapper::toResponse)
                .toList();

        return new PageResponseDTO<>(
                items,
                pageIndex,
                pageSize,
                query.count(),
                query.pageCount()
        );
    }

    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return ProductMapper.toResponse(product);
    }

    @Transactional
    public ProductResponseDTO create(ProductCreateDTO dto) {
        validateUniqueName(dto.name(), null);

        Product product = ProductMapper.toEntity(dto);
        product.setActive(true);

        productRepository.persist(product);
        return ProductMapper.toResponse(product);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductCreateDTO dto) {
        Product product = productRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        validateUniqueName(dto.name(), id);

        ProductMapper.updateEntity(product, dto);
        return ProductMapper.toResponse(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        productRepository.softDelete(product.getId());

        productRawMaterialRepository.update("active = false WHERE product.id = ?1", product.getId());
    }

    private void validateUniqueName(String name, Long excludeId) {
        productRepository.find("name", name).firstResultOptional()
                .ifPresent(existing -> {
                    if (excludeId == null || !existing.getId().equals(excludeId)) {
                        throw new BusinessException("Product with name '" + name + "' already exists");
                    }
                });
    }
}