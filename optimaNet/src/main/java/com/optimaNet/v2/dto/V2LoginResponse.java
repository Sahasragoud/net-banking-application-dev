package com.optimaNet.v2.dto;

import com.optimaNet.v2.enums.CustomerStatus;

public record V2LoginResponse(
        boolean authenticated,
        boolean mfaRequired,
        Long customerId,
        String customerCode,
        String fullName,
        CustomerStatus customerStatus
) {
}
