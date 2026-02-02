package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.dto.KycDocumentResponse;
import com.optimaNet.auth.entity.KycApplication;
import com.optimaNet.auth.entity.KycDocument;
import com.optimaNet.auth.enums.DocumentType;
import com.optimaNet.auth.enums.KYCStatus;
import com.optimaNet.auth.enums.KycDocumentStatus;
import com.optimaNet.auth.repository.KycApplicationRepository;
import com.optimaNet.auth.repository.KycDocumentRepository;
import com.optimaNet.auth.service.DocumentUrlService;
import com.optimaNet.auth.service.KycDocumentService;
import com.optimaNet.exception.AccessDeniedException;
import com.optimaNet.exception.DuplicateResourceException;
import com.optimaNet.exception.KycApplicationNotFoundException;
import com.optimaNet.exception.KycDocumentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class KycDocumentServiceImpl implements KycDocumentService {

    private final KycApplicationRepository applicationRepository;
    private final KycDocumentRepository documentRepository;
    private final Set<DocumentType> REQUIRED_DOCUMENTS = Set.of(
            DocumentType.AADHAAR_FRONT,
            DocumentType.AADHAAR_BACK,
            DocumentType.PASSPORT_PHOTO,
            DocumentType.DIGITAL_SIGN,
            DocumentType.PAN_CARD
    );
    private final DocumentUrlService documentUrlService;

    @Override
    public Long uploadDocuments(Long applicationId, DocumentType docType, String filePath) throws DuplicateResourceException, KycApplicationNotFoundException {
        KycApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new KycApplicationNotFoundException(
                        "KYC application not found with id " + applicationId
                ));
        System.out.println("UPLOAD DOCUMENT HIT FOR APPLICATION-EXISTS ANS IS-ACTIVE");

        boolean exists = documentRepository.existsByApplicationIdAndDocumentTypeAndStatusIn(
                applicationId,
                docType,
                List.of(
                        KycDocumentStatus.UPLOADED,
                        KycDocumentStatus.UNDER_REVIEW,
                        KycDocumentStatus.APPROVED
                )
        );

        System.out.println("UPLOAD DOCUMENT HIT FOR DOCUMENT-EXISTS");
        if (exists) {
            throw new DuplicateResourceException(
                    "Active document already exists for type " + docType
            );
        }

        System.out.println("UPLOAD DOCUMENT HIT FOR DOCUMENT-CREATE");

        KycDocument document = new KycDocument();
        document.setDocumentType(docType);
        document.setApplication(application);
        document.setFilePath(filePath);
        documentRepository.save(document);
        System.out.println("UPLOAD DOCUMENT HIT FOR SAVE-DOCUMENT");
        return document.getId();
    }

    @Override
    public void reUploadDocument(Long documentId, String filePath) throws KycDocumentNotFoundException {
        KycDocument document = getDocumentById(documentId);
        System.out.println("UPLOAD DOCUMENT HIT FOR DOCUMENT-EXISTS:" + documentId);

        document.setStatus(KycDocumentStatus.UPLOADED);
        document.setFilePath(filePath);
        document.setRejectionReason(null);
        document.setReviewedAt(null);

        System.out.println("UPLOAD DOCUMENT HIT FOR DOCUMENT-STATUS & FILEPATH:" + documentId);

        documentRepository.save(document);
    }

    @Override
    public void approveDocument(Long documentId) throws KycDocumentNotFoundException {
        KycDocument document = getDocumentById(documentId);

        document.setStatus(KycDocumentStatus.APPROVED);
        document.setReviewedAt(LocalDateTime.now());

        documentRepository.save(document);
    }

    @Override
    public void rejectDocument(Long documentId, String reason) throws KycDocumentNotFoundException {
        KycDocument document = getDocumentById(documentId);

        document.setRejectionReason(reason);
        document.setReviewedAt(LocalDateTime.now());
        document.setStatus(KycDocumentStatus.REJECTED);

        documentRepository.save(document);
    }

    @Override
    public List<KycDocument> getDocumentsByApplicationAndStatus(Long applicationId, KycDocumentStatus status) {
        return documentRepository.findByApplicationIdAndStatus(applicationId, status);
    }

    public KycDocument getDocumentById(Long documentId) throws KycDocumentNotFoundException {
        return documentRepository.findById(documentId)
                .orElseThrow(() ->
                        new KycDocumentNotFoundException(
                                "KYC document not found with id " + documentId
                        )
                );
    }

    @Override
    public boolean existsApprovedDocument(Long applicationId, DocumentType documentType) {
        return documentRepository.existsByApplicationIdAndDocumentTypeAndStatusIn(
                applicationId,
                documentType,
                List.of(KycDocumentStatus.APPROVED)
        );
    }

    @Override
    public void markUnderReview(Long documentId) throws KycDocumentNotFoundException {
        KycDocument document = getDocumentById(documentId);

        document.setStatus(KycDocumentStatus.UNDER_REVIEW);
        documentRepository.save(document);

        KycApplication application = document.getApplication();

        if (application.getStatus() == KYCStatus.KYC_PENDING) {
            application.setStatus(KYCStatus.KYC_IN_REVIEW);
            applicationRepository.save(application);
        }
    }

    @Override
    public void ensureAllMandatoryDocumentsApproved(Long applicationId) throws AccessDeniedException {

        List<KycDocument> approvedDocuments = getDocumentsByApplicationAndStatus(
                applicationId,
                KycDocumentStatus.APPROVED
        );

        Set<DocumentType> approvedTypes = approvedDocuments.stream()
                .map(KycDocument::getDocumentType)
                .collect(Collectors.toSet());

        if (!approvedTypes.containsAll(REQUIRED_DOCUMENTS)) {
            throw new AccessDeniedException(
                    "All mandatory KYC documents must be approved before application approval"
            );
        }

    }

    @Transactional(readOnly = true)
    public KycDocument getDocumentForOfficer(Long documentId, Long officerId)
            throws AccessDeniedException, KycDocumentNotFoundException {

        KycDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new KycDocumentNotFoundException("DOCUMENT not found: " + documentId));

        KycApplication application = doc.getApplication();

        if (application.getEmployee() == null ||
                !application.getEmployee().getId().equals(officerId)) {
            throw new AccessDeniedException("You are not assigned to this application");
        }

        return doc;
    }


    @Override
    public Page<KycDocumentResponse> getDocumentsByApplicationId(Long applicationId, Pageable pageable) {

        Page<KycDocument> documents = documentRepository.findByApplicationId(applicationId, pageable);

        return documents.map(document -> {
            KycDocumentResponse response = new KycDocumentResponse();
            response.setDocumentId(document.getId());
            response.setDocumentType(document.getDocumentType());
            response.setStatus(document.getStatus());
            response.setReviewedAt(document.getReviewedAt());
            response.setUploadedAt(document.getUploadedAt());
            response.setRejectionReason(document.getRejectionReason());
            response.setViewUrl(documentUrlService.generateOfficerViewUrl(document.getId()));

            return response;
        });
    }
}
