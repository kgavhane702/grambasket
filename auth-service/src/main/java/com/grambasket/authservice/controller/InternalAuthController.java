package com.grambasket.authservice.controller;

import com.grambasket.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth-service/internal/users")
@RequiredArgsConstructor
public class InternalAuthController {

    private final AuthService authService;

    @DeleteMapping("/{authId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String authId) {
        authService.deleteUserCredentials(authId);
        return ResponseEntity.noContent().build();
    }
}