package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.enums.EmployeeStatus;
import com.optimaNet.auth.enums.Role;
import com.optimaNet.auth.enums.UserStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private final String SECRET = "very-strong-secret-key-which-is-at-least-32-bytes-long";
    private final long ACCESS_TOKEN_EXPIRY = 15 * 60; // seconds

    public String generateAccessToken(Long userId, String deviceId, Role role, UserStatus status) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("deviceId", deviceId)
                .claim("role", role.name())
                .claim("status", status.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY * 1000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .claim("type", "ACCESS")
                .setIssuer("optimaNet-auth")
                .compact();
    }

    public String generateAccessToken(Long employeeId, String deviceId, Role role, EmployeeStatus status) {
        return Jwts.builder()
                .setSubject(String.valueOf(employeeId))
                .claim("deviceId", deviceId)
                .claim("role", role.name())
                .claim("status", status.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY * 1000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .claim("type", "ACCESS")
                .setIssuer("optimaNet-auth")
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generatePreAuthToken(Long userId,String deviceId){
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("deviceId", deviceId)
                .claim("role" , Role.USER.name())
                .claim("status", UserStatus.APPROVAL_PENDING.name())
                .claim("type", "PRE_AUTH")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 10*60*1000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
}
