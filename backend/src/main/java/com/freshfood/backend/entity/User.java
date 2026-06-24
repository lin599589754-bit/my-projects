package com.freshfood.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64, unique = true)
    private String openid;

    @Column(length = 64)
    private String unionid;

    @Column(name = "nick_name", length = 50)
    private String nickName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    private Byte gender;

    @Column(nullable = false)
    private Byte status;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
}