package com.optimaNet.auth.controller;

import com.optimaNet.auth.dto.KycApplicationResponse;
import com.optimaNet.auth.dto.UnassignedApplicationResponse;
import com.optimaNet.auth.entity.KycApplication;
import com.optimaNet.auth.enums.KYCStatus;
import com.optimaNet.auth.service.KycApplicationOrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/kyc-applications/")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasRole('ADMIN')")
public class KYCAdminController {

    private final KycApplicationOrchestratorService kycApplicationOrchestrator;

    @GetMapping
    public Page<KycApplication> getAllKycApplications(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection
    ){
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sortBy = Sort.by(direction, sortField);
        return kycApplicationOrchestrator.getAllKycApplications(PageRequest.of(page, size, sortBy));
    }

    @GetMapping("/status/{status}")
    public Page<UnassignedApplicationResponse> getKycApplicationsInReview(
            @PathVariable KYCStatus status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection
    ){
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return kycApplicationOrchestrator.getKycApplicationsByStatus(status, PageRequest.of(page, size, sortBy));
    }

    @GetMapping("/employee/{employeeId}")
    public Page<KycApplicationResponse> getKycApplicationsByEmployeeId(
            @PathVariable Long employeeId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection
    ){
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return kycApplicationOrchestrator.getKycApplicationsByEmployeeId(employeeId, PageRequest.of(page, size, sortBy));
    }
}
