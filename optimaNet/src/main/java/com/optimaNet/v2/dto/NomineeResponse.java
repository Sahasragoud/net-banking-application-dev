package com.optimaNet.v2.dto;

public record NomineeResponse(
        Long id,
        Long customerId,
        String depositorName,
        String depositorAddress,
        String nomineeName,
        String nomineeAddress,
        String relationship,
        Integer ageYears,
        String guardianName,
        String guardianRelationship
) {
}
