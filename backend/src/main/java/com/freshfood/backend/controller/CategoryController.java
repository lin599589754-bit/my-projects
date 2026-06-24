package com.freshfood.backend.controller;

import com.freshfood.backend.common.ApiResponse;
import com.freshfood.backend.entity.Category;
import com.freshfood.backend.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api/categories")
    public ApiResponse<List<Category>> listCategories() {
        return ApiResponse.success(categoryService.listEnabledCategories());
    }
}