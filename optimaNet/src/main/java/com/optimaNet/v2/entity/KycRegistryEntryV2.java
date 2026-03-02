package com.optimaNet.v2.entity;

import com.optimaNet.v2.enums.KycDocumentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "v2_kyc_registry",
        uniqueConstraints = @UniqueConstraint(name = "uk_v2_kyc_registry_document", columnNames = {"document_type", "document_number"})
)
public class KycRegistryEntryV2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 20)
    private KycDocumentType documentType;

    @Column(name = "document_number", nullable = false, length = 32)
    private String documentNumber;

    @Column(name = "holder_name", nullable = false, length = 120)
    private String holderName;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.active == null) {
            this.active = Boolean.TRUE;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
