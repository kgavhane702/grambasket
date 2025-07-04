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

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register new user: {}", request.getUsername());
        userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
            log.warn("Registration failed. Username '{}' already exists.", request.getUsername());
            throw new UsernameAlreadyExistsException("Username already exists: " + request.getUsername());
        });

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        User savedUser = userRepository.save(user);
        log.info("User '{}' saved successfully with ID: {} and roles: {}", savedUser.getUsername(), savedUser.getId(), savedUser.getRoles());

        String accessToken = jwtService.generateAccessToken(savedUser);
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

            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails.getUsername());
            return new AuthResponse(accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user '{}': Invalid credentials.", request.getUsername());
            throw e;
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Attempting to refresh token.");
        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found during token refresh for username: {}", username);
                    return new UserNotFoundException("User not found: " + username);
                });

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            log.warn("Refresh token validation failed for user: {}", username);
            throw new TokenValidationException("Invalid or expired refresh token");
        }

        log.info("Refresh token validated successfully for user: {}", username);
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails.getUsername());
        log.info("New access and refresh tokens generated for user: {}", username);
        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public void updateUserRoles(String username, Set<Role> newRoles) {
        log.info("Admin request to update roles for user: {} to {}", username, newRoles);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        user.setRoles(newRoles);
        userRepository.save(user);
        log.info("Successfully updated roles for user: {}", username);
    }
}