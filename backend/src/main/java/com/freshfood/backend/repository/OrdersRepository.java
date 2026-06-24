package com.freshfood.backend.repository;

import com.freshfood.backend.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByUserIdOrderByCreateTimeDesc(Long userId);

    List<Orders> findByUserIdAndOrderStatusOrderByCreateTimeDesc(Long userId, Byte orderStatus);

    Optional<Orders> findByOrderNo(String orderNo);
}
