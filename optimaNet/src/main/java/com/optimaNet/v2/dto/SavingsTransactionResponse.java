package com.optimaNet.v2.dto;

import com.optimaNet.v2.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SavingsTransactionResponse(
        Long id,
        TransactionType txnType,
        BigDecimal amount,
        BigDecimal balanceAfterTxn,
        String remarks,
        LocalDateTime createdAt
) {
}
