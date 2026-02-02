package com.optimaNet.auth.security;

import com.optimaNet.auth.serviceImpl.JwtService;
import com.optimaNet.exception.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * Endpoints that can be accessed using PRE_AUTH tokens
     * (e.g. KYC submission, limited onboarding actions)
     */
    private static final List<String> PRE_AUTH_ALLOWED_PATHS = List.of(
            "/api/user/**"
    );

    /**
     * Public endpoints that do not require JWT at all
     */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/registration/**",
            "/api/auth/login/**"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1. Skip JWT processing for public endpoints
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        // 2. No token provided → let Spring Security decide (401/403)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtService.validateToken(token);

            String tokenType = claims.get("type", String.class);

            // 3. Validate token scope
            if ("PRE_AUTH".equals(tokenType)) {
                validatePreAuthScope(path);
            } else if (!"ACCESS".equals(tokenType)) {
                throw new JwtAuthenticationException(
                        "Invalid token type",
                        "INVALID_TOKEN"
                );
            }

            Long userId = Long.valueOf(claims.getSubject());
            String deviceId = claims.get("deviceId", String.class);
            String role = claims.get("role", String.class);
            String status = claims.get("status", String.class);

            // 4. Business security checks
            if ("BLOCKED".equals(status)) {
                throw new JwtAuthenticationException(
                        "User is not allowed to access the system",
                        "USER_BLOCKED"
                );
            }

            if (role == null) {
                throw new JwtAuthenticationException(
                        "Role missing in token",
                        "INVALID_TOKEN"
                );
            }

            // 5. Build authenticated principal
            JwtUserPrincipal principal =
                    new JwtUserPrincipal(userId, deviceId, status, tokenType);

            List<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            authorities
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtAuthenticationException ex) {
            SecurityContextHolder.clearContext();
            throw ex; // handled by JwtAuthenticationEntryPoint
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            throw new JwtAuthenticationException(
                    "Invalid access token",
                    "INVALID_TOKEN"
            );
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream()
                .anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }

    private void validatePreAuthScope(String path) {
        boolean allowed = PRE_AUTH_ALLOWED_PATHS.stream()
                .anyMatch(pattern -> PATH_MATCHER.match(pattern, path));

        if (!allowed) {
            throw new JwtAuthenticationException(
                    "Pre-auth token not allowed for this endpoint",
                    "INVALID_TOKEN_SCOPE"
            );
        }
    }
}
