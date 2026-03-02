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

    @Column(name = "occupation", nullable = false, length = 100)
    private String occupation;

    @Column(name = "income_source", nullable = false, length = 100)
    private String incomeSource;

    @Column(name = "yearly_income", nullable = false, length = 100)
    private String yearlyIncome;

    @Column(name = "marital_status", nullable = false, length = 50)
    private String maritalStatus;

    @Column(name = "father_name", nullable = false, length = 120)
    private String fatherName;

    @Column(name = "mother_maiden_name", nullable = false, length = 120)
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
