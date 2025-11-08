package com.sqool.sqoolbus.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
public class JwtTokenProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    
    @Value("${app.jwt.secret:sqoolbus-secret-key-for-jwt-token-generation-must-be-at-least-256-bits}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration:86400}") // 24 hours in seconds
    private int jwtExpirationInSeconds;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    public String generateToken(String username, String tenantId, Set<String> roles, Set<String> permissions, Long schoolId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInSeconds * 1000L);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("tenantId", tenantId);
        claims.put("roles", roles);
        claims.put("permissions", permissions);
        if (schoolId != null) {
            claims.put("schoolId", schoolId);
        }
        
        return Jwts.builder()
                .setSubject(username)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String generateMasterToken(String username, Long userId, java.util.List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInSeconds * 1000L);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("roles", roles);
        claims.put("type", "master"); // Indicates this is a master system token
        
        return Jwts.builder()
                .setSubject(username)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        // First try to get username from claims, then fall back to subject
        String username = (String) claims.get("username");
        if (username == null || username.trim().isEmpty()) {
            username = claims.getSubject();
        }
        
        return username;
    }
    
    public String getTenantIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return (String) claims.get("tenantId");
    }
    
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        Object userId = claims.get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        } else if (userId instanceof Long) {
            return (Long) userId;
        }
        return null;
    }
    
    public Long getSchoolIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        Object schoolId = claims.get("schoolId");
        if (schoolId instanceof Integer) {
            return ((Integer) schoolId).longValue();
        } else if (schoolId instanceof Long) {
            return (Long) schoolId;
        }
        return null;
    }
    
    public String getTokenType(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return (String) claims.get("type");
    }
    
    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof List) {
            return new HashSet<>((List<String>) rolesObj);
        } else if (rolesObj instanceof Set) {
            return (Set<String>) rolesObj;
        }
        return new HashSet<>();
    }
    
    @SuppressWarnings("unchecked")
    public Set<String> getPermissionsFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        Object permissionsObj = claims.get("permissions");
        if (permissionsObj instanceof List) {
            return new HashSet<>((List<String>) permissionsObj);
        } else if (permissionsObj instanceof Set) {
            return (Set<String>) permissionsObj;
        }
        return new HashSet<>();
    }
    
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.getExpiration();
    }
    
    public LocalDateTime getExpirationLocalDateTimeFromToken(String token) {
        Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
    
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty");
        }
        return false;
    }
    
    public long getExpirationTimeInSeconds() {
        return jwtExpirationInSeconds;
    }
}