package com.optimaNet.v2.controller;

import com.optimaNet.v2.dto.KycCaseResponse;
import com.optimaNet.v2.dto.KycDecisionRequest;
import com.optimaNet.v2.dto.SubmitKycRequest;
import com.optimaNet.v2.service.CoreBankingV2Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/kyc")
@CrossOrigin(origins = "http://localhost:5173")
public class KycV2Controller {

    private final CoreBankingV2Service service;

    @PostMapping("/{customerId}/submit")
    public ResponseEntity<KycCaseResponse> submit(
            @PathVariable Long customerId,
            @Valid @RequestBody SubmitKycRequest request
    ) {
        return ResponseEntity.ok(service.submitKyc(customerId, request));
    }

    @PutMapping("/{customerId}/approve")
    public ResponseEntity<KycCaseResponse> approve(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.approveKyc(customerId));
    }

    @PutMapping("/{customerId}/reject")
    public ResponseEntity<KycCaseResponse> reject(
            @PathVariable Long customerId,
            @RequestBody KycDecisionRequest request
    ) {
        return ResponseEntity.ok(service.rejectKyc(customerId, request.reason()));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<KycCaseResponse> getCase(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getKycCaseDetails(customerId));
    }
}
