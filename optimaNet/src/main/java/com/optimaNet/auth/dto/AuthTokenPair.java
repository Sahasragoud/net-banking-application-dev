package com.optimaNet.auth.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokenPair {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
}
