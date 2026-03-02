package com.optimaNet.v2.dto;

public record CustomerProfileResponse(
        Long customerId,
        String occupation,
        String incomeSource,
        String yearlyIncome,
        String maritalStatus,
        String fatherName,
        String motherMaidenName
) {
}
