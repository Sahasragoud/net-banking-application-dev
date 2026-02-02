package com.optimaNet.auth.service;

import com.optimaNet.auth.dto.AuthResult;
import com.optimaNet.auth.dto.EmployeeLoginRequest;
import com.optimaNet.auth.dto.EmployeeLoginSessionDto;
import com.optimaNet.auth.dto.LoginSessionRequest;
import com.optimaNet.auth.entity.EmployeeLoginSession;
import com.optimaNet.exception.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeLoginOrchestratorService {
    Long initiateLogin(EmployeeLoginRequest request, String clientIp) throws EmployeeNotFoundException, AccessDeniedException;

    AuthResult completeLogin(LoginSessionRequest request, String clientIp) throws SessionNotFoundException, InvalidKYCDetailsException, OTPExpiredException, OTPBlockedException, OTPInvalidException, EmployeeNotFoundException, DeviceBlockedException, UserNotFoundException, EmployeeDeviceNotFoundException, UserDeviceNotFoundException, TokenNotFoundException;

    Page<EmployeeLoginSession> getAllByEmployeeIdAndIsActive(Long employeeId, Pageable pageable);

    Page<EmployeeLoginSessionDto> getAllEmployeeLoginSessions(Pageable pageable);


}
