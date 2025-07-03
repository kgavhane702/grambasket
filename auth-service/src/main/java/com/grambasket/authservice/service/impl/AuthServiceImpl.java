// Updated file: service/impl/AuthServiceImpl.java
package com.grambasket.authservice.service.impl;

import com.grambasket.authservice.dto.*;
import com.grambasket.authservice.exception.UsernameAlreadyExistsException;
import com.grambasket.authservice.model.Role;
import com.grambasket.authservice.model.User;
import com.grambasket.authservice.repository.UserRepository;
import com.grambasket.authservice.service.AuthService;
import com.grambasket.authservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists: " + request.getUsername());
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singleton(Role.USER))
                .build();
        userRepository.save(user);

        return tokenService.generateAndPersistTokens(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        return tokenService.generateAndPersistTokens(user);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        User user = tokenService.validateAndGetUserFromRefreshToken(request.getRefreshToken());
        return tokenService.generateAndPersistTokens(user);
    }

    @Override
    public void logout(String refreshToken) {
        tokenService.revokeRefreshToken(refreshToken);
    }
}