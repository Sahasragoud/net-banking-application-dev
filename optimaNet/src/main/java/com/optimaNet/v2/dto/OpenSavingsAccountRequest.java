package com.optimaNet.v2.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OpenSavingsAccountRequest(
        @NotNull @DecimalMin(value = "0.00") BigDecimal initialDeposit,
        @NotNull @DecimalMin(value = "0.00") BigDecimal interestRate
) {
}
