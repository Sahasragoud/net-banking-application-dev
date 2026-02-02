package com.optimaNet.auth.controller;

import com.optimaNet.auth.entity.LoginSession;
import com.optimaNet.auth.entity.OTPRequest;
import com.optimaNet.auth.entity.UserDevice;
import com.optimaNet.auth.enums.DocumentType;
import com.optimaNet.auth.security.JwtUserPrincipal;
import com.optimaNet.auth.service.*;
import com.optimaNet.exception.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/user/")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasRole('USER')")
public class UserOrchestratorController {

    private final LoginOrchestratorService loginOrchestratorService;
    private final OTPService otpService;
    private final UserDeviceService userDeviceService;
    private final KycApplicationOrchestratorService kycApplicationOrchestrator;
    private final KycDocumentOrchestrator documentOrchestrator;

    @GetMapping("{userId}/loginSessions")
    public Page<LoginSession> getAllLoginSessionsByUserId(
            @PathVariable Long userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection
    ){
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return loginOrchestratorService.getAllByUserIdAndIsActive(userId, PageRequest.of(page, size, sortBy));
    }

    @GetMapping("{userId}/otpRequests")
    public Page<OTPRequest> getAllOtpRequestsByUserId(
            @PathVariable Long userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection
    ) throws UserNotFoundException {
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return otpService.getAllOtpRequestsByUserId(userId, PageRequest.of(page, size, sortBy));
    }

    @GetMapping("{userId}/userDevices")
    public Page<UserDevice> getAllUserDevicesByUserId(
            @PathVariable Long userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection
    ) throws UserNotFoundException {
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return userDeviceService.getAllUserDevicesByUserId(userId, PageRequest.of(page, size, sortBy));
    }

    // User creates a KYC application
    @PostMapping("create/kyc-application")
    @PreAuthorize("""
            hasRole('USER') and authentication.principal.tokenType == 'PRE_AUTH'
            """)
    public ResponseEntity<Map<String, Long>> createApplication(Authentication authentication)
            throws UserNotFoundException, AccessDeniedException, DuplicateResourceException, UserIdentityNotFoundException {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        System.out.println("CREATE APPLICATION HIT CONTROLLER-AUTHENTICATION");
        Long applicationId = kycApplicationOrchestrator.createApplication(principal.getUserId());
        return ResponseEntity.accepted().body(Map.of("applicationId", applicationId));
    }

    // User uploads a document
    @PostMapping("upload/kyc-document")
    @PreAuthorize("""
            hasRole('USER') and
            authentication.principal.tokenType == 'PRE_AUTH'
    """)
    public ResponseEntity<Long> uploadDocument(
            @RequestParam @NotNull Long applicationId,
            @RequestParam @NotNull DocumentType documentType,
            @RequestPart("file") MultipartFile file,
            Authentication authentication
    ) throws DuplicateResourceException, KycApplicationNotFoundException, AccessDeniedException, IOException {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        Long documentId = documentOrchestrator.uploadDocument(
                principal.getUserId(), applicationId, documentType, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(documentId);
    }

    // User re-uploads a rejected document
    @PutMapping("kyc-document/{documentId}/reupload")
    public ResponseEntity<Void> reuploadDocument(
            @PathVariable Long documentId,
            @RequestParam @NotBlank String filePath,
            Authentication authentication
    ) throws KycDocumentNotFoundException, AccessDeniedException {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        System.out.println("RE-UPLOAD DOCUMENT HIT FOR CONTROLLER");
        documentOrchestrator.reuploadDocument(documentId, filePath);
        return ResponseEntity.ok().build();
    }
}
