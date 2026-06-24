package com.freshfood.backend.controller;

import com.freshfood.backend.common.ApiResponse;
import com.freshfood.backend.dto.CartAddRequest;
import com.freshfood.backend.entity.Cart;
import com.freshfood.backend.security.CurrentUser;
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
    private final CurrentUser currentUser;

    public CartController(CartService cartService, CurrentUser currentUser) {
        this.cartService = cartService;
        this.currentUser = currentUser;
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Cart>> listByUserId(@PathVariable @Min(value = 1, message = "用户ID不能小于1") Long userId) {
        currentUser.requireSameUser(userId);
        return ApiResponse.success(cartService.listByUserId(userId));
    }

    @GetMapping("/user/{userId}/selected")
    public ApiResponse<List<Cart>> listSelectedByUserId(@PathVariable @Min(value = 1, message = "用户ID不能小于1") Long userId) {
        currentUser.requireSameUser(userId);
        return ApiResponse.success(cartService.listSelectedByUserId(userId));
    }

    @GetMapping("/current")
    public ApiResponse<List<Cart>> listCurrentUserCart() {
        return ApiResponse.success(cartService.listByUserId(currentUser.getUserId()));
    }

    @GetMapping("/current/selected")
    public ApiResponse<List<Cart>> listCurrentUserSelectedCart() {
        return ApiResponse.success(cartService.listSelectedByUserId(currentUser.getUserId()));
    }

    @PostMapping
    public ApiResponse<Cart> addToCart(@Valid @ModelAttribute CartAddRequest cartAddRequest) {
        currentUser.requireSameUser(cartAddRequest.getUserId());
        return ApiResponse.success(cartService.addToCart(
                cartAddRequest.getUserId(),
                cartAddRequest.getProductId(),
                cartAddRequest.getQuantity()));
    }

    @PostMapping("/current")
    public ApiResponse<Cart> addToCurrentUserCart(@RequestParam @Min(value = 1, message = "商品ID不能小于1") Long productId,
                                                  @RequestParam(required = false) @Min(value = 1, message = "数量不能小于1") Integer quantity) {
        return ApiResponse.success(cartService.addToCart(currentUser.getUserId(), productId, quantity));
    }

    @PutMapping("/{id}/quantity")
    public ApiResponse<Cart> updateQuantity(@PathVariable @Min(value = 1, message = "购物车ID不能小于1") Long id,
                                            @RequestParam @Min(value = 1, message = "数量不能小于1") Integer quantity) {
        return ApiResponse.success(cartService.updateQuantityForUser(currentUser.getUserId(), id, quantity));
    }

    @PutMapping("/{id}/selected")
    public ApiResponse<Cart> updateSelected(@PathVariable @Min(value = 1, message = "购物车ID不能小于1") Long id,
                                            @RequestParam
                                            @Min(value = 0, message = "选中状态只能是0或1")
                                            @Max(value = 1, message = "选中状态只能是0或1")
                                            Byte selected) {
        return ApiResponse.success(cartService.updateSelectedForUser(currentUser.getUserId(), id, selected));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCart(@PathVariable @Min(value = 1, message = "购物车ID不能小于1") Long id) {
        cartService.deleteCartForUser(currentUser.getUserId(), id);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/user/{userId}")
    public ApiResponse<Void> clearByUserId(@PathVariable @Min(value = 1, message = "用户ID不能小于1") Long userId) {
        currentUser.requireSameUser(userId);
        cartService.clearByUserId(userId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/current")
    public ApiResponse<Void> clearCurrentUserCart() {
        cartService.clearByUserId(currentUser.getUserId());
        return ApiResponse.success(null);
    }
}
