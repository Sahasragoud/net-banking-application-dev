package com.optimaNet.v2.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NomineeRequest(
        String depositorName,
        String depositorAddress,
        @NotBlank String nomineeName,
        String nomineeAddress,
        String relationship,
        @NotNull @Min(1) @Max(120) Integer ageYears,
        String guardianName,
        String guardianRelationship
) {
}
