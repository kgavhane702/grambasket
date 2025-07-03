// Create new file: service/TokenService.java
package com.grambasket.authservice.service;

import com.grambasket.authservice.dto.AuthResponse;
import com.grambasket.authservice.model.User;

public interface TokenService {
    AuthResponse generateAndPersistTokens(User user);
    User validateAndGetUserFromRefreshToken(String refreshToken);
    void revokeRefreshToken(String refreshToken);
}