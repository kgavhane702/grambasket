package com.grambasket.authservice.service;

import com.grambasket.authservice.dto.*;
import com.grambasket.authservice.model.Role;

import java.util.Set;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void updateUserRoles(String username, Set<Role> newRoles);
}