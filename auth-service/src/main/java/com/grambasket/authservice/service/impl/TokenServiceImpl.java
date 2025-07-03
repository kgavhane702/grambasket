// Create new file: service/impl/TokenServiceImpl.java
package com.grambasket.authservice.service.impl;

import com.grambasket.authservice.dto.AuthResponse;
import com.grambasket.authservice.exception.UserNotFoundException;
import com.grambasket.authservice.model.Token;
import com.grambasket.authservice.model.User;
import com.grambasket.authservice.repository.TokenRepository;
import com.grambasket.authservice.repository.UserRepository;
import com.grambasket.authservice.security.JwtService;
import com.grambasket.authservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse generateAndPersistTokens(User user) {
        String newAccessToken = jwtService.generateAccessToken(user.getUsername());
        String newRefreshToken = jwtService.generateRefreshToken(user.getUsername());

        tokenRepository.deleteByUserId(user.getId());
        tokenRepository.save(Token.builder()
                .userId(user.getId())
                .refreshToken(newRefreshToken)
                .revoked(false)
                .build());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public User validateAndGetUserFromRefreshToken(String refreshToken) {
        Token token = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.isRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }

        String username = jwtService.extractUsername(token.getRefreshToken());
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        tokenRepository.deleteByRefreshToken(refreshToken);
    }
}