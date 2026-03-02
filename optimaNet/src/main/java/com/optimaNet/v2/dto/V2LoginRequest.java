package com.optimaNet.v2.dto;

import jakarta.validation.constraints.Pattern;

public record V2LoginRequest(
        @Pattern(regexp = "^OPT-CUST-\\d{4}-\\d{6}$") String customerCode,
        @Pattern(regexp = "^[0-9]{6}$") String mpin,
        @Pattern(regexp = "^[0-9]{6}$") String mfaCode
) {
}
