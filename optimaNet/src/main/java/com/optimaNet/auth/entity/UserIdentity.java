package com.optimaNet.auth.entity;

import com.optimaNet.auth.enums.KYCStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "user_identities")
public class UserIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "full_name", updatable = false, nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String encryptedAadhaarNumber;

    @Column(name = "hashed_aadhaar_number", nullable = false, unique = true, length = 64)
    private String hashedAadhaarNumber;

    @Column(nullable = false, length = 20)
    private String maskedAadhaarNumber;

    @Column(nullable = false, unique = true, length = 10)
    private String mobileNumber;

    @Column(nullable = false, unique = true)
    private String email;

    private boolean emailVerified =  false;

    @Enumerated(EnumType.STRING)
    private KYCStatus verificationStatus = KYCStatus.KYC_PENDING;

    private LocalDateTime createdAt;


    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }

}
