package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.dto.*;
import com.optimaNet.auth.entity.*;
import com.optimaNet.auth.enums.EmployeeStatus;
import com.optimaNet.auth.enums.LoginSessionStatus;
import com.optimaNet.auth.enums.OTPPurpose;
import com.optimaNet.auth.repository.EmployeeLoginSessionRepository;
import com.optimaNet.auth.repository.EmployeeRepository;
import com.optimaNet.auth.service.*;
import com.optimaNet.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeLoginOrchestratorServiceImpl implements EmployeeLoginOrchestratorService {

    private final EmployeeLoginSessionService loginSessionService;
    private final EmployeeOTPService otpService;
    private final EmployeeLoginSessionRepository sessionRepository;
    private final EmployeeDeviceService deviceService;
    private final EmployeeAuthTokenService authTokenService;
    private final EmployeeRepository employeeRepository;

    @Override
    public Long initiateLogin(EmployeeLoginRequest request, String clientIp) throws EmployeeNotFoundException, AccessDeniedException {

        Employee employee =  employeeRepository.findByEmployeeCode(request.getEmployeeCode())
                        .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with code: " + request.getEmployeeCode())
        );
        System.out.println("LOGIN INITIATE HIT for employee=" + employee.getId());

        if(employee.getStatus() != EmployeeStatus.ACTIVE){
            throw new AccessDeniedException("Employee is not allowed to login");
        }

        System.out.println("LOGIN INITIATE HIT for employee - status=" + employee.getStatus());

        loginSessionService.terminateSessionsActive(employee.getId());

        System.out.println("LOGIN INITIATE HIT for terminating active sessions");

        EmployeeLoginSession session = loginSessionService.createSession(employee.getId(), clientIp);

        System.out.println("LOGIN INITIATE HIT for creating LoginSession=" + session.getId());

        otpService.generateOTP(employee.getId(), OTPPurpose.LOGIN);

        System.out.println("LOGIN INITIATE HIT for GenerateOtp");

        session.setLoginSessionStatus(LoginSessionStatus.OTP_PENDING);
        sessionRepository.save(session);

        System.out.println("LOGIN INITIATE HIT for Session saved");

        return session.getId();
    }

    @Override
    public AuthResult completeLogin(LoginSessionRequest request, String ipAddress) throws SessionNotFoundException, InvalidKYCDetailsException, OTPExpiredException, OTPBlockedException, OTPInvalidException, EmployeeNotFoundException, DeviceBlockedException, UserNotFoundException, EmployeeDeviceNotFoundException, UserDeviceNotFoundException, TokenNotFoundException {
        //validate login session.
        EmployeeLoginSession session = sessionRepository.findByIdAndIsActiveTrue(request.getSessionId())
                .orElseThrow(() -> new SessionNotFoundException("No session is found with id : " + request.getSessionId()));

        System.out.println("LOGIN VERIFY HIT for Employee-session=" + session.getLastActivityAt());

        LoginSessionStatus sessionStatus = session.getLoginSessionStatus();
        if(sessionStatus == LoginSessionStatus.AUTHENTICATED ||
                sessionStatus == LoginSessionStatus.TERMINATED){
            throw new RuntimeException("Session had been used");
        }
        System.out.println("LOGIN VERIFY HIT for Session-usage=" + session.getLoginSessionStatus());

        if(!session.isActive() ||
            session.getLoginSessionStatus() == LoginSessionStatus.TERMINATED){
            throw new RuntimeException("Session has expired");
        }

        System.out.println("LOGIN VERIFY HIT for Session-terminated?=" + session.getLoginSessionStatus());

        //validate user context
        Employee employee = session.getEmployee();
        if (session.getEmployee().getId() != employee.getId()) {
            throw new SecurityException("Session-user mismatch");
        }
        System.out.println("LOGIN VERIFY HIT for Session-employee=" + session.getEmployee().getId());

        if(employee.getStatus() != EmployeeStatus.ACTIVE){
            throw new InvalidKYCDetailsException("Employee is not active");
        }
        System.out.println("LOGIN VERIFY HIT for Session-employee-status=" + session.getEmployee().getStatus());


        //verify otp
        boolean verify = otpService.verifyOTP(employee.getId(), request.getOtp(), OTPPurpose.LOGIN);

        if(!verify){
            throw new OTPInvalidException("Invalid otp, OTPs didn't match");
        }
        System.out.println("LOGIN VERIFY HIT for Session-otp-verified=");

        // extract user device information.
        EmployeeDevice device = deviceService.registerOrUpdateDevice(
                employee.getId(),
                request.getDeviceInfo(),
                ipAddress
        );
        System.out.println("LOGIN VERIFY HIT for EmployeeDevice created");

        //attach authentication
        AuthTokenPair pair = authTokenService.issueTokens(
                employee.getId(), device.getDeviceId()
        );
        System.out.println("LOGIN VERIFY HIT for Issue token=" + pair.getAccessToken());


        //finalize login session
        session.setLoginSessionStatus(LoginSessionStatus.AUTHENTICATED);
        session.setLastActivityAt(LocalDateTime.now());
        sessionRepository.save(session);

        System.out.println("LOGIN VERIFY HIT for session-authenticated");

        return new AuthResult(
                pair.getAccessToken(),
                pair.getRefreshToken(),
                pair.getExpiresIn(),
                employee.getId(),
                employee.getRole()
        );
    }

    @Override
    public Page<EmployeeLoginSession> getAllByEmployeeIdAndIsActive(Long employeeId, Pageable pageable) {
        return sessionRepository.findAllByEmployeeIdAndIsActiveTrue(employeeId, pageable);
    }

    @Override
    public Page<EmployeeLoginSessionDto> getAllEmployeeLoginSessions(Pageable pageable) {
        System.out.println("GET EMPLOYEE SESSIONS HIT sessionRepository");
        return sessionRepository.findAllProjected(pageable);
    }
}
