package com.optimaNet.auth.service;

import com.optimaNet.auth.dto.KycDocumentResponse;
import com.optimaNet.auth.entity.KycDocument;
import com.optimaNet.auth.enums.DocumentType;
import com.optimaNet.auth.enums.KycDocumentStatus;
import com.optimaNet.exception.AccessDeniedException;
import com.optimaNet.exception.DuplicateResourceException;
import com.optimaNet.exception.KycApplicationNotFoundException;
import com.optimaNet.exception.KycDocumentNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface KycDocumentService {

    Long uploadDocuments(Long applicationId, DocumentType docType, String file) throws DuplicateResourceException, KycApplicationNotFoundException, AccessDeniedException;

    void reUploadDocument(Long documentId, String filePath) throws KycDocumentNotFoundException;

    void approveDocument(Long documentId) throws KycDocumentNotFoundException, AccessDeniedException;

    void rejectDocument(Long documentId, String reason) throws KycDocumentNotFoundException, AccessDeniedException;

    List<KycDocument> getDocumentsByApplicationAndStatus(Long applicationId, KycDocumentStatus status);

    KycDocument getDocumentById(Long documentId) throws KycDocumentNotFoundException;

    boolean existsApprovedDocument(
            Long applicationId,
            DocumentType documentType
    );

    void markUnderReview(Long documentId) throws KycDocumentNotFoundException;

    void ensureAllMandatoryDocumentsApproved(Long applicationId)
            throws AccessDeniedException;

    Page<KycDocumentResponse> getDocumentsByApplicationId(Long applicationId, Pageable pageable);

    KycDocument getDocumentForOfficer(Long documentId, Long officerId)
            throws AccessDeniedException, KycDocumentNotFoundException;
}

