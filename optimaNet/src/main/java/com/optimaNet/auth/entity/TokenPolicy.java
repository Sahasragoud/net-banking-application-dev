package com.optimaNet.auth.entity;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class TokenPolicy {

    private final long accessTokenTtlSeconds = 15 * 60;
    private final long refreshTokenTtlDays = 30;
}
