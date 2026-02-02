package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.dto.KycDocumentResponse;
import com.optimaNet.auth.entity.KycApplication;
import com.optimaNet.auth.entity.KycDocument;
import com.optimaNet.auth.enums.DocumentType;
import com.optimaNet.auth.enums.KYCStatus;
import com.optimaNet.auth.enums.KycDocumentStatus;
import com.optimaNet.auth.service.KycApplicationService;
import com.optimaNet.auth.service.KycDocumentOrchestrator;
import com.optimaNet.auth.service.KycDocumentService;
import com.optimaNet.auth.service.LocalFileStorageService;
import com.optimaNet.exception.AccessDeniedException;
import com.optimaNet.exception.DuplicateResourceException;
import com.optimaNet.exception.KycApplicationNotFoundException;
import com.optimaNet.exception.KycDocumentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
public class KycDocumentOrchestratorImpl implements KycDocumentOrchestrator {

    private final KycDocumentService documentService;
    private final KycApplicationService applicationService;
    private final LocalFileStorageService storageService;

    @Override
    public Long uploadDocument(
            Long userId,
            Long applicationId,
            DocumentType docType,
            MultipartFile file
    ) throws DuplicateResourceException, KycApplicationNotFoundException, AccessDeniedException, IOException {

        KycApplication application = applicationService.getApplicationById(applicationId);

        if(application.getUser().getId() != userId){
            throw new AccessDeniedException("Not your application");
        }

        if (application.getStatus() != KYCStatus.KYC_PENDING
                && application.getStatus() != KYCStatus.KYC_IN_REVIEW) {
            throw new AccessDeniedException("Documents cannot be uploaded in current state");
        }

        String filePath = storageService.store(applicationId, docType, file);

        return documentService.uploadDocuments(applicationId, docType, filePath);
    }

    @Override
    public void reuploadDocument(Long documentId, String filePath)
            throws KycDocumentNotFoundException, AccessDeniedException {
        KycDocument document = documentService.getDocumentById(documentId);
        if(document.getStatus() != KycDocumentStatus.REJECTED ){
            throw new AccessDeniedException("Document can not be re-uploaded right now.");
        }
        documentService.reUploadDocument(documentId, filePath);
    }

    @Transactional(readOnly = true)
    public KycDocument getDocumentForOfficer(Long documentId, Long officerId)
            throws AccessDeniedException, KycDocumentNotFoundException {
        return documentService.getDocumentForOfficer(documentId, officerId);
    }


    @Override
    public void approveDocument(Long documentId)
            throws KycDocumentNotFoundException, AccessDeniedException {

        KycDocument document = documentService.getDocumentById(documentId);

        if (document.getStatus() != KycDocumentStatus.UNDER_REVIEW) {
            throw new AccessDeniedException("Document is not under review");
        }

        documentService.approveDocument(documentId);
    }

    @Override
    public void rejectDocument(Long documentId, String reason)
            throws KycDocumentNotFoundException, AccessDeniedException {

        KycDocument document = documentService.getDocumentById(documentId);

        if (document.getStatus() != KycDocumentStatus.UNDER_REVIEW) {
            throw new AccessDeniedException("Document is not under review");
        }

        documentService.rejectDocument(documentId, reason);
    }

    @Override
    public void startDocumentReview(Long documentId)
            throws KycDocumentNotFoundException, AccessDeniedException {

        KycDocument document = documentService.getDocumentById(documentId);
        if(document.getStatus()!= KycDocumentStatus.UPLOADED){
            throw new AccessDeniedException("Document already under review");
        }
        documentService.markUnderReview(documentId);
    }

    @Override
    public KycDocument getDocumentById(Long documentId) throws KycDocumentNotFoundException {
        return  documentService.getDocumentById(documentId);
    }

    @Override
     public void ensureAllMandatoryDocumentsApproved(Long applicationId) throws AccessDeniedException {
        documentService.ensureAllMandatoryDocumentsApproved(applicationId);
    }

    @Override
    public Page<KycDocumentResponse> getDocumentsByApplicationId(Long applicationId, Pageable pageable) throws KycApplicationNotFoundException {
        KycApplication kycApplication = applicationService.getApplicationById(applicationId);

        return documentService.getDocumentsByApplicationId(applicationId, pageable);
    }


}


