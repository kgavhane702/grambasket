// File: auth-service/src/main/java/com/grambasket/authservice/dto/LoginRequest.java
package com.grambasket.authservice.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}