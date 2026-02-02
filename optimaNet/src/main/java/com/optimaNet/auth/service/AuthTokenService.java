package com.optimaNet.auth.service;

import com.optimaNet.auth.dto.AuthTokenPair;
import com.optimaNet.auth.entity.UserAuthToken;
import com.optimaNet.auth.enums.TokenRevokeReason;
import com.optimaNet.exception.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthTokenService {

    AuthTokenPair issueTokens(Long userId, String deviceId) throws UserNotFoundException, InvalidKYCDetailsException, UserDeviceNotFoundException, DeviceBlockedException, TokenNotFoundException, EmployeeDeviceNotFoundException;

    void revokeToken(Long tokenId, TokenRevokeReason reason) throws TokenNotFoundException;

    AuthTokenPair refreshTokens(String refreshToken) throws UserNotFoundException, InvalidKYCDetailsException, UserDeviceNotFoundException, DeviceBlockedException, TokenNotFoundException;

    void logout(Long userId, String deviceId) throws TokenNotFoundException;

    Page<UserAuthToken> getAllAuthTokens(Pageable pageable);
}
