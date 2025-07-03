// File: auth-service/src/main/java/com/grambasket/authservice/service/impl/AuthServiceImpl.java
package com.grambasket.authservice.service.impl;

import com.grambasket.authservice.dto.*;
import com.grambasket.authservice.exception.TokenValidationException;
import com.grambasket.authservice.exception.UserNotFoundException;
import com.grambasket.authservice.exception.UsernameAlreadyExistsException;
import com.grambasket.authservice.model.Role;
import com.grambasket.authservice.model.User;
import com.grambasket.authservice.repository.UserRepository;
import com.grambasket.authservice.security.JwtService;
import com.grambasket.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j // Add Slf4j for logging
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register new user: {}", request.getUsername());
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Registration failed. Username '{}' already exists.", request.getUsername());
            throw new UsernameAlreadyExistsException("Username already exists: " + request.getUsername());
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singleton(Role.USER))
                .build();
        User savedUser = userRepository.save(user);
        log.info("User '{}' saved successfully with ID: {}", savedUser.getUsername(), savedUser.getId());

        String accessToken = jwtService.generateAccessToken(savedUser.getUsername());
        String refreshToken = jwtService.generateRefreshToken(savedUser.getUsername());
        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting to authenticate user: {}", request.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            log.info("User '{}' authenticated successfully.", userDetails.getUsername());

            String accessToken = jwtService.generateAccessToken(userDetails.getUsername());
            String refreshToken = jwtService.generateRefreshToken(userDetails.getUsername());
            return new AuthResponse(accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user '{}': Invalid credentials.", request.getUsername());
            // Re-throw to be handled by a global exception handler
            throw e;
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Attempting to refresh token.");
        String username = jwtService.extractUsername(refreshToken);
        UserDetails user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found during token refresh for username: {}", username);
                    return new UserNotFoundException("User not found: " + username);
                });

        if (!jwtService.isTokenValid(refreshToken, user)) {
            log.warn("Refresh token validation failed for user: {}", username);
            throw new TokenValidationException("Invalid or expired refresh token");
        }

        log.info("Refresh token validated successfully for user: {}", username);
        String newAccessToken = jwtService.generateAccessToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username); // Token rotation
        log.info("New access and refresh tokens generated for user: {}", username);
        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}