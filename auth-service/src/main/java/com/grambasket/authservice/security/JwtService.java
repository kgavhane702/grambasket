package com.grambasket.authservice.security;

import com.grambasket.authservice.config.JwtProperties;
import com.grambasket.authservice.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${spring.security.oauth2.resourceserver.jwt.secret-key}")
    private String jwtSecret;

    private final JwtProperties jwtProperties;

    private Key signInKey;

    @PostConstruct
    public void init() {
        this.signInKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        // CORRECTED: The log now accurately reflects the algorithm used in buildToken()
        log.info("JWT signing key initialized successfully for use with HS512 algorithm.");
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        if (claims == null) {
            return null;
        }
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(UserDetails userDetails) {
        String subject = ((User) userDetails).getId();
        log.info("Generating access token for user subject: {}", subject);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> claims = Map.of("roles", roles);
        return buildToken(claims, userDetails, jwtProperties.getExpiration());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        String subject = ((User) userDetails).getId();
        log.info("Generating refresh token for user subject: {}", subject);
        return buildToken(Map.of(), userDetails, jwtProperties.getRefreshExpiration());
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        String subject = ((User) userDetails).getId();
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signInKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String subject = extractUsername(token);
            boolean isSubjectValid = subject != null && subject.equals(((User) userDetails).getId());
            boolean isTokenExpired = isTokenExpired(token);
            log.info("Validating token for subject: {}. Subject match: {}, Token expired: {}", subject, isSubjectValid, isTokenExpired);
            return isSubjectValid && !isTokenExpired;
        } catch (Exception e) {
            log.warn("Token validation failed for subject {}: {}", ((User) userDetails).getId(), e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration != null && expiration.before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signInKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token has expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token format: {}", e.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty or null: {}", e.getMessage());
        }
        return null;
    }
}