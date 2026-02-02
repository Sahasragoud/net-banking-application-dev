package com.optimaNet.auth.dto;


import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank
    @Pattern(regexp = "OPT-CUST-\\d{4}-\\d{6}")
    private String customerId;
}

