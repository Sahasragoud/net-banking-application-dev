package com.optimaNet.auth.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginSessionRequest {
    private Long sessionId;
    private String otp;
    private DeviceInfo deviceInfo;
}
