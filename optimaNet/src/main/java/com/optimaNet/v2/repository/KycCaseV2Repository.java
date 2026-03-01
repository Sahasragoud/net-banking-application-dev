package com.optimaNet.v2.repository;

import com.optimaNet.v2.entity.KycCaseV2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycCaseV2Repository extends JpaRepository<KycCaseV2, Long> {
    Optional<KycCaseV2> findByCustomer_Id(Long customerId);
}
