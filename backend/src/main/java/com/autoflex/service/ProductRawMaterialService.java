package com.autoflex.service;

import com.autoflex.dto.ProductRawMaterialCreateDTO;
import com.autoflex.dto.ProductRawMaterialResponseDTO;
import com.autoflex.entity.Product;
import com.autoflex.entity.ProductRawMaterial;
import com.autoflex.entity.RawMaterial;
import com.autoflex.exception.BusinessException;
import com.autoflex.exception.ResourceNotFoundException;
import com.autoflex.mapper.ProductRawMaterialMapper;
import com.autoflex.repository.ProductRawMaterialRepository;
import com.autoflex.repository.ProductRepository;
import com.autoflex.repository.RawMaterialRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductRawMaterialService {

    private final ProductRawMaterialRepository productRawMaterialRepository;
    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    public ProductRawMaterialService(ProductRawMaterialRepository productRawMaterialRepository,
                                     ProductRepository productRepository,
                                     RawMaterialRepository rawMaterialRepository) {
        this.productRawMaterialRepository = productRawMaterialRepository;
        this.productRepository = productRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }


    /**
     * Lista todas as matérias-primas vinculadas a um produto específico.
     */
    public List<ProductRawMaterialResponseDTO> findByProductId(Long productId) {
        validateProductExists(productId);
        return productRawMaterialRepository.findByProductId(productId).stream()
                .filter(ProductRawMaterial::isActive)
                .map(ProductRawMaterialMapper::toResponse)
                .toList();
    }

    /**
     * Associa uma matéria-prima a um produto, validando se o vínculo já existe.
     */
    @Transactional
    public ProductRawMaterialResponseDTO addRawMaterialToProduct(Long productId, ProductRawMaterialCreateDTO dto) {
        Product product = productRepository.findByIdOptional(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        RawMaterial rawMaterial = rawMaterialRepository.findByIdOptional(dto.rawMaterialId())
                .orElseThrow(() -> new ResourceNotFoundException("RawMaterial", dto.rawMaterialId()));

        productRawMaterialRepository.findByProductIdAndRawMaterialId(productId, dto.rawMaterialId())
                .filter(ProductRawMaterial::isActive)
                .ifPresent(existing -> {
                    throw new BusinessException("Raw material '" + rawMaterial.getName()
                            + "' is already associated with this product");
                });

        ProductRawMaterial entity = new ProductRawMaterial();
        entity.setProduct(product);
        entity.setRawMaterial(rawMaterial);
        entity.setQuantity(dto.quantity());
        entity.setActive(true);

        productRawMaterialRepository.persist(entity);
        return ProductRawMaterialMapper.toResponse(entity);
    }

    /**
     * Realiza a associação em lote de múltiplas matérias-primas a um produto.
     */
    @Transactional
    public List<ProductRawMaterialResponseDTO> addMultipleRawMaterials(Long productId, List<ProductRawMaterialCreateDTO> dtos) {
        Product product = productRepository.findByIdOptional(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        List<Long> rmIds = dtos.stream().map(ProductRawMaterialCreateDTO::rawMaterialId).toList();

        Map<Long, RawMaterial> rawMaterialsMap = rawMaterialRepository.find("id in ?1", rmIds)
                .stream().collect(Collectors.toMap(RawMaterial::getId, rm -> rm));

        return dtos.stream().map(dto -> {
            RawMaterial rm = Optional.ofNullable(rawMaterialsMap.get(dto.rawMaterialId()))
                    .orElseThrow(() -> new ResourceNotFoundException("RawMaterial", dto.rawMaterialId()));

            ProductRawMaterial entity = new ProductRawMaterial();
            entity.setProduct(product);
            entity.setRawMaterial(rm);
            entity.setQuantity(dto.quantity());
            entity.setActive(true);

            productRawMaterialRepository.persist(entity);
            return ProductRawMaterialMapper.toResponse(entity);
        }).toList();
    }

    /**
     * Atualiza a quantidade necessária de uma matéria-prima em um vínculo ativo.
     */
    @Transactional
    public ProductRawMaterialResponseDTO updateQuantity(Long productId, Long rawMaterialId, ProductRawMaterialCreateDTO dto) {
        validateProductExists(productId);

        ProductRawMaterial entity = productRawMaterialRepository
                .findByProductIdAndRawMaterialId(productId, rawMaterialId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Association between Product " + productId + " and RawMaterial " + rawMaterialId
                                + " not found"));

        if (!entity.isActive()) {
            throw new BusinessException("Cannot update an inactive association.");
        }

        entity.setQuantity(dto.quantity());
        return ProductRawMaterialMapper.toResponse(entity);
    }

    /**
     * Inativa o vínculo entre uma matéria-prima e um produto (exclusão lógica).
     */
    @Transactional
    public void removeRawMaterialFromProduct(Long productId, Long rawMaterialId) {
        validateProductExists(productId);

        ProductRawMaterial entity = productRawMaterialRepository
                .findByProductIdAndRawMaterialId(productId, rawMaterialId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Association between Product " + productId + " and RawMaterial " + rawMaterialId
                                + " not found"));

        entity.setActive(false);
    }

    /**
     * Verifica a existência do produto para garantir a integridade da operação.
     */
    private void validateProductExists(Long productId) {
        productRepository.findByIdOptional(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
    }
}