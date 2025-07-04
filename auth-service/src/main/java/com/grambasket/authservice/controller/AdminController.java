package com.grambasket.authservice.controller;

import com.grambasket.authservice.model.Role;
import com.grambasket.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth-service/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private final AuthService authService;

    @PutMapping("/users/{username}/roles")
    public ResponseEntity<Map<String, Object>> updateUserRoles(
            @PathVariable String username,
            @RequestBody Set<Role> roles
    ) {
        // The service layer handles the business logic and throws exceptions if needed.
        authService.updateUserRoles(username, roles);

        // On success, we build a clear and informative response body.
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("message", "Roles updated successfully");
        responseBody.put("username", username);
        responseBody.put("assignedRoles", roles);

        return ResponseEntity.ok(responseBody);
    }
}