package com.grambasket.authservice.controller;

    import com.grambasket.authservice.dto.*;
    import com.grambasket.authservice.service.AuthService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.Map;

    @RestController
    @RequestMapping("/api/auth-service")
    @RequiredArgsConstructor
    public class AuthController {
        private final AuthService authService;

        @PostMapping("/register")
        public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
            return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
        }

        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
            return ResponseEntity.ok(authService.login(request));
        }

        @PostMapping("/refresh")
        public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
            return ResponseEntity.ok(authService.refreshToken(request));
        }

        @PostMapping("/logout")
        public ResponseEntity<Map<String, String>> logout(@RequestBody RefreshTokenRequest request) {
            authService.logout(request.getRefreshToken());
            return ResponseEntity.ok(Map.of("message", "User has been logged out successfully."));
        }
    }