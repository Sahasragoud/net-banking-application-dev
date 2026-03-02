package com.optimaNet.v2.repository;

import com.optimaNet.v2.entity.CustomerProfileV2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerProfileV2Repository extends JpaRepository<CustomerProfileV2, Long> {
    Optional<CustomerProfileV2> findByCustomer_Id(Long customerId);
}
