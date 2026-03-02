package com.optimaNet.v2.repository;

import com.optimaNet.v2.entity.NomineeV2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NomineeV2Repository extends JpaRepository<NomineeV2, Long> {
    List<NomineeV2> findByCustomer_IdOrderByIdAsc(Long customerId);
    void deleteByCustomer_Id(Long customerId);
}
