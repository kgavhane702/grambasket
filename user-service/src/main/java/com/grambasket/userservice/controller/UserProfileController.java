package com.grambasket.userservice.controller;

import com.grambasket.userservice.dto.InternalCreateUserRequest;
import com.grambasket.userservice.dto.UserResponse;
import com.grambasket.userservice.dto.UserUpdateRequest;
import com.grambasket.userservice.model.CommunicationPreferences;
import com.grambasket.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user-service/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Profile", description = "Endpoints for managing the current user's core profile")
public class UserProfileController {

    private final UserService userService;

    @Operation(summary = "Get current user's profile", description = "Fetches the complete profile for the authenticated user, including all addresses.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT is missing or invalid", content = @Content),
            @ApiResponse(responseCode = "404", description = "User profile not found for the given token", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUserProfile(Authentication authentication) {
        String authId = authentication.getName();
        log.info("Fetching profile for authId: {}", authId);
        return ResponseEntity.ok(userService.getUserProfileByAuthId(authId));
    }

    @Operation(summary = "Update current user's profile", description = "Updates the profile information (name, phone, etc.) for the authenticated user. Note: This does not update addresses.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user profile",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT is missing or invalid", content = @Content),
            @ApiResponse(responseCode = "404", description = "User profile not found for the given token", content = @Content)
    })
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUserProfile(
            Authentication authentication,
            @Valid @RequestBody UserUpdateRequest updateRequest) {
        String authId = authentication.getName();
        log.info("Updating profile for authId: {}", authId);
        return ResponseEntity.ok(userService.updateUserProfile(authId, updateRequest));
    }

    @Operation(summary = "Update communication preferences", description = "Updates the user's settings for receiving email, SMS, and push notifications.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preferences updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "User profile not found", content = @Content)
    })
    @PutMapping("/me/preferences")
    public ResponseEntity<UserResponse> updateCommunicationPreferences(
            Authentication authentication,
            @RequestBody CommunicationPreferences preferences) {
        String authId = authentication.getName();
        UserResponse updatedUser = userService.updateCommunicationPreferences(authId, preferences);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Deactivate current user's account (Soft Delete)", description = "Marks the user's account as inactive. The user will be logged out and will not be able to log back in. This is a soft delete.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account deactivated successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "User profile not found", content = @Content)
    })
    @DeleteMapping("/me")
    public ResponseEntity<Void> deactivateAccount(Authentication authentication) {
        String authId = authentication.getName();
        userService.deactivateUserProfile(authId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Permanently delete current user's account (Hard Delete)", description = "DANGER ZONE: This action is irreversible and will permanently delete all of your data. Use with extreme caution.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account permanently deleted", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "User profile not found", content = @Content)
    })
    @DeleteMapping("/me/permanent")
    public ResponseEntity<Void> permanentDeleteAccount(Authentication authentication) {
        String authId = authentication.getName();
        log.warn("PERMANENTLY DELETING account for authId: {}", authId);
        userService.deleteUserProfile(authId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Create a user profile (Internal)",
            description = "Internal endpoint used by the auth-service to create a new user profile after registration. This endpoint is not protected by JWT and should not be exposed publicly via the API Gateway.",
            security = @SecurityRequirement(name = "none")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User profile created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data or user already exists", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict - A user with this authId or email already exists", content = @Content)
    })
    @PostMapping("/internal/create")
    public ResponseEntity<UserResponse> createUserProfile(@Valid @RequestBody InternalCreateUserRequest request) {
        log.info("Internal create profile for authId: {}", request.getAuthId());
        return new ResponseEntity<>(userService.createUserProfile(request.getAuthId(), request.getEmail()), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Ping endpoint",
            description = "A simple health check endpoint to verify the service is running. This endpoint is public.",
            security = @SecurityRequirement(name = "none")
    )
    @ApiResponse(responseCode = "200", description = "Service is responsive")
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of("response", "pong"));
    }
}