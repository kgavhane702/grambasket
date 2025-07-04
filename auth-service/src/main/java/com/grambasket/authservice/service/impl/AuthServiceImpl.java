package com.grambasket.authservice.service.impl;

import com.grambasket.authservice.client.UserServiceClient;
import com.grambasket.authservice.dto.*;
import com.grambasket.authservice.exception.ProfileCreationException;
import com.grambasket.authservice.exception.TokenValidationException;
import com.grambasket.authservice.exception.UserNotFoundException;
import com.grambasket.authservice.exception.UsernameAlreadyExistsException;
import com.grambasket.authservice.model.Role;
import com.grambasket.authservice.model.User;
import com.grambasket.authservice.repository.UserRepository;
import com.grambasket.authservice.security.JwtService;
import com.grambasket.authservice.service.AuthService;
import feign.FeignException;
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
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register new user with email: {}", request.getEmail());
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registration failed. Email '{}' already exists.", request.getEmail());
            throw new UsernameAlreadyExistsException("An account with this email already exists: " + request.getEmail());
        }

        // FIXED: Use .email() instead of the non-existent .username()
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(Role.USER))
                .build();

        User savedUser = userRepository.save(user);
        // The getUsername() method correctly returns the email from the User object.
        log.info("User '{}' saved in auth-db with ID: {}.", savedUser.getUsername(), savedUser.getId());

        triggerProfileCreation(savedUser.getId(), savedUser.getUsername());

        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);
        return new AuthResponse(accessToken, refreshToken);
    }

    private void triggerProfileCreation(String authId, String email) {
        try {
            log.info("Triggering profile creation for authId: {}", authId);
            InternalCreateUserRequest profileRequest = new InternalCreateUserRequest(authId, email);
            userServiceClient.createUserProfile(profileRequest);
            log.info("Successfully triggered profile creation for authId: {}", authId);
        } catch (FeignException e) {
            log.error("CRITICAL: Feign client failed to create user profile for authId: {}. Status: {}, Response: {}",
                    authId, e.status(), e.contentUTF8(), e);

            // Compensating Transaction: Rollback user creation in auth-service
            log.warn("Initiating rollback. Deleting user with authId '{}' from auth-db due to profile creation failure.", authId);
            userRepository.deleteById(authId);

            throw new ProfileCreationException("User registration failed during profile creation. The registration has been rolled back.", e);
        }
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting to authenticate user: {}", request.getEmail());
        try {
            // This is correct. Spring Security uses the first parameter as the 'username' for the UserDetailsService.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            log.info("User '{}' authenticated successfully.", userDetails.getUsername());

            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);
            return new AuthResponse(accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user '{}': Invalid credentials.", request.getEmail());
            throw e;
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Attempting to refresh token.");
        String email = jwtService.extractUsername(refreshToken); // The 'username' in the token is the email.
        UserDetails userDetails = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found during token refresh for email: {}", email);
                    return new UserNotFoundException("User not found: " + email);
                });

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            log.warn("Refresh token validation failed for user: {}", email);
            throw new TokenValidationException("Invalid or expired refresh token");
        }

        log.info("Refresh token validated successfully for user: {}", email);
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);
        log.info("New access and refresh tokens generated for user: {}", email);
        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void updateUserRoles(String email, Set<Role> newRoles) {
        log.info("Admin request to update roles for user: {} to {}", email, newRoles);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        user.setRoles(newRoles);
        userRepository.save(user);
        log.info("Successfully updated roles for user: {}", email);
    }
}