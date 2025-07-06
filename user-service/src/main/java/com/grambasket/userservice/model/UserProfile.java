package com.grambasket.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_profiles")
public class UserProfile {

    @Id
    private String id;

    @Indexed(unique = true)
    private String authId;

    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String email;

    @Builder.Default
    private List<String> phoneNumbers = new ArrayList<>();

    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    @Builder.Default
    private CommunicationPreferences communicationPreferences = new CommunicationPreferences();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder.Default
    private boolean active = true;

    private LocalDateTime deactivatedAt;
}