package com.grambasket.userservice.service;

import com.grambasket.userservice.dto.UserResponse;
import com.grambasket.userservice.dto.UserUpdateRequest;

public interface UserService {
    UserResponse createUserProfile(String authId, String email);
    UserResponse getUserProfileByAuthId(String authId);
    UserResponse updateUserProfile(String authId, UserUpdateRequest updateRequest);
    void deleteUserProfile(String authId);
}