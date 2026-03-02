package com.optimaNet.v2.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerProfileRequest(
        @NotBlank String occupation,
        @NotBlank String incomeSource,
        @NotBlank String yearlyIncome,
        @NotBlank String maritalStatus,
        @NotBlank String fatherName,
        @NotBlank String motherMaidenName
) {
}
