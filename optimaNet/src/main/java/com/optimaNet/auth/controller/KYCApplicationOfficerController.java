package com.optimaNet.auth.controller;

import com.optimaNet.auth.dto.Applicant;
import com.optimaNet.auth.dto.KycApplicationResponse;
import com.optimaNet.auth.dto.KycDocumentResponse;
import com.optimaNet.auth.dto.UnassignedApplicationResponse;
import com.optimaNet.auth.entity.KycApplication;
import com.optimaNet.auth.enums.KYCStatus;
import com.optimaNet.auth.security.JwtUserPrincipal;
import com.optimaNet.auth.service.KycApplicationOrchestratorService;
import com.optimaNet.auth.service.KycDocumentOrchestrator;
import com.optimaNet.exception.*;
import com.twilio.rest.microvisor.v1.App;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/kyc-officer/kyc-applications")
@CrossOrigin(origins = "http://localhost:5173")
public class KYCApplicationOfficerController {

    private final KycApplicationOrchestratorService kycApplicationOrchestrator;
    private final KycDocumentOrchestrator kycDocumentOrchestrator;

    // KYC Officer assigns themselves to an application
    @PostMapping("/{applicationId}/assign")
    @PreAuthorize("hasRole('KYC_OFFICER')")
    public ResponseEntity<String> assignToEmployee(
            @PathVariable Long applicationId,
            Authentication authentication
    ) throws KycApplicationNotFoundException, EmployeeNotFoundException, AccessDeniedException, UserIdentityNotFoundException {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        Long employeeId = principal.getUserId(); // Officer's own ID
        kycApplicationOrchestrator.assignToEmployee(applicationId, employeeId);

        return ResponseEntity.ok(
                "The application with id " + applicationId + " is assigned to KYC officer with id " + employeeId
        );
    }

    @PostMapping("/{applicationId}/approve")
    @PreAuthorize("hasRole('KYC_OFFICER')")
    public ResponseEntity<String> approve(
            @PathVariable Long applicationId,
            Authentication authentication
    ) throws KycApplicationNotFoundException, EmployeeNotFoundException, UserNotFoundException, AccessDeniedException, UserIdentityNotFoundException {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        Long employeeId = principal.getUserId();
        System.out.println("APPROVE APPLICATION HIT FOR CONTROLLER, employee=" + employeeId);
        kycApplicationOrchestrator.approve(applicationId, employeeId);

        return ResponseEntity.ok(
                "The application with id " + applicationId + " is approved by KYC officer with id " + employeeId
        );
    }

    @PostMapping("/{applicationId}/reject")
    @PreAuthorize("hasRole('KYC_OFFICER')")
    public ResponseEntity<String> reject(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> body,
            Authentication authentication
    ) throws KycApplicationNotFoundException, EmployeeNotFoundException, UserNotFoundException, AccessDeniedException, UserIdentityNotFoundException {
        String reason = body.get("reason");

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        Long employeeId = principal.getUserId();
        kycApplicationOrchestrator.reject(applicationId, employeeId, reason);

        return ResponseEntity.ok(
                "The application with id " + applicationId + " is rejected by KYC officer with id " + employeeId + "\nReason: " + reason
        );
    }

    @GetMapping
    public Page<UnassignedApplicationResponse> getKycApplicationsUploaded(
            @RequestParam KYCStatus status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection
    ) throws AccessDeniedException {
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return kycApplicationOrchestrator.getUnassignedApplications(status, PageRequest.of(page, size, sortBy));
    }

    @GetMapping("/me")
    public Page<KycApplicationResponse> getMyAssignedApplications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        Long employeeId = principal.getUserId();
        System.out.println("Employee Id:" + employeeId);
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return kycApplicationOrchestrator.getKycApplicationsByEmployeeId(
                employeeId,
                PageRequest.of(page, size, sortBy)
        );
    }

    @GetMapping("/{applicationId}/documents")
    Page<KycDocumentResponse> getDocumentsOfApplication(
            @PathVariable Long applicationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) throws KycApplicationNotFoundException {
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return kycDocumentOrchestrator.getDocumentsByApplicationId(applicationId, PageRequest.of(page,size,sortBy));
    }

    @GetMapping("/{applicationId}/applicant")
    public ResponseEntity<Applicant> getApplicantDetails(
            @PathVariable Long applicationId
    ) throws UserIdentityNotFoundException, KycApplicationNotFoundException {
        System.out.println("APPLICANT-DETAILS HIT FOR CONTROLLER");
        Applicant applicant = kycApplicationOrchestrator.getApplicantDetails(applicationId);
        return ResponseEntity.ok(applicant);
    }

}
