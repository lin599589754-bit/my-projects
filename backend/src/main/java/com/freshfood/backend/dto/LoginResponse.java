package com.freshfood.backend.dto;

import com.freshfood.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String tokenType;

    private String token;

    private Long expiresIn;

    private User user;
}
