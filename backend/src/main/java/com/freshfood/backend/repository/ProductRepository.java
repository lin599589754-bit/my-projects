package com.freshfood.backend.repository;

import com.freshfood.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatusOrderBySortOrderAscIdAsc(Byte status);

    List<Product> findByCategoryIdAndStatusOrderBySortOrderAscIdAsc(Long categoryId, Byte status);

    List<Product> findByIsHotAndStatusOrderBySortOrderAscIdAsc(Byte isHot, Byte status);

    List<Product> findByIsNewAndStatusOrderBySortOrderAscIdAsc(Byte isNew, Byte status);
}