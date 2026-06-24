package com.freshfood.backend.controller;

import com.freshfood.backend.common.ApiResponse;
import com.freshfood.backend.dto.AddressRequest;
import com.freshfood.backend.entity.Address;
import com.freshfood.backend.service.AddressService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Address>> listByUserId(@PathVariable @Min(value = 1, message = "用户ID不能小于1") Long userId) {
        return ApiResponse.success(addressService.listByUserId(userId));
    }

    @PostMapping
    public ApiResponse<Address> createAddress(@Valid @RequestBody AddressRequest addressRequest) {
        return ApiResponse.success(addressService.createAddress(addressRequest.toEntity()));
    }

    @PutMapping("/{id}")
    public ApiResponse<Address> updateAddress(@PathVariable @Min(value = 1, message = "地址ID不能小于1") Long id,
                                              @Valid @RequestBody AddressRequest addressRequest) {
        return ApiResponse.success(addressService.updateAddress(id, addressRequest.toEntity()));
    }

    @PutMapping("/{id}/default")
    public ApiResponse<Address> setDefaultAddress(@PathVariable @Min(value = 1, message = "地址ID不能小于1") Long id) {
        return ApiResponse.success(addressService.setDefaultAddress(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable @Min(value = 1, message = "地址ID不能小于1") Long id) {
        addressService.deleteAddress(id);
        return ApiResponse.success(null);
    }
}
