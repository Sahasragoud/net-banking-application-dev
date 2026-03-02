package com.optimaNet.v2.controller;

import com.optimaNet.v2.dto.CustomerResponse;
import com.optimaNet.v2.dto.CustomerProfileRequest;
import com.optimaNet.v2.dto.CustomerProfileResponse;
import com.optimaNet.v2.dto.MfaCodeRequest;
import com.optimaNet.v2.dto.MfaSetupResponse;
import com.optimaNet.v2.dto.NomineeRequest;
import com.optimaNet.v2.dto.NomineeResponse;
import com.optimaNet.v2.dto.SetMpinRequest;
import com.optimaNet.v2.dto.V2LoginRequest;
import com.optimaNet.v2.dto.V2LoginResponse;
import com.optimaNet.v2.enums.CustomerStatus;
import com.optimaNet.v2.service.CoreBankingV2Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserManagementV2Controller {

    private final CoreBankingV2Service service;

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> list(
            @RequestParam(required = false) CustomerStatus status
    ) {
        return ResponseEntity.ok(service.listCustomers(status));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getCustomerById(customerId));
    }

    @GetMapping("/by-code/{customerCode}")
    public ResponseEntity<CustomerResponse> getByCode(@PathVariable String customerCode) {
        return ResponseEntity.ok(service.getCustomerByCode(customerCode));
    }

    @PutMapping("/{customerId}/status/{status}")
    public ResponseEntity<CustomerResponse> updateStatus(
            @PathVariable Long customerId,
            @PathVariable CustomerStatus status
    ) {
        return ResponseEntity.ok(service.updateCustomerStatus(customerId, status));
    }

    @PutMapping("/{customerId}/mpin")
    public ResponseEntity<CustomerResponse> setMpin(
            @PathVariable Long customerId,
            @Valid @RequestBody SetMpinRequest request
    ) {
        return ResponseEntity.ok(service.setMpin(customerId, request.mpin()));
    }

    @PostMapping("/{customerId}/mfa/setup")
    public ResponseEntity<MfaSetupResponse> setupMfa(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.setupMfa(customerId));
    }

    @PutMapping("/{customerId}/mfa/enable")
    public ResponseEntity<CustomerResponse> enableMfa(
            @PathVariable Long customerId,
            @Valid @RequestBody MfaCodeRequest request
    ) {
        return ResponseEntity.ok(service.enableMfa(customerId, request.code()));
    }

    @PostMapping("/{customerId}/mfa/verify")
    public ResponseEntity<java.util.Map<String, Boolean>> verifyMfa(
            @PathVariable Long customerId,
            @Valid @RequestBody MfaCodeRequest request
    ) {
        return ResponseEntity.ok(java.util.Map.of("verified", service.verifyMfa(customerId, request.code())));
    }

    @PostMapping("/login")
    public ResponseEntity<V2LoginResponse> login(@Valid @RequestBody V2LoginRequest request) {
        return ResponseEntity.ok(service.loginV2(request));
    }

    @PutMapping("/{customerId}/profile")
    public ResponseEntity<CustomerProfileResponse> saveProfile(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerProfileRequest request
    ) {
        return ResponseEntity.ok(service.upsertProfile(customerId, request));
    }

    @GetMapping("/{customerId}/profile")
    public ResponseEntity<CustomerProfileResponse> getProfile(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getProfile(customerId));
    }

    @PutMapping("/{customerId}/nominees")
    public ResponseEntity<List<NomineeResponse>> saveNominees(
            @PathVariable Long customerId,
            @Valid @RequestBody List<NomineeRequest> nominees
    ) {
        return ResponseEntity.ok(service.replaceNominees(customerId, nominees));
    }

    @GetMapping("/{customerId}/nominees")
    public ResponseEntity<List<NomineeResponse>> getNominees(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getNominees(customerId));
    }
}
