package com.freshfood.backend.service;

import com.freshfood.backend.entity.Address;
import com.freshfood.backend.repository.AddressRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressService {

    private static final Byte YES = (byte) 1;
    private static final Byte NO = (byte) 0;

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<Address> listByUserId(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDescIdDesc(userId);
    }

    @Transactional
    public Address createAddress(Address address) {
        LocalDateTime now = LocalDateTime.now();

        if (address.getIsDefault() == null) {
            address.setIsDefault(NO);
        }

        if (YES.equals(address.getIsDefault())) {
            clearDefaultAddress(address.getUserId());
        }

        address.setCreateTime(now);
        address.setUpdateTime(now);

        return addressRepository.save(address);
    }

    @Transactional
    public Address updateAddress(Long id, Address address) {
        Address existingAddress = addressRepository.findById(id).orElse(null);

        if (existingAddress == null) {
            return null;
        }

        if (address.getReceiverName() != null) {
            existingAddress.setReceiverName(address.getReceiverName());
        }

        if (address.getReceiverPhone() != null) {
            existingAddress.setReceiverPhone(address.getReceiverPhone());
        }

        if (address.getProvince() != null) {
            existingAddress.setProvince(address.getProvince());
        }

        if (address.getCity() != null) {
            existingAddress.setCity(address.getCity());
        }

        if (address.getDistrict() != null) {
            existingAddress.setDistrict(address.getDistrict());
        }

        if (address.getDetailAddress() != null) {
            existingAddress.setDetailAddress(address.getDetailAddress());
        }

        if (address.getLabel() != null) {
            existingAddress.setLabel(address.getLabel());
        }

        if (address.getIsDefault() != null) {
            if (YES.equals(address.getIsDefault())) {
                clearDefaultAddress(existingAddress.getUserId());
            }

            existingAddress.setIsDefault(address.getIsDefault());
        }

        existingAddress.setUpdateTime(LocalDateTime.now());

        return addressRepository.save(existingAddress);
    }

    @Transactional
    public Address updateAddressForUser(Long userId, Long id, Address address) {
        Address existingAddress = getOwnedAddress(userId, id);

        if (existingAddress == null) {
            return null;
        }

        return updateAddress(id, address);
    }

    @Transactional
    public Address setDefaultAddress(Long id) {
        Address address = addressRepository.findById(id).orElse(null);

        if (address == null) {
            return null;
        }

        clearDefaultAddress(address.getUserId());

        address.setIsDefault(YES);
        address.setUpdateTime(LocalDateTime.now());

        return addressRepository.save(address);
    }

    @Transactional
    public Address setDefaultAddressForUser(Long userId, Long id) {
        Address address = getOwnedAddress(userId, id);

        if (address == null) {
            return null;
        }

        return setDefaultAddress(id);
    }

    @Transactional
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }

    @Transactional
    public void deleteAddressForUser(Long userId, Long id) {
        Address address = getOwnedAddress(userId, id);

        if (address != null) {
            addressRepository.deleteById(id);
        }
    }

    private void clearDefaultAddress(Long userId) {
        List<Address> defaultAddresses = addressRepository.findByUserIdAndIsDefault(userId, YES);

        for (Address address : defaultAddresses) {
            address.setIsDefault(NO);
            address.setUpdateTime(LocalDateTime.now());
            addressRepository.save(address);
        }
    }

    private Address getOwnedAddress(Long userId, Long id) {
        Address address = addressRepository.findById(id).orElse(null);

        if (address == null) {
            return null;
        }

        if (!userId.equals(address.getUserId())) {
            throw new AccessDeniedException("不能访问其他用户的数据");
        }

        return address;
    }
}
