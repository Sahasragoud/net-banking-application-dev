
package com.optimaNet.auth.dto;

import com.optimaNet.auth.enums.DocumentType;
import com.optimaNet.auth.enums.KycDocumentStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class KycDocumentResponse {

    private Long documentId;
    private DocumentType documentType;
    private KycDocumentStatus status;

    private LocalDateTime uploadedAt;
    private LocalDateTime reviewedAt;

    private String rejectionReason;

    // Secure access
    private String viewUrl;
}
