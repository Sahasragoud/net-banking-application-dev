package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.dto.AuthResult;
import com.optimaNet.auth.dto.AuthTokenPair;
import com.optimaNet.auth.dto.LoginRequest;
import com.optimaNet.auth.dto.LoginSessionRequest;
import com.optimaNet.auth.entity.*;
import com.optimaNet.auth.enums.LoginSessionStatus;
import com.optimaNet.auth.enums.OTPPurpose;
import com.optimaNet.auth.enums.UserStatus;
import com.optimaNet.auth.repository.LoginSessionRepository;
import com.optimaNet.auth.repository.UserRepository;
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
public class LoginOrchestratorServiceImpl implements LoginOrchestratorService {

    private final LoginSessionService loginSessionService;
    private final OTPService otpService;
    private final LoginSessionRepository sessionRepository;
    private final UserDeviceService deviceService;
    private final AuthTokenService authTokenService;
    private final UserRepository userRepository;

    @Override
    public Long initiateLogin(LoginRequest request) throws InvalidKYCDetailsException, UserNotFoundException, UserIdentityNotFoundException {

        User user = userRepository.findByCustomerId(request.getCustomerId())
                .orElseThrow(() -> new UserNotFoundException("User not found with custerId :" + request.getCustomerId()));

        if(user.getUserStatus() != UserStatus.ACTIVE){
            throw new InvalidKYCDetailsException("User is not allowed to login");
        }

        loginSessionService.terminateSessionsActive(user.getId());

        LoginSession session = loginSessionService.createSession(user.getId());

        otpService.generateOTP(user.getId(), OTPPurpose.LOGIN);

        session.setLoginSessionStatus(LoginSessionStatus.OTP_PENDING);
        sessionRepository.save(session);

        return session.getId();
    }

    @Override
    public AuthResult completeLogin(LoginSessionRequest request, String clientIp)
            throws SessionNotFoundException, UserNotFoundException, OTPExpiredException, OTPBlockedException, OTPInvalidException, UserDeviceNotFoundException, InvalidKYCDetailsException, DeviceBlockedException, TokenNotFoundException, UserIdentityNotFoundException, EmployeeDeviceNotFoundException {

        //validate login session.
        LoginSession session = sessionRepository.findByIdAndIsActiveTrue(request.getSessionId())
                .orElseThrow(() -> new SessionNotFoundException("No session is found with id : " + request.getSessionId()));

        LoginSessionStatus sessionStatus = session.getLoginSessionStatus();
        if(sessionStatus == LoginSessionStatus.AUTHENTICATED ||
                sessionStatus == LoginSessionStatus.TERMINATED){
            throw new RuntimeException("Session had been used");
        }

        if(!session.isActive()){
            throw new RuntimeException("Session has expired");
        }

        //validate user context
        User user = session.getUser();
        if(user.isBlocked()){
            throw new InvalidKYCDetailsException("User is Blocked");
        }

        if (session.getUser().getId() != user.getId()) {
            throw new SecurityException("Session-user mismatch");
        }


        //verify otp
        boolean verify = otpService.verifyOTP(user.getId(), request.getOtp(), OTPPurpose.LOGIN);

        if(!verify){
            throw new OTPInvalidException("Invalid otp, OTPs didn't match");
        }

        // extract user device information.
        UserDevice device = deviceService.registerOrUpdateDevice(
                user.getId(),
                request.getDeviceInfo(),
                clientIp
        );


        //attach authentication
        AuthTokenPair pair = authTokenService.issueTokens(
                user.getId(), device.getDeviceId()
        );

        //finalize login session
        session.setLoginSessionStatus(LoginSessionStatus.AUTHENTICATED);
        session.setActive(true);
        session.setLastActivityAt(LocalDateTime.now());
        sessionRepository.save(session);
        return new AuthResult(
                pair.getAccessToken(),
                pair.getRefreshToken(),
                pair.getExpiresIn(),
                user.getId(),
                user.getRole()
        );
    }

    @Override
    public Page<LoginSession> getAllByUserIdAndIsActive(Long userId, Pageable pageable) {
        return sessionRepository.findAllByUserIdAndIsActiveTrue(userId, pageable);
    }

    @Override
    public Page<LoginSession> getAllLoginSessions(Pageable pageable) {
        return sessionRepository.findAll(pageable);
    }
}
