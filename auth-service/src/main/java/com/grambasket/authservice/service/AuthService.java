package com.grambasket.authservice.service;

import com.grambasket.authservice.dto.AuthResponse;
import com.grambasket.authservice.dto.LoginRequest;
import com.grambasket.authservice.dto.RegisterRequest;
import com.grambasket.authservice.model.Role;

import java.util.Set;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void updateUserRoles(String email, Set<Role> newRoles);
    void deleteUserCredentials(String authId);
}