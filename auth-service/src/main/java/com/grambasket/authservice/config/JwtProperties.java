package com.grambasket.authservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "auth.jwt")
@Data
public class JwtProperties {
    private long expiration;
    private long refreshExpiration;
    private String issuer;
}