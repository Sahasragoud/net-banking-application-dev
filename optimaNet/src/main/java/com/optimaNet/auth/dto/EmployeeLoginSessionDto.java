package com.optimaNet.auth.dto;

import com.optimaNet.auth.enums.LoginSessionStatus;

import java.time.LocalDateTime;

public record EmployeeLoginSessionDto(
        Long id,
        Long employeeId,
        String ipAddress,
        LocalDateTime loginAt,
        LocalDateTime lastActivityAt,
        boolean isActive,
        LoginSessionStatus loginSessionStatus
) {}
