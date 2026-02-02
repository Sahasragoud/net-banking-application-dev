package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.dto.AuthTokenPair;
import com.optimaNet.auth.entity.UserAuthToken;
import com.optimaNet.auth.entity.User;
import com.optimaNet.auth.entity.UserDevice;
import com.optimaNet.auth.enums.TokenRevokeReason;
import com.optimaNet.auth.enums.UserStatus;
import com.optimaNet.auth.repository.UserAuthTokenRepository;
import com.optimaNet.auth.repository.UserDeviceRepository;
import com.optimaNet.auth.service.AuthTokenService;
import com.optimaNet.exception.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthTokenServiceImpl implements AuthTokenService {

    private final UserDeviceRepository userDeviceRepository;
    private final UserAuthTokenRepository authTokenRepository;
    private final JwtService jwtService;


    public AuthTokenServiceImpl (UserDeviceRepository userDeviceRepository, UserAuthTokenRepository authTokenRepository, JwtService jwtService) {
        this.userDeviceRepository = userDeviceRepository;
        this.authTokenRepository = authTokenRepository;
        this.jwtService = jwtService;
    }

    @Override
    public AuthTokenPair issueTokens(Long userId, String deviceId) throws UserNotFoundException, InvalidKYCDetailsException, UserDeviceNotFoundException, DeviceBlockedException, TokenNotFoundException {

        UserDevice device = userDeviceRepository.findByUserIdAndDeviceId(userId, deviceId);
        if(device == null){
            throw new UserDeviceNotFoundException("Device Not found with id" + deviceId + " belonging to user with id + " + userId);
        }

        if(device.isBlocked()){
            throw new DeviceBlockedException("Device is blocked");
        }

        User user = device.getUser();
        if(user.getUserStatus() != UserStatus.ACTIVE){
            throw new InvalidKYCDetailsException("User id not active / got blocked");
        }

        //revoke existing token.
        authTokenRepository.findByUser_IdAndDevice_DeviceIdAndRevokedAtIsNull(userId, deviceId)
                .ifPresent(token -> {
                    token.setRevokedAt(LocalDateTime.now());
                    token.setRevokedReason(TokenRevokeReason.TOKEN_ROTATION);
                    authTokenRepository.save(token);
                });

        //generate tokens
        String accessToken = jwtService.generateAccessToken(userId, deviceId, user.getRole(), user.getUserStatus());
        String refreshToken = generateToken();

        //persist refresh token(hash)
        UserAuthToken token = new UserAuthToken();
        token.setUser(user);
        token.setDevice(device);
        token.setRefreshTokenHash(hash(refreshToken));
        token.setIssuedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusDays(30));

        authTokenRepository.save(token);

        return new AuthTokenPair(
                accessToken,
                refreshToken,
                15 * 60
        );
    }

    @Override
    public void revokeToken(Long tokenId, TokenRevokeReason reason) throws TokenNotFoundException {
        UserAuthToken token = authTokenRepository.findById(tokenId)
                .orElseThrow(() -> new TokenNotFoundException("Token not found with id" + tokenId));
        token.setRevokedAt(LocalDateTime.now());
        token.setRevokedReason(reason);

        authTokenRepository.save(token);
    }

    @Override
    public AuthTokenPair refreshTokens(String refreshToken) throws UserNotFoundException, InvalidKYCDetailsException, UserDeviceNotFoundException, DeviceBlockedException, TokenNotFoundException {

        String hashed = hash(refreshToken);

        UserAuthToken oldToken = authTokenRepository
                .findByRefreshTokenHashAndRevokedAtIsNull(hashed)
                .orElseThrow(() -> new SecurityException("Invalid refresh token"));

        // rotate
        oldToken.setRevokedAt(LocalDateTime.now());
        oldToken.setRevokedReason(TokenRevokeReason.TOKEN_ROTATION);

        String newAccessToken = jwtService.generateAccessToken(
                oldToken.getUser().getId(),
                oldToken.getDevice().getDeviceId(),
                oldToken.getUser().getRole(),
                oldToken.getUser().getUserStatus()
        );
        String newRefreshToken = generateToken();

        UserAuthToken newToken = new UserAuthToken();
        newToken.setUser(oldToken.getUser());
        newToken.setDevice(oldToken.getDevice());
        newToken.setRefreshTokenHash(hash(newRefreshToken));
        newToken.setIssuedAt(LocalDateTime.now());
        newToken.setExpiresAt(LocalDateTime.now().plusDays(15));

        authTokenRepository.save(oldToken);
        authTokenRepository.save(newToken);

        return new AuthTokenPair(
                newAccessToken,
                newRefreshToken,
                15 * 60
        );
    }

    @Override
    public void logout(Long userId, String deviceId) throws TokenNotFoundException {

        UserAuthToken token = authTokenRepository
                .findByUser_IdAndDevice_DeviceIdAndRevokedAtIsNull(userId, deviceId)
                .orElseThrow(() ->
                        new TokenNotFoundException("No active session found to logout")
                );

        token.setRevokedAt(LocalDateTime.now());
        token.setRevokedReason(TokenRevokeReason.LOGOUT);

        authTokenRepository.save(token);
    }

    @Override
    public Page<UserAuthToken> getAllAuthTokens(Pageable pageable) {
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
