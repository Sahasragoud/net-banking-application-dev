package com.optimaNet.v2.dto;

import jakarta.validation.constraints.Pattern;

public record SetMpinRequest(
        @Pattern(regexp = "^[0-9]{6}$") String mpin
) {
}
