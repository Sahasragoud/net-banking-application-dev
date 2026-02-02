package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.dto.RegistrationRequest;
import com.optimaNet.auth.entity.User;
import com.optimaNet.auth.entity.UserDevice;
import com.optimaNet.auth.entity.UserIdentity;
import com.optimaNet.auth.enums.OTPPurpose;
import com.optimaNet.auth.enums.UserStatus;
import com.optimaNet.auth.service.*;
import com.optimaNet.exception.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Transactional
@RequiredArgsConstructor
public class RegistrationOrchestratorServiceImpl implements RegistrationOrchestratorService {

    private final OTPService otpService;
    private final UserService userService;
    private final UserIdentityService userIdentityService;
    private final JwtService jwtService;
    private final UserDeviceService userDeviceService;

    @Override
    public User startRegistration(RegistrationRequest request) throws InvalidKYCDetailsException, UserNotFoundException, UserIdentityNotFoundException, AccessDeniedException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if(request.getAadhaarNumber().length() != 12 || request.getMobileNumber().length() != 10){
            throw new InvalidKYCDetailsException("Your KYC details are rejected");
        }

        if(userIdentityService.aadhaarExists(request.getAadhaarNumber()) || userIdentityService.mobileExists(request.getMobileNumber()) || userIdentityService.emailExists(request.getEmailAddress())){
            throw new AccessDeniedException("User already exists with this details");
        }

        User user = userService.createInitiatedUser();
        userIdentityService.createUserIdentity(
                user.getId(), request
        );

        otpService.generateOTP(user.getId(), OTPPurpose.REGISTRATION);
        return user;
    }

    @Override
    public String verifyRegistrationOTP(Long userId, String otp) throws OTPExpiredException, OTPBlockedException, OTPInvalidException, UserNotFoundException, UserIdentityNotFoundException, AccessDeniedException {
        User user = userService.getUserById(userId);
        if(user.isBlocked() || user.getUserStatus() == UserStatus.BLOCKED){
            throw new AccessDeniedException("User is blocked, try again later");
        }
        boolean valid = otpService.verifyOTP(userId, otp, OTPPurpose.REGISTRATION);
        if(!valid){
            throw new OTPInvalidException("Invalid OTP Exception");
        }

        UserIdentity identity = userIdentityService.getIdentityByUserId(userId);
        userIdentityService.markEmailVerified(userId);

        return jwtService.generatePreAuthToken(userId, "xxx");
    }

    @Override
    public void resendRegistrationOTP(Long userId, OTPPurpose purpose, UserStatus status) throws OTPBlockedException, OTPInvalidException, UserNotFoundException, UserIdentityNotFoundException {

        User user = userService.getUserById(userId);

        if(user.getUserStatus() == UserStatus.BLOCKED){
            throw new OTPBlockedException("OTP has blocked,Try again later");
        }

        if(user.getUserStatus() != UserStatus.OTP_PENDING){
            throw new OTPInvalidException("Resending OTP is not allowed in current state");
        }

        otpService.resendOTP(userId, purpose);
    }
}
