package com.optimaNet.v2.repository;

import com.optimaNet.v2.entity.SavingsTransactionV2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsTransactionV2Repository extends JpaRepository<SavingsTransactionV2, Long> {
    List<SavingsTransactionV2> findTop20BySavingsAccountAccountNumberOrderByCreatedAtDesc(String accountNumber);
}
