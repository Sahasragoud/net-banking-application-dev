package com.optimaNet.auth.service;


import com.optimaNet.auth.dto.AuthResult;
import com.optimaNet.auth.dto.LoginRequest;
import com.optimaNet.auth.dto.LoginSessionRequest;
import com.optimaNet.auth.entity.LoginSession;
import com.optimaNet.exception.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoginOrchestratorService {
    Long initiateLogin(LoginRequest request) throws UserIdentityNotFoundException, InvalidKYCDetailsException, UserNotFoundException;

    AuthResult completeLogin(LoginSessionRequest request, String clientIp) throws SessionNotFoundException, UserNotFoundException, OTPExpiredException, OTPBlockedException, OTPInvalidException, UserDeviceNotFoundException, InvalidKYCDetailsException, DeviceBlockedException, TokenNotFoundException, UserIdentityNotFoundException, EmployeeDeviceNotFoundException;

    Page<LoginSession> getAllByUserIdAndIsActive(Long userId, Pageable pageable);

    Page<LoginSession> getAllLoginSessions(Pageable pageable);

}
