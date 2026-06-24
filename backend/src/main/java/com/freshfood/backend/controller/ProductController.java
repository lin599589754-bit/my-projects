package com.freshfood.backend.controller;

import com.freshfood.backend.common.ApiResponse;
import com.freshfood.backend.entity.Product;
import com.freshfood.backend.service.ProductService;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/onsale")
    public ApiResponse<List<Product>> listOnSaleProducts() {
        return ApiResponse.success(productService.listOnSaleProducts());
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<Product>> listProductsByCategory(@PathVariable @Min(value = 1, message = "分类ID不能小于1") Long categoryId) {
        return ApiResponse.success(productService.listProductsByCategory(categoryId));
    }

    @GetMapping("/hot")
    public ApiResponse<List<Product>> listHotProducts() {
        return ApiResponse.success(productService.listHotProducts());
    }

    @GetMapping("/new")
    public ApiResponse<List<Product>> listNewProducts() {
        return ApiResponse.success(productService.listNewProducts());
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> getProductById(@PathVariable @Min(value = 1, message = "商品ID不能小于1") Long id) {
        return ApiResponse.success(productService.getProductById(id));
    }
}
