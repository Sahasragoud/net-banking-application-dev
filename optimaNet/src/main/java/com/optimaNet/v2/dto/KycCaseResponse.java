package com.optimaNet.v2.dto;

import com.optimaNet.v2.enums.KycStatus;

import java.time.LocalDateTime;

public record KycCaseResponse(
        Long id,
        Long customerId,
        KycStatus kycStatus,
        String aadhaarNumberMasked,
        String voterIdNumberMasked,
        String panNumberMasked,
        String rejectionReason,
        LocalDateTime submittedAt,
        LocalDateTime reviewedAt
) {
}
