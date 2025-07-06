package com.grambasket.userservice.service;

import com.grambasket.userservice.dto.UserResponse;
import com.grambasket.userservice.dto.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    Page<UserResponse> findAllUsers(Pageable pageable, Boolean active);
    Page<UserResponse> findUserByEmail(String email, Pageable pageable, Boolean active);
    UserResponse getUserProfileById(String userId);
    UserResponse updateUserProfile(String userId, UserUpdateRequest updateRequest);
    void hardDeleteUserProfile(String userId);
}