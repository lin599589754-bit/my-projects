package com.freshfood.backend.controller;

import com.freshfood.backend.common.ApiResponse;
import com.freshfood.backend.dto.AddressRequest;
import com.freshfood.backend.dto.CurrentAddressRequest;
import com.freshfood.backend.entity.Address;
import com.freshfood.backend.security.CurrentUser;
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
    private final CurrentUser currentUser;

    public AddressController(AddressService addressService, CurrentUser currentUser) {
        this.addressService = addressService;
        this.currentUser = currentUser;
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Address>> listByUserId(@PathVariable @Min(value = 1, message = "用户ID不能小于1") Long userId) {
        currentUser.requireSameUser(userId);
        return ApiResponse.success(addressService.listByUserId(userId));
    }

    @GetMapping("/current")
    public ApiResponse<List<Address>> listCurrentUserAddresses() {
        return ApiResponse.success(addressService.listByUserId(currentUser.getUserId()));
    }

    @PostMapping
    public ApiResponse<Address> createAddress(@Valid @RequestBody AddressRequest addressRequest) {
        currentUser.requireSameUser(addressRequest.getUserId());
        return ApiResponse.success(addressService.createAddress(addressRequest.toEntity()));
    }

    @PostMapping("/current")
    public ApiResponse<Address> createCurrentUserAddress(@Valid @RequestBody CurrentAddressRequest addressRequest) {
        return ApiResponse.success(addressService.createAddress(addressRequest.toEntity(currentUser.getUserId())));
    }

    @PutMapping("/{id}")
    public ApiResponse<Address> updateAddress(@PathVariable @Min(value = 1, message = "地址ID不能小于1") Long id,
                                              @Valid @RequestBody AddressRequest addressRequest) {
        currentUser.requireSameUser(addressRequest.getUserId());
        return ApiResponse.success(addressService.updateAddressForUser(addressRequest.getUserId(), id, addressRequest.toEntity()));
    }

    @PutMapping("/current/{id}")
    public ApiResponse<Address> updateCurrentUserAddress(@PathVariable @Min(value = 1, message = "地址ID不能小于1") Long id,
                                                         @Valid @RequestBody CurrentAddressRequest addressRequest) {
        Long userId = currentUser.getUserId();
        return ApiResponse.success(addressService.updateAddressForUser(userId, id, addressRequest.toEntity(userId)));
    }

    @PutMapping("/{id}/default")
    public ApiResponse<Address> setDefaultAddress(@PathVariable @Min(value = 1, message = "地址ID不能小于1") Long id) {
        return ApiResponse.success(addressService.setDefaultAddressForUser(currentUser.getUserId(), id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable @Min(value = 1, message = "地址ID不能小于1") Long id) {
        addressService.deleteAddressForUser(currentUser.getUserId(), id);
        return ApiResponse.success(null);
    }
}
