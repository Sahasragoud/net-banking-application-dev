package com.optimaNet.v2.repository;

import com.optimaNet.v2.entity.SavingsAccountV2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavingsAccountV2Repository extends JpaRepository<SavingsAccountV2, Long> {
    Optional<SavingsAccountV2> findByAccountNumber(String accountNumber);
    List<SavingsAccountV2> findByCustomer_Id(Long customerId);
}
