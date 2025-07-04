package com.grambasket.userservice.model;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserProfile {
    @Id
    private String id;

    @Indexed(unique = true)
    private String authId;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    @Builder.Default
    private CommunicationPreferences communicationPreferences = new CommunicationPreferences();

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}