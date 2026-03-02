package com.optimaNet.v2.dto;

import jakarta.validation.constraints.Pattern;

public record MfaCodeRequest(
        @Pattern(regexp = "^[0-9]{6}$") String code
) {
}
