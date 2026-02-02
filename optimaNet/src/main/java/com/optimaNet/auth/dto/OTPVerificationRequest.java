package com.optimaNet.auth.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OTPVerificationRequest {
    private Long userId;
    private String otp;
}
