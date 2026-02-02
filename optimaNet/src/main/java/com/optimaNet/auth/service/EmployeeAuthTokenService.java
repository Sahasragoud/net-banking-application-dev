package com.optimaNet.auth.service;

import com.optimaNet.auth.dto.AuthTokenPair;
import com.optimaNet.auth.entity.EmployeeAuthToken;
import com.optimaNet.auth.entity.UserAuthToken;
import com.optimaNet.auth.enums.TokenRevokeReason;
import com.optimaNet.exception.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeAuthTokenService {

    AuthTokenPair issueTokens(Long employeeId, String deviceId) throws EmployeeDeviceNotFoundException, DeviceBlockedException, InvalidKYCDetailsException;

    void revokeToken(Long tokenId, TokenRevokeReason reason) throws TokenNotFoundException;

    AuthTokenPair refreshTokens(String refreshToken) throws UserNotFoundException, InvalidKYCDetailsException, UserDeviceNotFoundException, DeviceBlockedException, TokenNotFoundException;

    void logout(Long employeeId, String deviceId) throws TokenNotFoundException;

    Page<EmployeeAuthToken> getAllAuthTokens(Pageable pageable);
}
