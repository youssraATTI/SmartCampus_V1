package com.smartcampus.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtTokenService {

    private final SecretKey key;
    private final long ttlSeconds;

    public JwtTokenService(
            @Value("${smartcampus.auth.jwt-secret}") String secret,
            @Value("${smartcampus.auth.jwt-ttl-seconds:3600}") long ttlSeconds) {
        if (secret == null || secret.length() < 16) {
            throw new IllegalArgumentException("JWT secret too short");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.ttlSeconds = ttlSeconds;
    }

    public String generate(String email, UserRole role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("role", role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(key)
                .compact();
    }

    public JwtClaims validate(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String role = claims.get("role", String.class);
        return new JwtClaims(claims.getSubject(), UserRole.fromString(role));
    }

    public record JwtClaims(String email, UserRole role) {}
}