package com.grambasket.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class InternalCreateUserRequest {

    @NotBlank(message = "Authentication ID cannot be blank")
    String authId;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    String email;
}