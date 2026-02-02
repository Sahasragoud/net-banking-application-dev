package com.optimaNet.auth.repository;

import com.optimaNet.auth.entity.KycApplication;
import com.optimaNet.auth.enums.KYCStatus;
import com.optimaNet.auth.enums.UserStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface KycApplicationRepository
        extends JpaRepository<KycApplication, Long> {

    Optional<KycApplication> findByUserId(Long userId);

    Page<KycApplication> findByStatus(KYCStatus status, Pageable pageable);

    Page<KycApplication> findByEmployeeId(Long employeeId, Pageable pageable);

    boolean existsByUserIdAndStatusIn(
            Long userId,
            Collection<KYCStatus> statuses
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT k from KycApplication k
            where k.id = :id AND k.status = 'KYC_PENDING'
            """)
    Optional<KycApplication> lockPendingById(Long id);


    @Modifying
    @Query("""
        UPDATE KycApplication k
        SET k.status = :newStatus
        WHERE k.id = :id AND k.status = :currentStatus
    """)
    int updateStatusIfCurrent(
            Long id,
            KYCStatus currentStatus,
            KYCStatus newStatus
    );
}
