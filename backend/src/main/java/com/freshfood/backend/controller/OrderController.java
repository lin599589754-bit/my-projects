package com.freshfood.backend.controller;

import com.freshfood.backend.common.ApiResponse;
import com.freshfood.backend.dto.OrderCreateRequest;
import com.freshfood.backend.entity.Orders;
import com.freshfood.backend.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Orders>> listByUserId(@PathVariable @Min(value = 1, message = "用户ID不能小于1") Long userId) {
        return ApiResponse.success(orderService.listByUserId(userId));
    }

    @GetMapping("/user/{userId}/status/{orderStatus}")
    public ApiResponse<List<Orders>> listByUserIdAndStatus(
            @PathVariable @Min(value = 1, message = "用户ID不能小于1") Long userId,
            @PathVariable
            @Min(value = 0, message = "订单状态不能小于0")
            @Max(value = 4, message = "订单状态不能大于4")
            Byte orderStatus) {
        return ApiResponse.success(orderService.listByUserIdAndStatus(userId, orderStatus));
    }

    @GetMapping("/{id}")
    public ApiResponse<Orders> getOrderDetail(@PathVariable @Min(value = 1, message = "订单ID不能小于1") Long id) {
        return ApiResponse.success(orderService.getOrderDetail(id));
    }

    @PostMapping
    public ApiResponse<Orders> createOrder(@Valid @ModelAttribute OrderCreateRequest orderCreateRequest) {
        return ApiResponse.success(orderService.createOrder(
                orderCreateRequest.getUserId(),
                orderCreateRequest.getAddressId(),
                orderCreateRequest.getUserRemark()));
    }

    @PutMapping("/{id}/mock-pay")
    public ApiResponse<Orders> mockPay(@PathVariable @Min(value = 1, message = "订单ID不能小于1") Long id) {
        return ApiResponse.success(orderService.mockPay(id));
    }

    @PutMapping("/{id}/ship")
    public ApiResponse<Orders> ship(@PathVariable @Min(value = 1, message = "订单ID不能小于1") Long id,
                                    @RequestParam(required = false) String trackingNo) {
        return ApiResponse.success(orderService.ship(id, trackingNo));
    }

    @PutMapping("/{id}/confirm-receive")
    public ApiResponse<Orders> confirmReceive(@PathVariable @Min(value = 1, message = "订单ID不能小于1") Long id) {
        return ApiResponse.success(orderService.confirmReceive(id));
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<Orders> cancel(@PathVariable @Min(value = 1, message = "订单ID不能小于1") Long id) {
        return ApiResponse.success(orderService.cancel(id));
    }
}
