// File: auth-service/src/main/java/com/grambasket/authservice/service/AuthService.java
package com.grambasket.authservice.service;

import com.grambasket.authservice.dto.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
}