package com.freshfood.backend.repository;

import com.freshfood.backend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserIdOrderByIsDefaultDescIdDesc(Long userId);

    List<Address> findByUserIdAndIsDefault(Long userId, Byte isDefault);
}