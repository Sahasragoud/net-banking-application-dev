package com.optimaNet.auth.controller;

import com.optimaNet.auth.entity.KycDocument;
import com.optimaNet.auth.security.JwtUserPrincipal;
import com.optimaNet.auth.service.KycDocumentOrchestrator;
import com.optimaNet.exception.AccessDeniedException;
import com.optimaNet.exception.KycDocumentNotFoundException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/kyc-officer/kyc-documents")
@PreAuthorize("hasRole('KYC_OFFICER')")
@CrossOrigin(origins = "http://localhost:5173")
public class KycDocumentOfficerController {

    private final KycDocumentOrchestrator documentOrchestrator;

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long documentId,
            Authentication authentication
    ) throws Exception {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        Long officerId = principal.getUserId();

        KycDocument doc =
                documentOrchestrator.getDocumentForOfficer(documentId, officerId);

        Path filePath = Paths.get(doc.getFilePath())
                .toAbsolutePath()
                .normalize();

        if (!Files.exists(filePath)) {
            throw new RuntimeException("Document file not found: " + filePath);
        }

        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + filePath.getFileName() + "\""
                )
                .body(resource);
    }

// KYC Officer starts reviewing a document
    @PutMapping("/{documentId}/start-review")
    public ResponseEntity<Void> startReview(
            @PathVariable Long documentId,
            Authentication authentication
    ) throws KycDocumentNotFoundException, AccessDeniedException {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        documentOrchestrator.startDocumentReview(documentId);
        return ResponseEntity.ok().build();
    }

    // KYC Officer approves a document
    @PutMapping("/{documentId}/approve")
    public ResponseEntity<Void> approveDocument(
            @PathVariable Long documentId,
            Authentication authentication
    ) throws KycDocumentNotFoundException, AccessDeniedException {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        documentOrchestrator.approveDocument(documentId);
        return ResponseEntity.ok().build();
    }

    // KYC Officer rejects a document
    @PutMapping("/{documentId}/reject")
    public ResponseEntity<Void> rejectDocument(
            @PathVariable Long documentId,
            @RequestParam @NotBlank String reason,
            Authentication authentication
    ) throws KycDocumentNotFoundException, AccessDeniedException {

        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        documentOrchestrator.rejectDocument(documentId, reason);
        return ResponseEntity.ok().build();
    }
}
