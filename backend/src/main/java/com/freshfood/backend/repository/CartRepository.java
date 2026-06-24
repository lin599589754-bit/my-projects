package com.freshfood.backend.repository;

import com.freshfood.backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUserIdOrderByIdDesc(Long userId);

    List<Cart> findByUserIdAndSelectedOrderByIdDesc(Long userId, Byte selected);

    Optional<Cart> findByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserId(Long userId);

    void deleteByUserIdAndSelected(Long userId, Byte selected);
}