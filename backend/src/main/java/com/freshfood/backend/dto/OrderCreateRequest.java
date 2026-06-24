package com.freshfood.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderCreateRequest {

    @NotNull(message = "用户ID不能为空")
    @Min(value = 1, message = "用户ID不能小于1")
    private Long userId;

    @NotNull(message = "地址ID不能为空")
    @Min(value = 1, message = "地址ID不能小于1")
    private Long addressId;

    @Size(max = 200, message = "订单备注不能超过200")
    private String userRemark;
}
