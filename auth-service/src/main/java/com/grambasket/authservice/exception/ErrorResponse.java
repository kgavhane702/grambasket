package com.grambasket.authservice.exception;

    import lombok.Builder;
    import lombok.Data;

    import java.time.LocalDateTime;

    @Data
    @Builder
    public class ErrorResponse {
        private String message;
        private int status;
        private String error;
        private LocalDateTime timestamp;
        private String path;
    }