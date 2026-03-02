package com.optimaNet.v2.dto;

public record MfaSetupResponse(
        Long customerId,
        String secret,
        String otpauthUrl
) {
}
