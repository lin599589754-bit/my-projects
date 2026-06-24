package com.freshfood.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "main_image")
    private String mainImage;

    @Column(name = "detail_images", columnDefinition = "text")
    private String detailImages;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "sale_volume", nullable = false)
    private Integer saleVolume;

    @Column(name = "is_hot", nullable = false)
    private Byte isHot;

    @Column(name = "is_new", nullable = false)
    private Byte isNew;

    @Column(nullable = false)
    private Byte status;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
}