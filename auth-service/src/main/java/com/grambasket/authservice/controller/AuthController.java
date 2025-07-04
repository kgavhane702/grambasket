package com.grambasket.authservice.controller;

import com.grambasket.authservice.dto.AuthResponse;
import com.grambasket.authservice.dto.LoginRequest;
import com.grambasket.authservice.dto.RegisterRequest;
import com.grambasket.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/auth-service")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Value("${app.security.cookie.secure:true}")
    private boolean useSecureCookie;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        log.info("Processing registration request for email: {}", request.getEmail());
        AuthResponse fullAuthResponse = authService.register(request);
        log.info("User with email {} registered successfully.", request.getEmail());
        return buildClientResponse(fullAuthResponse, response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        log.info("Processing login attempt for email: {}", request.getEmail());
        AuthResponse fullAuthResponse = authService.login(request);
        log.info("User with email {} logged in successfully.", request.getEmail());
        return buildClientResponse(fullAuthResponse, response, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue("gmbasket-refresh-token") String refreshToken,
            HttpServletResponse response
    ) {
        log.info("Processing request to refresh token.");
        AuthResponse fullAuthResponse = authService.refreshToken(refreshToken);
        log.info("Token refreshed successfully.");
        return buildClientResponse(fullAuthResponse, response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        log.info("Processing logout request.");
        clearRefreshTokenCookie(response);
        log.info("User logged out successfully.");
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
        ResponseCookie cookie = ResponseCookie.from("gmbasket-refresh-token", refreshToken)
                .httpOnly(true)
                .secure(useSecureCookie)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("gmbasket-refresh-token", "")
                .httpOnly(true)
                .secure(useSecureCookie)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}