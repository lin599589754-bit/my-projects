package com.freshfood.backend.service;

import com.freshfood.backend.entity.Cart;
import com.freshfood.backend.entity.Product;
import com.freshfood.backend.repository.CartRepository;
import com.freshfood.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {

    private static final Byte ON_SALE = (byte) 1;
    private static final Byte SELECTED = (byte) 1;
    private static final Byte UNSELECTED = (byte) 0;

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public List<Cart> listByUserId(Long userId) {
        return cartRepository.findByUserIdOrderByIdDesc(userId);
    }

    public List<Cart> listSelectedByUserId(Long userId) {
        return cartRepository.findByUserIdAndSelectedOrderByIdDesc(userId, SELECTED);
    }

    @Transactional
    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            quantity = 1;
        }

        Product product = productRepository.findById(productId).orElse(null);

        if (product == null || !ON_SALE.equals(product.getStatus())) {
            throw new RuntimeException("商品不存在或已下架");
        }

        Cart cart = cartRepository.findByUserIdAndProductId(userId, productId)
                .orElseGet(Cart::new);

        Integer newQuantity;

        if (cart.getId() == null) {
            newQuantity = quantity;
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setSelected(SELECTED);
            cart.setCreateTime(LocalDateTime.now());
        } else {
            newQuantity = cart.getQuantity() + quantity;
        }

        if (product.getStock() < newQuantity) {
            throw new RuntimeException("库存不足");
        }

        cart.setQuantity(newQuantity);
        cart.setUpdateTime(LocalDateTime.now());

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateQuantity(Long id, Integer quantity) {
        Cart cart = cartRepository.findById(id).orElse(null);

        if (cart == null) {
            return null;
        }

        if (quantity == null || quantity <= 0) {
            cartRepository.delete(cart);
            return null;
        }

        Product product = productRepository.findById(cart.getProductId()).orElse(null);

        if (product == null || !ON_SALE.equals(product.getStatus())) {
            throw new RuntimeException("商品不存在或已下架");
        }

        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        cart.setQuantity(quantity);
        cart.setUpdateTime(LocalDateTime.now());

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateSelected(Long id, Byte selected) {
        Cart cart = cartRepository.findById(id).orElse(null);

        if (cart == null) {
            return null;
        }

        if (selected == null) {
            selected = UNSELECTED;
        }

        cart.setSelected(selected);
        cart.setUpdateTime(LocalDateTime.now());

        return cartRepository.save(cart);
    }

    @Transactional
    public void deleteCart(Long id) {
        cartRepository.deleteById(id);
    }

    @Transactional
    public void clearByUserId(Long userId) {
        cartRepository.deleteByUserId(userId);
    }

    @Transactional
    public void clearSelectedByUserId(Long userId) {
        cartRepository.deleteByUserIdAndSelected(userId, SELECTED);
    }
}