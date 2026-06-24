package com.freshfood.backend.controller;

import com.freshfood.backend.common.ApiResponse;
import com.freshfood.backend.entity.Address;
import com.freshfood.backend.service.AddressService;
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
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Address>> listByUserId(@PathVariable Long userId) {
        return ApiResponse.success(addressService.listByUserId(userId));
    }

    @PostMapping
    public ApiResponse<Address> createAddress(@RequestBody Address address) {
        return ApiResponse.success(addressService.createAddress(address));
    }

    @PutMapping("/{id}")
    public ApiResponse<Address> updateAddress(@PathVariable Long id, @RequestBody Address address) {
        return ApiResponse.success(addressService.updateAddress(id, address));
    }

    @PutMapping("/{id}/default")
    public ApiResponse<Address> setDefaultAddress(@PathVariable Long id) {
        return ApiResponse.success(addressService.setDefaultAddress(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ApiResponse.success(null);
    }
}