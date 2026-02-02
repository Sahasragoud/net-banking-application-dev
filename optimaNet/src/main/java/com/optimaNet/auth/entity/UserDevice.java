package com.optimaNet.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_devices",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "device_id"})
        })
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_id", nullable = false)
    private String deviceId;  // UUID from client app

    private String deviceFingerprint;
    private String deviceType;         // MOBILE / WEB
    private String deviceModel;        // "Redmi Note 12"
    private String osVersion;          // "Android 14"
    private String appVersion;

    private boolean isBlocked = false;
    private String blockedReason;

    private boolean isTrusted;


    private String firstSeenIp;
    private String lastSeenIp;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
