package com.freshfood.backend.controller;

import com.freshfood.backend.common.ApiResponse;
import com.freshfood.backend.entity.Cart;
import com.freshfood.backend.service.CartService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Cart>> listByUserId(@PathVariable Long userId) {
        return ApiResponse.success(cartService.listByUserId(userId));
    }

    @GetMapping("/user/{userId}/selected")
    public ApiResponse<List<Cart>> listSelectedByUserId(@PathVariable Long userId) {
        return ApiResponse.success(cartService.listSelectedByUserId(userId));
    }

    @PostMapping
    public ApiResponse<Cart> addToCart(@RequestParam Long userId,
                                       @RequestParam Long productId,
                                       @RequestParam(required = false) Integer quantity) {
        return ApiResponse.success(cartService.addToCart(userId, productId, quantity));
    }

    @PutMapping("/{id}/quantity")
    public ApiResponse<Cart> updateQuantity(@PathVariable Long id,
                                            @RequestParam Integer quantity) {
        return ApiResponse.success(cartService.updateQuantity(id, quantity));
    }

    @PutMapping("/{id}/selected")
    public ApiResponse<Cart> updateSelected(@PathVariable Long id,
                                            @RequestParam Byte selected) {
        return ApiResponse.success(cartService.updateSelected(id, selected));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCart(@PathVariable Long id) {
        cartService.deleteCart(id);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/user/{userId}")
    public ApiResponse<Void> clearByUserId(@PathVariable Long userId) {
        cartService.clearByUserId(userId);
        return ApiResponse.success(null);
    }
}