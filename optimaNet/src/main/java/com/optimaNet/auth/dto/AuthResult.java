package com.optimaNet.auth.dto;

import com.optimaNet.auth.enums.Role;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResult {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private Long userId;
    private Role role;
}
