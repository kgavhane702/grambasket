package com.grambasket.userservice.delegate;

import com.grambasket.userservice.client.AuthServiceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthServiceDelegate {

    private static final String AUTH_SERVICE_CIRCUIT_BREAKER = "authService";
    private final AuthServiceClient authServiceClient;

    @CircuitBreaker(name = AUTH_SERVICE_CIRCUIT_BREAKER, fallbackMethod = "handleAuthServiceDeletionFailure")
    public void deleteUserCredentials(String authId) {
        log.info("DELEGATE: Attempting to delete credentials in auth-service for authId: {}", authId);
        authServiceClient.deleteUserCredentials(authId);
        log.info("DELEGATE: Successfully requested credential deletion for authId: {}", authId);
    }

    public void handleAuthServiceDeletionFailure(String authId, Throwable t) {
        log.error("CIRCUIT BREAKER OPEN: The auth-service is unavailable. Failed to delete credentials for authId: {}. Manual cleanup will be required. Error: {}", authId, t.getMessage());
    }
}