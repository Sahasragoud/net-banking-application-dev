package com.optimaNet.auth.entity;

import com.optimaNet.auth.enums.LoginSessionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employee_login_sessions")
public class EmployeeLoginSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "ip_address", nullable = false)
    private String ip_address;

    @Column(name = "login_at", nullable = false)
    private LocalDateTime loginAt;

    @Column(name = "last_activity_at", nullable = false)
    private LocalDateTime lastActivityAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_session_status", nullable = false)
    private LoginSessionStatus loginSessionStatus;

    @PrePersist
    protected void onCreate() {
        loginAt = LocalDateTime.now();
        lastActivityAt = loginAt;
        loginSessionStatus = LoginSessionStatus.INITIATED;
    }
}


