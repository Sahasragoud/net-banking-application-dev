package com.optimaNet.v2.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record BalanceOperationRequest(
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @Pattern(regexp = "^[0-9]{6}$") String mpin,
        String sourceReference,
        String remarks
) {
}
