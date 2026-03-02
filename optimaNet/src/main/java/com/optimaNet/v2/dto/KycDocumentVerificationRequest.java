package com.optimaNet.v2.dto;

import com.optimaNet.v2.enums.KycDocumentType;
import jakarta.validation.constraints.NotBlank;

public record KycDocumentVerificationRequest(
        KycDocumentType documentType,
        @NotBlank String documentNumber
) {
}
