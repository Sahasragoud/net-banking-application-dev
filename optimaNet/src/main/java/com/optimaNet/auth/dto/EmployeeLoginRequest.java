package com.optimaNet.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class EmployeeLoginRequest {
    @NotBlank
    @Pattern(regexp = "OPT-EMP-\\d{4}-\\d{6}")
    private String employeeCode;
}
