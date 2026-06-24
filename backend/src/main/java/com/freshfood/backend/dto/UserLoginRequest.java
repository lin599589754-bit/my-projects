package com.freshfood.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginRequest {

    @NotBlank(message = "openid不能为空")
    @Size(max = 64, message = "openid长度不能超过64")
    private String openid;

    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickName;

    @Size(max = 255, message = "头像地址长度不能超过255")
    private String avatarUrl;
}
