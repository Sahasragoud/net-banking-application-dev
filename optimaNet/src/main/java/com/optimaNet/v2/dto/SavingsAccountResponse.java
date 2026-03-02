package com.optimaNet.v2.dto;

import com.optimaNet.v2.enums.SavingsAccountStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SavingsAccountResponse(
        Long id,
        String accountNumber,
        Long customerId,
        String customerCode,
        String holderName,
        String customerStatus,
        String bankName,
        String branchName,
        String ifscCode,
        String upiHandle,
        SavingsAccountStatus accountStatus,
        BigDecimal availableBalance,
        BigDecimal interestRate,
        LocalDateTime createdAt,
        List<SavingsTransactionResponse> recentTransactions
) {
}
