package com.grambasket.userservice.controller;

import com.grambasket.userservice.dto.UserResponse;
import com.grambasket.userservice.dto.UserUpdateRequest;
import com.grambasket.userservice.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-service/admin/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin User Management", description = "Endpoints for administrators to manage user profiles")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Get all users with pagination", description = "Fetches a paginated list of all users. Can be filtered by active status.")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @ParameterObject Pageable pageable,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(adminService.findAllUsers(pageable, active));
    }

    @Operation(summary = "Search for users by email", description = "Fetches a paginated list of users whose email matches the search term. Can be filtered by active status.")
    @GetMapping("/search")
    public ResponseEntity<Page<UserResponse>> searchUsersByEmail(
            @RequestParam String email,
            @ParameterObject Pageable pageable,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(adminService.findUserByEmail(email, pageable, active));
    }

    @Operation(summary = "Get a single user profile by internal ID", description = "Fetches a complete user profile using the internal database ID.")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "Internal database ID of the user") @PathVariable String userId) {
        return ResponseEntity.ok(adminService.getUserProfileById(userId));
    }

    @Operation(summary = "Update a user's profile by internal ID", description = "Updates profile information for a specific user using their internal database ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user profile",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "User profile not found for the given ID", content = @Content)
    })
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "Internal database ID of the user") @PathVariable String userId,
            @Valid @RequestBody UserUpdateRequest updateRequest) {
        return ResponseEntity.ok(adminService.updateUserProfile(userId, updateRequest));
    }

    @Operation(summary = "Permanently delete a user by internal ID (Hard Delete)", description = "DANGER ZONE: This action is irreversible and will permanently delete all of the user's data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account permanently deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "User profile not found for the given ID", content = @Content)
    })
    @DeleteMapping("/{userId}/permanent")
    public ResponseEntity<Void> hardDeleteUser(
            @Parameter(description = "Internal database ID of the user") @PathVariable String userId) {
        adminService.hardDeleteUserProfile(userId);
        return ResponseEntity.noContent().build();
    }
}