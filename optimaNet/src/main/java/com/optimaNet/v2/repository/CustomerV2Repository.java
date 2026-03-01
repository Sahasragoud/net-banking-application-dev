package com.optimaNet.v2.repository;

import com.optimaNet.v2.entity.CustomerV2;
import com.optimaNet.v2.enums.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerV2Repository extends JpaRepository<CustomerV2, Long> {
    Optional<CustomerV2> findByCustomerCode(String customerCode);
    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
    List<CustomerV2> findByCustomerStatus(CustomerStatus status);
}
