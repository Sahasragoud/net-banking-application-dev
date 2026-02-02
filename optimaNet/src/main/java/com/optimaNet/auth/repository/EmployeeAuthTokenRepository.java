package com.optimaNet.auth.repository;

import com.optimaNet.auth.entity.EmployeeAuthToken;
import com.optimaNet.auth.entity.UserAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeAuthTokenRepository extends JpaRepository<EmployeeAuthToken, Long> {
    Optional<EmployeeAuthToken> findByRefreshTokenHashAndRevokedAtIsNull(String refreshTokenHash);

    Optional<EmployeeAuthToken> findByEmployee_IdAndDevice_DeviceIdAndRevokedAtIsNull(Long employeeId, String deviceId);

}
