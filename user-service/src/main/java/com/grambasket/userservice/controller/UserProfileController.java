package com.grambasket.userservice.controller;

import com.grambasket.userservice.dto.InternalCreateUserRequest;
import com.grambasket.userservice.dto.UserResponse;
import com.grambasket.userservice.dto.UserUpdateRequest;
import com.grambasket.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user-service/users")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUserProfile(Authentication authentication) {
        String authId = authentication.getName();
        log.info("Fetching profile for authId: {}", authId);
        return ResponseEntity.ok(userService.getUserProfileByAuthId(authId));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUserProfile(
            Authentication authentication,
            @Valid @RequestBody UserUpdateRequest updateRequest) {
        String authId = authentication.getName();
        log.info("Updating profile for authId: {}", authId);
        return ResponseEntity.ok(userService.updateUserProfile(authId, updateRequest));
    }

    @PostMapping("/internal/create")
    public ResponseEntity<UserResponse> createUserProfile(@Valid @RequestBody InternalCreateUserRequest request) {
        log.info("Internal create profile for authId: {}", request.getAuthId());
        return new ResponseEntity<>(userService.createUserProfile(request.getAuthId(), request.getEmail()), HttpStatus.CREATED);
    }

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of("response", "pong"));
    }
}