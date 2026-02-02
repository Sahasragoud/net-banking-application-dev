package com.optimaNet.auth.repository;

import com.optimaNet.auth.entity.OTPRequest;
import com.optimaNet.auth.enums.OTPPurpose;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRequestRepository extends JpaRepository<OTPRequest, Long> {

    Optional<OTPRequest> findTopByUserIdAndPurposeOrderByCreatedAtDesc(Long userId, OTPPurpose purpose);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OTPRequest> findById(Long id);

    Page<OTPRequest> findAllByUserId(Long UserId, Pageable pageable);

    Page<OTPRequest> findAll(Pageable pageable);


}
