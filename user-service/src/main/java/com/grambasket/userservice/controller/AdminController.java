package com.grambasket.userservice.controller;

import com.grambasket.userservice.dto.UserResponse;
import com.grambasket.userservice.service.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-service/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin: User Management", description = "Endpoints for administrators to manage user profiles")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<Page<UserResponse>> findUsers(
            Pageable pageable,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean active) {
        Page<UserResponse> users;
        if (email != null && !email.isBlank()) {
            users = adminService.findUserByEmail(email, pageable, active);
        } else {
            users = adminService.findAllUsers(pageable, active);
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(adminService.getUserProfileById(userId));
    }

    @DeleteMapping("/{userId}/permanent")
    public ResponseEntity<Void> hardDeleteUser(@PathVariable String userId) {
        adminService.hardDeleteUserProfile(userId);
        return ResponseEntity.noContent().build();
    }
}