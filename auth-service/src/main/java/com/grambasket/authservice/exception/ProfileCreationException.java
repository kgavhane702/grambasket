package com.grambasket.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ProfileCreationException extends RuntimeException {

    public ProfileCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProfileCreationException(String message) {
        super(message);
    }
}