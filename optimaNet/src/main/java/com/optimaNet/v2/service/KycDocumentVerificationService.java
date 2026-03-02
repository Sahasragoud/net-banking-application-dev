package com.optimaNet.v2.service;

import com.optimaNet.v2.dto.KycDocumentVerificationResponse;
import com.optimaNet.v2.dto.PanVerificationResponse;
import com.optimaNet.v2.entity.KycRegistryEntryV2;
import com.optimaNet.v2.enums.KycDocumentType;
import com.optimaNet.v2.exception.V2ValidationException;
import com.optimaNet.v2.repository.KycRegistryEntryV2Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KycDocumentVerificationService {

    private static final String SOURCE = "LOCAL_KYC_REGISTRY";

    private final KycRegistryEntryV2Repository kycRegistryEntryRepository;

    @Transactional(readOnly = true)
    public KycDocumentVerificationResponse verifyDocument(KycDocumentType requestedType, String rawDocumentNumber) {
        String normalizedDocumentNumber = normalize(rawDocumentNumber);
        if (normalizedDocumentNumber.isBlank()) {
            throw new V2ValidationException("Document number is required");
        }

        KycDocumentType documentType = requestedType != null ? requestedType : inferDocumentType(normalizedDocumentNumber);
        validateFormat(documentType, normalizedDocumentNumber);

        KycRegistryEntryV2 entry = kycRegistryEntryRepository
                .findByDocumentTypeAndDocumentNumberAndActiveTrue(documentType, normalizedDocumentNumber)
                .orElseThrow(() -> new V2ValidationException("No matching KYC record found for the provided details"));

        return new KycDocumentVerificationResponse(
                documentType,
                mask(documentType, normalizedDocumentNumber),
                entry.getHolderName(),
                SOURCE
        );
    }

    @Transactional(readOnly = true)
    public PanVerificationResponse verifyPan(String rawPanNumber) {
        String normalizedPan = normalize(rawPanNumber);
        KycDocumentVerificationResponse verification = verifyDocument(KycDocumentType.PAN, normalizedPan);
        return new PanVerificationResponse(
                normalizedPan,
                verification.holderName(),
                verification.source()
        );
    }

    private String normalize(String documentNumber) {
        return documentNumber == null ? "" : documentNumber.trim().toUpperCase().replace(" ", "");
    }

    private KycDocumentType inferDocumentType(String documentNumber) {
        if (documentNumber.matches("^[A-Z]{5}[0-9]{4}[A-Z]$")) {
            return KycDocumentType.PAN;
        }
        if (documentNumber.matches("^[0-9]{12}$")) {
            return KycDocumentType.AADHAAR;
        }
        if (documentNumber.matches("^[A-Z]{3}[0-9]{7}$")) {
            return KycDocumentType.VOTER_ID;
        }
        throw new V2ValidationException("Unable to infer KYC document type from the provided number");
    }

    private void validateFormat(KycDocumentType documentType, String documentNumber) {
        String pattern = switch (documentType) {
            case PAN -> "^[A-Z]{5}[0-9]{4}[A-Z]$";
            case AADHAAR -> "^[0-9]{12}$";
            case VOTER_ID -> "^[A-Z]{3}[0-9]{7}$";
        };
        if (!documentNumber.matches(pattern)) {
            throw new V2ValidationException("Invalid " + documentType + " format");
        }
    }

    private String mask(KycDocumentType type, String documentNumber) {
        return switch (type) {
            case PAN -> "XXXXXX" + documentNumber.substring(documentNumber.length() - 4);
            case AADHAAR -> "XXXXXXXX" + documentNumber.substring(documentNumber.length() - 4);
            case VOTER_ID -> "XXX" + documentNumber.substring(documentNumber.length() - 3);
        };
    }
}
