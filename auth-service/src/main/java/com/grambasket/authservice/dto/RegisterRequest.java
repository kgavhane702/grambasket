// File: auth-service/src/main/java/com/grambasket/authservice/dto/RegisterRequest.java
package com.grambasket.authservice.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    // add other fields as needed
}