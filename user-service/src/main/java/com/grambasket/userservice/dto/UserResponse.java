package com.grambasket.userservice.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponse {
    // Note: We do NOT expose the internal 'id' or 'authId'
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private List<AddressDto> addresses;
    private CommunicationPreferencesDto communicationPreferences;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}