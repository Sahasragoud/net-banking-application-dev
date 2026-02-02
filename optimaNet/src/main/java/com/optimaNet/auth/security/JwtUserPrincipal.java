package com.optimaNet.auth.security;

import lombok.*;

@Getter
@RequiredArgsConstructor
public class JwtUserPrincipal {
    private final Long userId;
    private final String deviceId;
    private final String userStatus;
    private final String tokenType;
}
