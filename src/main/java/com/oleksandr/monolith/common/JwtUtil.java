package com.oleksandr.monolith.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class JwtUtil {
    
    // Используем тот же секрет, что и в RegisterMS
    private static final String SECRET_KEY = "NnjJGfGyhjJMmNbvfFgtYHjkKkNJbhghjhJmkLmNCFfggHjmK";
    private final SecretKey key;
    
    public JwtUtil() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Извлекает user ID из JWT токена
     */
    public UUID extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        String userIdString = claims.getSubject();
        return UUID.fromString(userIdString);
    }
    
    /**
     * Извлекает username из JWT токена
     */
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("username", String.class);
    }
    
    /**
     * Проверяет, валиден ли токен
     */
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Извлекает все claims из токена
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Извлекает токен из Authorization header (убирает "Bearer " префикс)
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}