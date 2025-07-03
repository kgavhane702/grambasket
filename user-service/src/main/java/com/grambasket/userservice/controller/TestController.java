package com.grambasket.userservice.controller; // Change this package for each service

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A simple, universal controller for testing service availability and routing.
 */
@RestController
@RequestMapping("/api/user-service") // A generic path, the service name is handled by the gateway
public class TestController {

    // Injects the application name from application.yml for a dynamic response
    @Value("${spring.application.name}")
    private String serviceName;

    /**
     * A simple "ping" endpoint to confirm the service is running and reachable.
     * @return A 200 OK response with a confirmation message indicating which service responded.
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        String message = String.format("Ping successful from: '%s'", serviceName);
        return ResponseEntity.ok(message);
    }
}