package com.optimaNet.v2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SubmitKycRequest(
        @Pattern(regexp = "^[0-9]{12}$") String aadhaarNumber,
        @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$") String panNumber,
        @NotBlank String addressLine,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String postalCode,
        @NotBlank String country
) {
}
