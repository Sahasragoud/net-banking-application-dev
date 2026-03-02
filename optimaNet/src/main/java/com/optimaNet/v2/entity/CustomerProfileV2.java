package com.optimaNet.v2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "v2_customer_profiles")
public class CustomerProfileV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private CustomerV2 customer;

    @Column(length = 80)
    private String occupation;

    @Column(name = "income_source", length = 80)
    private String incomeSource;

    @Column(name = "yearly_income", length = 80)
    private String yearlyIncome;

    @Column(name = "marital_status", length = 40)
    private String maritalStatus;

    @Column(name = "father_name", length = 120)
    private String fatherName;

    @Column(name = "mother_maiden_name", length = 120)
    private String motherMaidenName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
