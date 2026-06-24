package com.freshfood.backend.dto;

import com.freshfood.backend.entity.Address;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressRequest {

    @NotNull(message = "用户ID不能为空")
    @Min(value = 1, message = "用户ID不能小于1")
    private Long userId;

    @NotBlank(message = "收货人不能为空")
    @Size(max = 50, message = "收货人长度不能超过50")
    private String receiverName;

    @NotBlank(message = "收货电话不能为空")
    @Size(max = 20, message = "收货电话长度不能超过20")
    private String receiverPhone;

    @NotBlank(message = "省份不能为空")
    @Size(max = 30, message = "省份长度不能超过30")
    private String province;

    @NotBlank(message = "城市不能为空")
    @Size(max = 30, message = "城市长度不能超过30")
    private String city;

    @NotBlank(message = "区县不能为空")
    @Size(max = 30, message = "区县长度不能超过30")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "详细地址长度不能超过200")
    private String detailAddress;

    @Size(max = 20, message = "标签长度不能超过20")
    private String label;

    @Min(value = 0, message = "默认地址状态只能是0或1")
    @Max(value = 1, message = "默认地址状态只能是0或1")
    private Byte isDefault;

    public Address toEntity() {
        Address address = new Address();
        address.setUserId(userId);
        address.setReceiverName(receiverName);
        address.setReceiverPhone(receiverPhone);
        address.setProvince(province);
        address.setCity(city);
        address.setDistrict(district);
        address.setDetailAddress(detailAddress);
        address.setLabel(label);
        address.setIsDefault(isDefault);
        return address;
    }
}
