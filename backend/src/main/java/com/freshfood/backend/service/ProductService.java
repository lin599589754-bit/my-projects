package com.freshfood.backend.service;

import com.freshfood.backend.entity.Product;
import com.freshfood.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private static final Byte ENABLED_STATUS = (byte) 1;
    private static final Byte YES = (byte) 1;

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> listOnSaleProducts() {
        return productRepository.findByStatusOrderBySortOrderAscIdAsc(ENABLED_STATUS);
    }

    public List<Product> listProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndStatusOrderBySortOrderAscIdAsc(categoryId, ENABLED_STATUS);
    }

    public List<Product> listHotProducts() {
        return productRepository.findByIsHotAndStatusOrderBySortOrderAscIdAsc(YES, ENABLED_STATUS);
    }

    public List<Product> listNewProducts() {
        return productRepository.findByIsNewAndStatusOrderBySortOrderAscIdAsc(YES, ENABLED_STATUS);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}