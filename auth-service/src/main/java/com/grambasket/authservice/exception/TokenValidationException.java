// File: auth-service/src/main/java/com/grambasket/authservice/exception/TokenValidationException.java
package com.grambasket.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a JWT (typically a refresh token) fails validation,
 * is expired, or is malformed.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED) // Ensures a 401 Unauthorized is returned
public class TokenValidationException extends RuntimeException {
    public TokenValidationException(String message) {
        super(message);
    }
}