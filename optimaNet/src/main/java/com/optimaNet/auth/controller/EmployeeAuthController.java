package com.optimaNet.auth.controller;

import com.optimaNet.auth.dto.*;
import com.optimaNet.auth.service.EmployeeAuthTokenService;
import com.optimaNet.auth.service.EmployeeLoginOrchestratorService;
import com.optimaNet.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/employee/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeAuthController {

    private final EmployeeLoginOrchestratorService authService;
    private final EmployeeAuthTokenService authTokenService;
    private final HttpServletRequest request;

    @PostMapping("/login/initiate")
    public ResponseEntity<Long> initiateLogin(
            @RequestBody EmployeeLoginRequest loginRequest
    ) throws AccessDeniedException, EmployeeNotFoundException {
        System.out.println("LOGIN INITIATE HIT for employeeCode=" + loginRequest.getEmployeeCode());
        String clientIp = (String) request.getAttribute("CLIENT_IP");

        Long sessionId = authService.initiateLogin(loginRequest, clientIp);
        return ResponseEntity.accepted().body(sessionId);
    }

    @PostMapping("/login/verify")
    public ResponseEntity<AuthResult> verifyLoginOtp(
            @RequestBody LoginSessionRequest sessionRequest
            ) throws UserNotFoundException, EmployeeDeviceNotFoundException, InvalidKYCDetailsException, OTPExpiredException, OTPBlockedException, UserDeviceNotFoundException, OTPInvalidException, DeviceBlockedException, TokenNotFoundException, EmployeeNotFoundException, SessionNotFoundException {

        System.out.println("LOGIN VERIFY HIT for Session=" + sessionRequest.getSessionId());

        String clientIp = (String) request.getAttribute("CLIENT_IP");
        AuthResult result = authService.completeLogin(sessionRequest, clientIp);
        return ResponseEntity.ok(result);
    }

    @PostMapping("{employeeId}/logout")
    public ResponseEntity<Void> logout(
            @PathVariable Long employeeId,
            @RequestParam String deviceId
    ) throws TokenNotFoundException {
        authTokenService.logout(employeeId, deviceId);
        return ResponseEntity.noContent().build();
    }
}
