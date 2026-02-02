package com.optimaNet.auth.controller;

import com.optimaNet.auth.dto.LoginRequest;
import com.optimaNet.auth.dto.LoginSessionRequest;
import com.optimaNet.auth.service.LoginOrchestratorService;
import com.optimaNet.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/login")
@RequiredArgsConstructor
public class LoginOrchestratorController {

    private final LoginOrchestratorService loginOrchestrator;
    private final HttpServletRequest request;

    @PostMapping("/initiate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> startLogin(
            @Valid @RequestBody LoginRequest loginRequest
            ) throws InvalidKYCDetailsException, UserIdentityNotFoundException, UserNotFoundException {
        Long sessionId = loginOrchestrator.initiateLogin(loginRequest);
        return ResponseEntity.accepted().body("OTP sent successfully.");
    }

    @PostMapping("/complete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> completeLogin(
            @RequestBody LoginSessionRequest loginSessionRequest
    ) throws SessionNotFoundException, UserNotFoundException, InvalidKYCDetailsException, OTPExpiredException, OTPBlockedException, UserDeviceNotFoundException, OTPInvalidException, DeviceBlockedException, TokenNotFoundException, UserIdentityNotFoundException, EmployeeDeviceNotFoundException {
        String clientIp = (String) request.getAttribute("CLIENT_IP");
        loginOrchestrator.completeLogin(loginSessionRequest, clientIp);
        return ResponseEntity.ok("User Logged In successfully.");
    }



}
