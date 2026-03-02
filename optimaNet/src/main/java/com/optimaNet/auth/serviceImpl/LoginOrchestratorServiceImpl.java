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
import com.optimaNet.auth.repository.UserIdentityRepository;
import com.optimaNet.auth.repository.UserRepository;
import com.optimaNet.auth.service.*;
import com.optimaNet.auth.enums.Role;
import com.optimaNet.v2.entity.CustomerV2;
import com.optimaNet.v2.repository.CustomerV2Repository;
import com.optimaNet.exception.*;
import org.apache.commons.codec.digest.DigestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

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
    private final CustomerV2Repository customerV2Repository;
    private final UserIdentityRepository userIdentityRepository;

    @Override
    public Long initiateLogin(LoginRequest request) throws InvalidKYCDetailsException, UserNotFoundException, UserIdentityNotFoundException {

        User user = userRepository.findByCustomerId(request.getCustomerId())
                .orElseGet(() -> bootstrapAuthUserFromV2Customer(request.getCustomerId()));

        if (user == null) {
            throw new UserNotFoundException("User not found with customerId: " + request.getCustomerId());
        }

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

    private User bootstrapAuthUserFromV2Customer(String customerCode) {
        CustomerV2 v2Customer = customerV2Repository.findByCustomerCode(customerCode).orElse(null);
        if (v2Customer == null) {
            return null;
        }
        Optional<UserIdentity> byEmail = userIdentityRepository.findByEmail(v2Customer.getEmail());
        Optional<UserIdentity> byMobile = userIdentityRepository.findByMobileNumber(v2Customer.getMobileNumber());

        if (byEmail.isPresent() || byMobile.isPresent()) {
            UserIdentity baseIdentity = byEmail.orElseGet(byMobile::get);
            if (byEmail.isPresent() && byMobile.isPresent()
                    && byEmail.get().getUser().getId() != byMobile.get().getUser().getId()) {
                throw new RuntimeException("Conflicting auth identities found for customer contact details");
            }
            User existingUser = baseIdentity.getUser();
            existingUser.setCustomerId(v2Customer.getCustomerCode());
            existingUser.setUserStatus(UserStatus.ACTIVE);
            existingUser.setBlocked(false);
            existingUser.setRole(Role.USER);
            return userRepository.save(existingUser);
        }

        User bootstrap = new User();
        bootstrap.setCustomerId(v2Customer.getCustomerCode());
        bootstrap.setUserStatus(UserStatus.ACTIVE);
        bootstrap.setBlocked(false);
        bootstrap.setRole(Role.USER);
        bootstrap = userRepository.save(bootstrap);

        UserIdentity identity = new UserIdentity();
        identity.setUser(bootstrap);
        identity.setFullName(v2Customer.getFullName());
        // placeholder values for auth-module identity compatibility with OTP flow
        String syntheticAadhaar = "SYNC" + String.format("%08d", v2Customer.getId());
        identity.setEncryptedAadhaarNumber(syntheticAadhaar);
        identity.setHashedAadhaarNumber(DigestUtils.sha256Hex(syntheticAadhaar));
        identity.setMaskedAadhaarNumber("XXXXXXXX0000");
        identity.setMobileNumber(v2Customer.getMobileNumber());
        identity.setEmail(v2Customer.getEmail());
        identity.setEmailVerified(true);
        userIdentityRepository.save(identity);

        return bootstrap;
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
