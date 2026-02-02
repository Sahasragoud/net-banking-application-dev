package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.dto.AuthTokenPair;
import com.optimaNet.auth.entity.*;
import com.optimaNet.auth.enums.EmployeeStatus;
import com.optimaNet.auth.enums.TokenRevokeReason;
import com.optimaNet.auth.repository.EmployeeAuthTokenRepository;
import com.optimaNet.auth.repository.EmployeeDeviceRepository;
import com.optimaNet.auth.service.EmployeeAuthTokenService;
import com.optimaNet.exception.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeAuthTokenServiceImpl implements EmployeeAuthTokenService {

    private final EmployeeDeviceRepository empDeviceRepository;
    private final EmployeeAuthTokenRepository authTokenRepository;
    private final JwtService jwtService;
    private final TokenPolicy tokenPolicy;

    @Override
    public AuthTokenPair issueTokens(Long employeeId, String deviceId) throws EmployeeDeviceNotFoundException, DeviceBlockedException, InvalidKYCDetailsException {
        EmployeeDevice device = empDeviceRepository.findByEmployeeIdAndDeviceId(employeeId, deviceId);
        if(device == null){
            throw new EmployeeDeviceNotFoundException("Device Not found with id" + deviceId + " belonging to employee with id + " + employeeId);
        }

        if(device.isBlocked()){
            throw new DeviceBlockedException("Device is blocked");
        }

        Employee employee = device.getEmployee();
        if(employee.getStatus() != EmployeeStatus.ACTIVE){
            throw new InvalidKYCDetailsException("User id not active / got blocked");
        }

        //revoke existing token.
        authTokenRepository.findByEmployee_IdAndDevice_DeviceIdAndRevokedAtIsNull(employeeId, deviceId)
                .ifPresent(token -> {
                    token.setRevokedAt(LocalDateTime.now());
                    token.setRevokedReason(TokenRevokeReason.TOKEN_ROTATION);
                    authTokenRepository.save(token);
                });

        //generate tokens
        String accessToken = jwtService.generateAccessToken(employeeId, deviceId, employee.getRole(), employee.getStatus());
        String refreshToken = generateToken();

        //persist refresh token(hash)
        EmployeeAuthToken token = new EmployeeAuthToken();
        token.setEmployee(employee);
        token.setDevice(device);
        token.setRefreshTokenHash(hash(refreshToken));
        token.setIssuedAt(LocalDateTime.now());
        token.setExpiresAt(
                LocalDateTime.now().plusDays(tokenPolicy.getRefreshTokenTtlDays())
        );
        token.setRevokedAt(null);
        authTokenRepository.save(token);

        return new AuthTokenPair(
                accessToken,
                refreshToken,
                tokenPolicy.getAccessTokenTtlSeconds()
        );
    }

    @Override
    public void revokeToken(Long tokenId, TokenRevokeReason reason) throws TokenNotFoundException {
        EmployeeAuthToken token = authTokenRepository.findById(tokenId)
                .orElseThrow(() -> new TokenNotFoundException("Token not found with id" + tokenId));
        token.setRevokedAt(LocalDateTime.now());
        token.setRevokedReason(reason);

        authTokenRepository.save(token);
    }

    @Override
    public AuthTokenPair refreshTokens(String refreshToken) throws UserNotFoundException, InvalidKYCDetailsException, UserDeviceNotFoundException, DeviceBlockedException, TokenNotFoundException {

        String hashed = hash(refreshToken);

        EmployeeAuthToken oldToken = authTokenRepository
                .findByRefreshTokenHashAndRevokedAtIsNull(hashed)
                .orElseThrow(() -> new SecurityException("Invalid refresh token"));

        // rotate
        oldToken.setRevokedAt(LocalDateTime.now());
        oldToken.setRevokedReason(TokenRevokeReason.TOKEN_ROTATION);

        String newAccessToken = jwtService.generateAccessToken(
                oldToken.getEmployee().getId(),
                oldToken.getDevice().getDeviceId(),
                oldToken.getEmployee().getRole(),
                oldToken.getEmployee().getStatus()
        );
        String newRefreshToken = generateToken();

        EmployeeAuthToken newToken = new EmployeeAuthToken();
        newToken.setEmployee(oldToken.getEmployee());
        newToken.setDevice(oldToken.getDevice());
        newToken.setRefreshTokenHash(hash(newRefreshToken));
        newToken.setIssuedAt(LocalDateTime.now());
        newToken.setExpiresAt(
                LocalDateTime.now().plusDays(tokenPolicy.getRefreshTokenTtlDays())

        );

        authTokenRepository.save(oldToken);
        authTokenRepository.save(newToken);

        return new AuthTokenPair(
                newAccessToken,
                newRefreshToken,
                15 * 60
        );
    }

    @Override
    public void logout(Long empId, String deviceId) throws TokenNotFoundException {

        EmployeeAuthToken token = authTokenRepository
                .findByEmployee_IdAndDevice_DeviceIdAndRevokedAtIsNull(empId, deviceId)
                .orElseThrow(() ->
                        new TokenNotFoundException("No active session found to logout")
                );

        token.setRevokedAt(LocalDateTime.now());
        token.setRevokedReason(TokenRevokeReason.LOGOUT);

        authTokenRepository.save(token);
    }

    @Override
    public Page<EmployeeAuthToken> getAllAuthTokens(Pageable pageable) {
        return authTokenRepository.findAll(pageable);
    }


    //helpers
    private String generateToken(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String hash(String token){
        return DigestUtils.sha256Hex(token);
    }

}
