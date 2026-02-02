package com.optimaNet.auth.repository;

import com.optimaNet.auth.entity.UserIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserIdentityRepository extends JpaRepository<UserIdentity, Long> {
    Optional<UserIdentity> findByUserId(Long userId);

    boolean existsByEncryptedAadhaarNumber(String aadhaarNumber);

    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByEmail(String email);

    Optional<UserIdentity> findByEmail(String email);

    Optional<UserIdentity> findByMobileNumber(String mobileNumber);
}
