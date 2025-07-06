package com.grambasket.userservice.dto;

import com.grambasket.userservice.model.Address;
import com.grambasket.userservice.model.CommunicationPreferences;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponse {
    private String id;
    private String authId;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> phoneNumbers;
    private List<Address> addresses;
    private CommunicationPreferences communicationPreferences;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
    private LocalDateTime deactivatedAt;
}