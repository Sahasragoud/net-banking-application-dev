package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.entity.OTPRequest;
import com.optimaNet.auth.entity.User;
import com.optimaNet.auth.entity.UserIdentity;
import com.optimaNet.auth.enums.KYCStatus;
import com.optimaNet.auth.enums.OTPPurpose;
import com.optimaNet.auth.enums.UserStatus;
import com.optimaNet.auth.repository.UserIdentityRepository;
import com.optimaNet.auth.service.EmailService;
import com.optimaNet.auth.repository.OTPRequestRepository;
import com.optimaNet.auth.repository.UserRepository;
import com.optimaNet.auth.service.OTPService;
import com.optimaNet.auth.service.UserService;
import com.optimaNet.exception.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {

    private final UserRepository userRepository;
    private final OTPRequestRepository otpRequestRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserIdentityRepository identityRepository;
    private final EmailService emailService;
    private final UserService userService;

    @Override
    public void generateOTP(Long userId, OTPPurpose purpose) throws UserNotFoundException, UserIdentityNotFoundException {
        UserIdentity identity = identityRepository.findByUserId(userId)
                .orElseThrow(() -> new UserIdentityNotFoundException("No user identity found with user Id: " + userId)
                );
        User user = userService.getUserById(userId);

        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        String plainOtp = String.valueOf(otp);

        String otpHash = passwordEncoder.encode(plainOtp);

        OTPRequest request = new OTPRequest();
        request.setUser(user);
        request.setOtpHash(otpHash);
        request.setPurpose(purpose);
        request.setAttemptCount(0);
        request.setBlockedUntil(null);

        otpRequestRepository.save(request);
        emailService.sendOtp(identity.getEmail(), "OptimaNet | OTP Verification", String.valueOf(otp));
    }

    @Override
    public boolean verifyOTP(Long userId, String otp, OTPPurpose purpose) throws OTPBlockedException, OTPExpiredException, OTPInvalidException, UserNotFoundException, UserIdentityNotFoundException {
        User user = userService.getUserById(userId);
        UserIdentity identity = identityRepository.findByUserId(userId).orElseThrow(() -> new UserIdentityNotFoundException("User Identity is not found with user_id " + userId));

        OTPRequest otpRequest = otpRequestRepository.findTopByUserIdAndPurposeOrderByCreatedAtDesc(userId, purpose).orElseThrow(
                () -> new OTPInvalidException("OTP not found")
        );

            if(otpRequest.getBlockedUntil() != null &&
                    otpRequest.getBlockedUntil().isAfter(LocalDateTime.now())) {
                throw new OTPBlockedException("OTP has blocked,Try again later");
            }

             if(otpRequest.getExpiresAt().isBefore(LocalDateTime.now())){
                throw new OTPExpiredException("OTP has expired, Try again later");
            }

             if(!passwordEncoder.matches(otp, otpRequest.getOtpHash())){
                 otpRequest.setAttemptCount(otpRequest.getAttemptCount()+1);

                 if(otpRequest.getAttemptCount() >= otpRequest.getMaxAttempts()) {
                     otpRequest.setBlockedUntil(LocalDateTime.now().plusMinutes(15));
                 }

                 otpRequestRepository.save(otpRequest);
                 throw new OTPInvalidException("Invalid OTP");
            }

        identity.setEmailVerified(true);
        identity.setVerificationStatus(KYCStatus.KYC_PENDING);
        identityRepository.save(identity);

        user.setUserStatus(UserStatus.APPROVAL_PENDING);
        userRepository.save(user);
        otpRequestRepository.delete(otpRequest);
        return true;
    }

    @Override
    public void resendOTP(Long userId, OTPPurpose purpose)
            throws OTPBlockedException, UserNotFoundException, UserIdentityNotFoundException {

        OTPRequest lastOtp = otpRequestRepository
                .findTopByUserIdAndPurposeOrderByCreatedAtDesc(userId, purpose)
                .orElse(null);

        if (lastOtp != null) {

            if (lastOtp.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(60))) {
                throw new OTPBlockedException("Please wait before requesting a new OTP.");
            }

            if (lastOtp.getResendCount() >= 3) {
                userService.blockUser(userId);
                throw new OTPBlockedException("OTP resend limit exceeded");
            }

            lastOtp.setResendCount(lastOtp.getResendCount() + 1);
            otpRequestRepository.save(lastOtp);
        }

        generateOTP(userId, purpose);
    }


    @Override
    public Page<OTPRequest> getAllOtpRequestsByUserId(Long userId, Pageable pageable) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No user found with user Id: " + userId)
        );
        return otpRequestRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public Page<OTPRequest> getAllOtpRequests(Pageable pageable) {
        return otpRequestRepository.findAll(pageable);
    }
}
