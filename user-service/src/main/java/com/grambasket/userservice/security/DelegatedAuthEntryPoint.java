package com.grambasket.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grambasket.userservice.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime; // Use LocalDateTime to match your ErrorResponse DTO

@Component("delegatedAuthEntryPoint")
@Slf4j
@RequiredArgsConstructor
public class DelegatedAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.warn("Authentication failed for request to [{} {}]: {}",
                request.getMethod(), request.getRequestURI(), authException.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String errorMessage = "Authentication failed: Invalid token or credentials provided.";
        if (authException instanceof OAuth2AuthenticationException oauthEx) {
            OAuth2Error error = oauthEx.getError();
            if (error.getDescription() != null && !error.getDescription().isBlank()) {
                errorMessage = error.getDescription();
            }
        }

        // Use the existing builder pattern to create the response object
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now()) // Use LocalDateTime as defined in your DTO
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message(errorMessage)
                .path(request.getRequestURI())
                .build();

        response.getOutputStream().println(objectMapper.writeValueAsString(errorResponse));
    }
}