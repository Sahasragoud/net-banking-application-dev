package com.optimaNet.v2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "v2_nominees")
public class NomineeV2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerV2 customer;

    @Column(name = "depositor_name", length = 120)
    private String depositorName;

    @Column(name = "depositor_address", length = 255)
    private String depositorAddress;

    @Column(name = "nominee_name", nullable = false, length = 120)
    private String nomineeName;

    @Column(name = "nominee_address", length = 255)
    private String nomineeAddress;

    @Column(name = "relationship", length = 60)
    private String relationship;

    @Column(name = "age_years", nullable = false)
    private Integer ageYears;

    @Column(name = "guardian_name", length = 120)
    private String guardianName;

    @Column(name = "guardian_relationship", length = 60)
    private String guardianRelationship;

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
