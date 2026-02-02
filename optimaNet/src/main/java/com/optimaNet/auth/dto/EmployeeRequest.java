package com.optimaNet.auth.dto;

import com.optimaNet.auth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmployeeRequest {
    private Long adminId;
    private String fullName;
    private String email;
    private String mobileNumber;
    private Role role;
}