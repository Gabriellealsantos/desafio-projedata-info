package com.autoflex.repository;

import com.autoflex.entity.ProductRawMaterial;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductRawMaterialRepository implements PanacheRepository<ProductRawMaterial> {

    public List<ProductRawMaterial> findByProductId(Long productId) {
        return list("product.id", productId);
    }

    public Optional<ProductRawMaterial> findByProductIdAndRawMaterialId(Long productId, Long rawMaterialId) {
        return find("product.id = ?1 AND rawMaterial.id = ?2", productId, rawMaterialId).firstResultOptional();
    }

    public void deleteByProductId(Long productId) {
        delete("product.id", productId);
    }
}
