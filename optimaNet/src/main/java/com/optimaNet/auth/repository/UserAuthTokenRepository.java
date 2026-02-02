package com.optimaNet.auth.repository;

import com.optimaNet.auth.entity.UserAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthTokenRepository extends JpaRepository<UserAuthToken, Long> {
    Optional<UserAuthToken> findByRefreshTokenHashAndRevokedAtIsNull(String refreshTokenHash);

    Optional<UserAuthToken> findByUser_IdAndDevice_DeviceIdAndRevokedAtIsNull(Long userId, String deviceId);
}
