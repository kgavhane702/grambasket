// File: auth-service/src/main/java/com/grambasket/authservice/dto/AuthResponse.java
package com.grambasket.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
}