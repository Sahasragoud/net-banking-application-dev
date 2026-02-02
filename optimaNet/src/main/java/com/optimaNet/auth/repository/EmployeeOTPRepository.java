package com.optimaNet.auth.repository;

import com.optimaNet.auth.entity.EmployeeOTPRequest;
import com.optimaNet.auth.enums.OTPPurpose;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeOTPRepository extends JpaRepository<EmployeeOTPRequest, Long> {

    Optional<EmployeeOTPRequest> findTopByEmployeeIdAndPurposeOrderByCreatedAtDesc(Long employeeId, OTPPurpose purpose);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EmployeeOTPRequest> findById(Long id);

    Page<EmployeeOTPRequest> findAllByEmployeeId(Long employeeId, Pageable pageable);

    Page<EmployeeOTPRequest> findAll(Pageable pageable);

    void deleteAllByEmployeeIdAndPurpose(Long employeeId, OTPPurpose purpose);



}
