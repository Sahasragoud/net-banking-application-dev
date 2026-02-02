package com.optimaNet.auth.controller;

import com.optimaNet.auth.dto.OTPVerificationRequest;
import com.optimaNet.auth.dto.RegistrationRequest;
import com.optimaNet.auth.entity.User;
import com.optimaNet.auth.enums.OTPPurpose;
import com.optimaNet.auth.enums.UserStatus;
import com.optimaNet.auth.service.RegistrationOrchestratorService;
import com.optimaNet.exception.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/registration")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class RegistrationOrchestratorController {

    private final RegistrationOrchestratorService orchestratorService;

    @PostMapping("/start")
    public ResponseEntity<Map<String, Long>> startRegistration(
           @Valid @RequestBody RegistrationRequest request
            ) throws UserNotFoundException, InvalidKYCDetailsException, UserIdentityNotFoundException, AccessDeniedException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        User user = orchestratorService.startRegistration(request);
        return ResponseEntity
                .accepted()
                .body(
                Map.of("userId", user.getId())
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyRegistrationOTP(
            @RequestBody OTPVerificationRequest request
            ) throws UserNotFoundException, OTPExpiredException, OTPBlockedException, OTPInvalidException, UserIdentityNotFoundException, AccessDeniedException {
        String token = orchestratorService.verifyRegistrationOTP(request.getUserId(), request.getOtp());

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("{userId}/otp-resend")
    public ResponseEntity<String> resentRegistrationOTP(
            @PathVariable Long userId
            ) throws UserNotFoundException, OTPBlockedException, OTPInvalidException, UserIdentityNotFoundException {
        orchestratorService.resendRegistrationOTP(userId, OTPPurpose.REGISTRATION, UserStatus.OTP_PENDING);
        return ResponseEntity.ok("Otp is sent to the registered email address.");
    }
}
