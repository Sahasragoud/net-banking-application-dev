package com.optimaNet.v2.dto;

import com.optimaNet.v2.enums.CustomerStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CustomerResponse(
        Long id,
        String customerCode,
        String fullName,
        String email,
        String mobileNumber,
        LocalDate dateOfBirth,
        CustomerStatus customerStatus,
        Boolean mfaEnabled,
        LocalDateTime createdAt
) {
}
