package com.optimaNet.auth.service;

import com.optimaNet.auth.dto.KycDocumentResponse;
import com.optimaNet.auth.entity.KycDocument;
import com.optimaNet.auth.enums.DocumentType;
import com.optimaNet.exception.AccessDeniedException;
import com.optimaNet.exception.DuplicateResourceException;
import com.optimaNet.exception.KycApplicationNotFoundException;
import com.optimaNet.exception.KycDocumentNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface KycDocumentOrchestrator {
    Long uploadDocument(Long userId, Long applicationId, DocumentType docType, MultipartFile file)
            throws DuplicateResourceException, KycApplicationNotFoundException, AccessDeniedException, IOException;

    void reuploadDocument(Long documentId, String filePath)
            throws KycDocumentNotFoundException, AccessDeniedException;

    void approveDocument(Long documentId)
            throws KycDocumentNotFoundException, AccessDeniedException;

    void rejectDocument(Long documentId, String reason)
            throws KycDocumentNotFoundException, AccessDeniedException;

    void startDocumentReview(Long documentId)
            throws KycDocumentNotFoundException, AccessDeniedException;

    KycDocument getDocumentById(Long documentId) throws KycDocumentNotFoundException;

    void ensureAllMandatoryDocumentsApproved(Long applicationId)
            throws AccessDeniedException;

    Page<KycDocumentResponse> getDocumentsByApplicationId(Long applicationId, Pageable pageable) throws KycApplicationNotFoundException;

    KycDocument getDocumentForOfficer(Long documentId, Long officerId) throws AccessDeniedException, KycDocumentNotFoundException;
}

