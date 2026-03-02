package com.optimaNet.v2.dto;

import jakarta.validation.constraints.Pattern;

public record PanVerificationRequest(
        @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$") String panNumber
) {
}
