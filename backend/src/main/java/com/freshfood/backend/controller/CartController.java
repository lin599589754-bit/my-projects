package com.freshfood.backend.controller;

import com.freshfood.backend.common.ApiResponse;
import com.freshfood.backend.dto.CartAddRequest;
import com.freshfood.backend.entity.Cart;
import com.freshfood.backend.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Cart>> listByUserId(@PathVariable @Min(value = 1, message = "用户ID不能小于1") Long userId) {
        return ApiResponse.success(cartService.listByUserId(userId));
    }

    @GetMapping("/user/{userId}/selected")
    public ApiResponse<List<Cart>> listSelectedByUserId(@PathVariable @Min(value = 1, message = "用户ID不能小于1") Long userId) {
        return ApiResponse.success(cartService.listSelectedByUserId(userId));
    }

    @PostMapping
    public ApiResponse<Cart> addToCart(@Valid @ModelAttribute CartAddRequest cartAddRequest) {
        return ApiResponse.success(cartService.addToCart(
                cartAddRequest.getUserId(),
                cartAddRequest.getProductId(),
                cartAddRequest.getQuantity()));
    }

    @PutMapping("/{id}/quantity")
    public ApiResponse<Cart> updateQuantity(@PathVariable @Min(value = 1, message = "购物车ID不能小于1") Long id,
                                            @RequestParam @Min(value = 1, message = "数量不能小于1") Integer quantity) {
        return ApiResponse.success(cartService.updateQuantity(id, quantity));
    }

    @PutMapping("/{id}/selected")
    public ApiResponse<Cart> updateSelected(@PathVariable @Min(value = 1, message = "购物车ID不能小于1") Long id,
                                            @RequestParam
                                            @Min(value = 0, message = "选中状态只能是0或1")
                                            @Max(value = 1, message = "选中状态只能是0或1")
                                            Byte selected) {
        return ApiResponse.success(cartService.updateSelected(id, selected));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCart(@PathVariable @Min(value = 1, message = "购物车ID不能小于1") Long id) {
        cartService.deleteCart(id);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/user/{userId}")
    public ApiResponse<Void> clearByUserId(@PathVariable @Min(value = 1, message = "用户ID不能小于1") Long userId) {
        cartService.clearByUserId(userId);
        return ApiResponse.success(null);
    }
}
