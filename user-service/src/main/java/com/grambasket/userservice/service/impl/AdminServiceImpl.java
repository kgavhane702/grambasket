package com.grambasket.userservice.service.impl;

import com.grambasket.userservice.dto.UserResponse;
import com.grambasket.userservice.dto.UserUpdateRequest;
import com.grambasket.userservice.exception.UserNotFoundException;
import com.grambasket.userservice.mapper.UserMapper;
import com.grambasket.userservice.model.UserProfile;
import com.grambasket.userservice.repository.UserRepository;
import com.grambasket.userservice.service.AdminService;
import com.grambasket.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> findAllUsers(Pageable pageable, Boolean active) {
        log.info("ADMIN: Fetching users for page: {}, active status: {}", pageable.getPageNumber(), active);
        Page<UserProfile> userPage;
        if (active == null) {
            userPage = userRepository.findAll(pageable);
        } else {
            userPage = userRepository.findAllByActive(active, pageable);
        }
        return userPage.map(userMapper::toUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> findUserByEmail(String email, Pageable pageable, Boolean active) {
        log.info("ADMIN: Searching for users with email containing: {}, active status: {}", email, active);
        Page<UserProfile> userPage;
        if (active == null) {
            userPage = userRepository.findByEmailContainingIgnoreCase(email, pageable);
        } else {
            userPage = userRepository.findByEmailContainingIgnoreCaseAndActive(email, active, pageable);
        }
        return userPage.map(userMapper::toUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserProfileById(String userId) {
        log.info("ADMIN: Fetching user profile by internal id: {}", userId);
        UserProfile userProfile = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return userMapper.toUserResponse(userProfile);
    }

    @Override
    @Transactional
    public UserResponse updateUserProfile(String userId, UserUpdateRequest updateRequest) {
        log.info("ADMIN: Updating user profile for internal id: {}", userId);
        UserProfile userProfile = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return userService.updateUserProfile(userProfile.getAuthId(), updateRequest);
    }

    @Override
    @Transactional
    public void hardDeleteUserProfile(String userId) {
        log.warn("ADMIN: Performing HARD DELETE for user ID: {}", userId);
        UserProfile userProfile = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        userService.deleteUserProfile(userProfile.getAuthId());
    }
}