package com.freshfood.backend.service;

import com.freshfood.backend.entity.Category;
import com.freshfood.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> listEnabledCategories() {
        return categoryRepository.findByStatusOrderBySortOrderAscIdAsc((byte) 1);
    }
}