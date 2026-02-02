package com.optimaNet.auth.dto;

import com.optimaNet.auth.enums.EmployeeStatus;
import com.optimaNet.auth.enums.Role;

public record EmployeeResponse(
        Long id,
        String employeeCode,
        String fullName,
        String email,
        Role role,
        EmployeeStatus status
) {}
