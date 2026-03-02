package com.optimaNet.v2.dto;

import com.optimaNet.v2.enums.KycDocumentType;

public record KycDocumentVerificationResponse(
        KycDocumentType documentType,
        String documentNumberMasked,
        String holderName,
        String source
) {
}
