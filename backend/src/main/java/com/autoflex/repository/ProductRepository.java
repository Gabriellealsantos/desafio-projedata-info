package com.autoflex.repository;

import com.autoflex.entity.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

import java.util.List;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {


    public List<Product> findAllOrderByValueDesc(int pageIndex, int pageSize) {
        return findAll(Sort.by("value").descending())
                .page(Page.of(pageIndex, pageSize))
                .list();
    }

    public void softDelete(Long id) {
        update("active = false WHERE id = ?1", id);
    }
}
