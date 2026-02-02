package com.optimaNet.auth.controller;

import com.optimaNet.auth.dto.EmployeeLoginSessionDto;
import com.optimaNet.auth.entity.*;
import com.optimaNet.auth.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/security/")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasRole('ADMIN')")
public class SecurityAdminController {

    private final OTPService otpService;
    private final LoginOrchestratorService loginOrchestrator;
    private final EmployeeOTPService employeeOTPService;
    private final EmployeeLoginOrchestratorService employeeLoginOrchestratorService;

    @GetMapping("user/loginSessions")
    public Page<LoginSession> getAllUserLoginSessions(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection
    ){
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return loginOrchestrator.getAllLoginSessions(PageRequest.of(page, size, sortBy));
    }

    @GetMapping("user/otp-requests")
    public Page<OTPRequest> getAllUserOtpRequests(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection
    ){
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return otpService.getAllOtpRequests(PageRequest.of(page, size, sortBy));
    }

    @GetMapping("employee/loginSessions")
    public Page<EmployeeLoginSessionDto> getAllEmployeeLoginSessions(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection
    ){
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        System.out.println("GET EMPLOYEES HIT for employeeLoginOrchestratorService");

        return employeeLoginOrchestratorService.getAllEmployeeLoginSessions(PageRequest.of(page, size, sortBy));
    }

    @GetMapping("employee/otp-requests")
    public Page<EmployeeOTPRequest> getAllEmployeeOtpRequests(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection
    ){
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return employeeOTPService.getAllOtpRequests(PageRequest.of(page, size, sortBy));
    }



}
