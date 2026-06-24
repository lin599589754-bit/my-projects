package com.freshfood.backend.controller;

import com.freshfood.backend.common.ApiResponse;
import com.freshfood.backend.entity.Orders;
import com.freshfood.backend.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Orders>> listByUserId(@PathVariable Long userId) {
        return ApiResponse.success(orderService.listByUserId(userId));
    }

    @GetMapping("/user/{userId}/status/{orderStatus}")
    public ApiResponse<List<Orders>> listByUserIdAndStatus(@PathVariable Long userId,
                                                           @PathVariable Byte orderStatus) {
        return ApiResponse.success(orderService.listByUserIdAndStatus(userId, orderStatus));
    }

    @GetMapping("/{id}")
    public ApiResponse<Orders> getOrderDetail(@PathVariable Long id) {
        return ApiResponse.success(orderService.getOrderDetail(id));
    }

    @PostMapping
    public ApiResponse<Orders> createOrder(@RequestParam Long userId,
                                           @RequestParam Long addressId,
                                           @RequestParam(required = false) String userRemark) {
        return ApiResponse.success(orderService.createOrder(userId, addressId, userRemark));
    }

    @PutMapping("/{id}/mock-pay")
    public ApiResponse<Orders> mockPay(@PathVariable Long id) {
        return ApiResponse.success(orderService.mockPay(id));
    }

    @PutMapping("/{id}/ship")
    public ApiResponse<Orders> ship(@PathVariable Long id,
                                    @RequestParam(required = false) String trackingNo) {
        return ApiResponse.success(orderService.ship(id, trackingNo));
    }

    @PutMapping("/{id}/confirm-receive")
    public ApiResponse<Orders> confirmReceive(@PathVariable Long id) {
        return ApiResponse.success(orderService.confirmReceive(id));
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<Orders> cancel(@PathVariable Long id) {
        return ApiResponse.success(orderService.cancel(id));
    }
}
