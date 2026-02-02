package com.optimaNet.auth.service;

import com.optimaNet.auth.dto.AuthResult;
import com.optimaNet.auth.dto.EmployeeLoginRequest;
import com.optimaNet.auth.entity.Employee;
import com.optimaNet.auth.entity.EmployeeLoginSession;
import com.optimaNet.auth.entity.LoginSession;
import com.optimaNet.exception.EmployeeNotFoundException;
import com.optimaNet.exception.InvalidKYCDetailsException;
import com.optimaNet.exception.UserIdentityNotFoundException;
import com.optimaNet.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeLoginSessionService {
    EmployeeLoginSession createSession(Long employeeId, String clientIp) throws EmployeeNotFoundException;

    void updateLastActivity(Long sessionId);

    void terminateSessionsActive(Long employeeId) throws EmployeeNotFoundException;

}
