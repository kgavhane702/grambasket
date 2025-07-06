package com.grambasket.userservice.service;

import com.grambasket.userservice.dto.UserResponse;
import com.grambasket.userservice.dto.UserUpdateRequest;
import com.grambasket.userservice.model.CommunicationPreferences;

public interface UserService {
    UserResponse createUserProfile(String authId, String email);
    UserResponse getUserProfileByAuthId(String authId);
    UserResponse updateUserProfile(String authId, UserUpdateRequest updateRequest);
    void deleteUserProfile(String authId);
    void deactivateUserProfile(String authId);
    UserResponse updateCommunicationPreferences(String authId, CommunicationPreferences preferences);
}