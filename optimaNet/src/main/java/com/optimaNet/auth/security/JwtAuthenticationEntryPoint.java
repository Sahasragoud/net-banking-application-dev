package com.optimaNet.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimaNet.exception.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", 401);
        body.put("path", request.getRequestURI());

        if (authException instanceof JwtAuthenticationException jwtEx) {
            body.put("error", jwtEx.getErrorCode());
            body.put("message", jwtEx.getMessage());
        } else {
            body.put("error", "UNAUTHORIZED");
            body.put("message", authException.getMessage());
        }

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
