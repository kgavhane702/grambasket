package com.grambasket.authservice.controller;

import com.grambasket.authservice.dto.AuthResponse;
import com.grambasket.authservice.dto.LoginRequest;
import com.grambasket.authservice.dto.RegisterRequest;
import com.grambasket.authservice.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth-service")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        log.info("Received registration request for username: {}", request.getUsername());
        AuthResponse fullAuthResponse = authService.register(request);
        log.info("User {} registered successfully.", request.getUsername());
        return buildClientResponse(fullAuthResponse, response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        log.info("Received login attempt for username: {}", request.getUsername());
        AuthResponse fullAuthResponse = authService.login(request);
        log.info("User {} logged in successfully.", request.getUsername());
        return buildClientResponse(fullAuthResponse, response, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue("gmbasket-refresh-token") String refreshToken,
            HttpServletResponse response
    ) {
        log.info("Received request to refresh token.");
        AuthResponse fullAuthResponse = authService.refreshToken(refreshToken);
        log.info("Token refreshed successfully.");
        return buildClientResponse(fullAuthResponse, response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        log.info("Received logout request.");
        clearRefreshTokenCookie(response);
        log.info("Refresh token cookie cleared.");
        return ResponseEntity.ok(Map.of("message", "User has been logged out successfully."));
    }

    private ResponseEntity<AuthResponse> buildClientResponse(AuthResponse fullAuthResponse, HttpServletResponse httpServletResponse, HttpStatus status) {
        setRefreshTokenCookie(httpServletResponse, fullAuthResponse.getRefreshToken());

        AuthResponse clientResponse = AuthResponse.builder()
                .accessToken(fullAuthResponse.getAccessToken())
                .build();

        return new ResponseEntity<>(clientResponse, status);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("gmbasket-refresh-token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("gmbasket-refresh-token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}