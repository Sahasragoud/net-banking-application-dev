package com.optimaNet.auth.controller;

import com.optimaNet.auth.dto.LoginRequest;
import com.optimaNet.auth.dto.LoginSessionRequest;
import com.optimaNet.auth.dto.AuthResult;
import com.optimaNet.auth.service.LoginOrchestratorService;
import com.optimaNet.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/login")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class LoginOrchestratorController {

    private final LoginOrchestratorService loginOrchestrator;
    private final HttpServletRequest request;

    @PostMapping("/initiate")
    public ResponseEntity<Long> startLogin(
            @Valid @RequestBody LoginRequest loginRequest
            ) throws InvalidKYCDetailsException, UserIdentityNotFoundException, UserNotFoundException {
        Long sessionId = loginOrchestrator.initiateLogin(loginRequest);
        return ResponseEntity.accepted().body(sessionId);
    }

    @PostMapping({"/complete", "/verify"})
    public ResponseEntity<AuthResult> completeLogin(
            @RequestBody LoginSessionRequest loginSessionRequest
    ) throws SessionNotFoundException, UserNotFoundException, InvalidKYCDetailsException, OTPExpiredException, OTPBlockedException, UserDeviceNotFoundException, OTPInvalidException, DeviceBlockedException, TokenNotFoundException, UserIdentityNotFoundException, EmployeeDeviceNotFoundException {
        String clientIp = (String) request.getAttribute("CLIENT_IP");
        AuthResult result = loginOrchestrator.completeLogin(loginSessionRequest, clientIp);
        return ResponseEntity.ok(result);
    }



}
