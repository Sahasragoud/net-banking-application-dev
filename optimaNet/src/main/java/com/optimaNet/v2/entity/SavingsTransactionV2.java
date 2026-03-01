package com.optimaNet.v2.entity;

import com.optimaNet.v2.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "v2_savings_transactions")
public class SavingsTransactionV2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "savings_account_id", nullable = false)
    private SavingsAccountV2 savingsAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "txn_type", nullable = false, length = 32)
    private TransactionType txnType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_after_txn", nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfterTxn;

    @Column(length = 255)
    private String remarks;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
