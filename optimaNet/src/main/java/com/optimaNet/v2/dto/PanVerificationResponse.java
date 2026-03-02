package com.optimaNet.v2.dto;

public record PanVerificationResponse(
        String panNumber,
        String holderName,
        String source
) {
}
