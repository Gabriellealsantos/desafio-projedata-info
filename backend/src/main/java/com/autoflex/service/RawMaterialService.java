package com.autoflex.service;

import com.autoflex.dto.PageResponseDTO;
import com.autoflex.dto.RawMaterialCreateDTO;
import com.autoflex.dto.RawMaterialResponseDTO;
import com.autoflex.entity.RawMaterial;
import com.autoflex.exception.BusinessException;
import com.autoflex.exception.ResourceNotFoundException;
import com.autoflex.mapper.RawMaterialMapper;
import com.autoflex.repository.ProductRawMaterialRepository;
import com.autoflex.repository.RawMaterialRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;
    private final ProductRawMaterialRepository productRawMaterialRepository;

    public RawMaterialService(RawMaterialRepository rawMaterialRepository,
                              ProductRawMaterialRepository productRawMaterialRepository) {
        this.rawMaterialRepository = rawMaterialRepository;
        this.productRawMaterialRepository = productRawMaterialRepository;
    }

    public PageResponseDTO<RawMaterialResponseDTO> findAll(int pageIndex, int pageSize) {
        PanacheQuery<RawMaterial> query = rawMaterialRepository.findAll(Sort.by("name").ascending())
                .page(Page.of(pageIndex, pageSize));

        List<RawMaterialResponseDTO> items = query.stream()
                .map(RawMaterialMapper::toResponse)
                .toList();

        return new PageResponseDTO<>(
                items,
                pageIndex,
                pageSize,
                query.count(),
                query.pageCount()
        );
    }

    public RawMaterialResponseDTO findById(Long id) {
        RawMaterial rawMaterial = rawMaterialRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("RawMaterial", id));
        return RawMaterialMapper.toResponse(rawMaterial);
    }

    @Transactional
    public RawMaterialResponseDTO create(RawMaterialCreateDTO dto) {
        validateUniqueName(dto.name(), null);

        RawMaterial rawMaterial = RawMaterialMapper.toEntity(dto);
        rawMaterial.setActive(true);

        rawMaterialRepository.persist(rawMaterial);
        return RawMaterialMapper.toResponse(rawMaterial);
    }

    @Transactional
    public RawMaterialResponseDTO update(Long id, RawMaterialCreateDTO dto) {
        RawMaterial rawMaterial = rawMaterialRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("RawMaterial", id));

        validateUniqueName(dto.name(), id);

        RawMaterialMapper.updateEntity(rawMaterial, dto);
        return RawMaterialMapper.toResponse(rawMaterial);
    }

    @Transactional
    public void delete(Long id) {
        RawMaterial rawMaterial = rawMaterialRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("RawMaterial", id));

        long usageCount = productRawMaterialRepository.count("rawMaterial.id = ?1 AND active = 1", id);

        if (usageCount > 0) {
            throw new BusinessException("Cannot delete raw material '" + rawMaterial.getName() +
                    "' because it is currently linked to " + usageCount + " active product(s).");
        }

        rawMaterial.setActive(false);
    }

    private void validateUniqueName(String name, Long excludeId) {
        rawMaterialRepository.find("name", name).firstResultOptional()
                .ifPresent(existing -> {
                    if (excludeId == null || !existing.getId().equals(excludeId)) {
                        throw new BusinessException("Raw material with name '" + name + "' already exists");
                    }
                });
    }
}