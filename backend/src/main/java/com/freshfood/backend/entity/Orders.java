package com.freshfood.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", nullable = false, length = 32, unique = true)
    private String orderNo;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "freight_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal freightAmount;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "actual_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal actualAmount;

    @Column(name = "payment_method")
    private Byte paymentMethod;

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Column(name = "order_status", nullable = false)
    private Byte orderStatus;

    @Column(name = "tracking_no", length = 64)
    private String trackingNo;

    @Column(name = "ship_time")
    private LocalDateTime shipTime;

    @Column(name = "receive_time")
    private LocalDateTime receiveTime;

    @Column(name = "receiver_name", nullable = false, length = 50)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    @Column(name = "receiver_address", nullable = false)
    private String receiverAddress;

    @Column(name = "user_remark", length = 200)
    private String userRemark;

    @Column(name = "close_reason", length = 100)
    private String closeReason;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @Transient
    private List<OrderItem> orderItems;
}
