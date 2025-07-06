package com.grambasket.userservice.service.impl;

import com.grambasket.userservice.client.AuthServiceClient;
import com.grambasket.userservice.dto.UserResponse;
import com.grambasket.userservice.dto.UserUpdateRequest;
import com.grambasket.userservice.exception.UserNotFoundException;
import com.grambasket.userservice.mapper.UserMapper;
import com.grambasket.userservice.model.CommunicationPreferences;
import com.grambasket.userservice.model.UserProfile;
import com.grambasket.userservice.repository.UserRepository;
import com.grambasket.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthServiceClient authServiceClient;

    @Override
    @Transactional
    public UserResponse createUserProfile(String authId, String email) {
        log.info("Creating user profile for authId: {}", authId);
        if (userRepository.findByAuthId(authId).isPresent()) {
            throw new IllegalStateException("User profile already exists for authId: " + authId);
        }
        UserProfile newUserProfile = UserProfile.builder()
                .authId(authId)
                .email(email)
                .build();
        UserProfile savedProfile = userRepository.save(newUserProfile);
        log.info("Successfully created and saved user profile with ID: {}", savedProfile.getId());
        return userMapper.toUserResponse(savedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserProfileByAuthId(String authId) {
        UserProfile userProfile = findUserByAuthIdInternal(authId);
        return userMapper.toUserResponse(userProfile);
    }

    @Override
    @Transactional
    public UserResponse updateUserProfile(String authId, UserUpdateRequest updateRequest) {
        UserProfile userProfile = findUserByAuthIdInternal(authId);
        userMapper.updateUserFromDto(updateRequest, userProfile);
        userProfile.setUpdatedAt(LocalDateTime.now());
        UserProfile savedProfile = userRepository.save(userProfile);
        return userMapper.toUserResponse(savedProfile);
    }

    @Override
    @Transactional
    public void deleteUserProfile(String authId) {
        log.warn("USER: Performing HARD DELETE for authId: {}", authId);
        UserProfile userProfile = findUserByAuthIdInternal(authId);

        try {
            log.info("Requesting credential deletion in auth-service for authId: {}", authId);
            authServiceClient.deleteUserCredentials(authId);
            log.info("Successfully requested credential deletion for authId: {}", authId);
        } catch (Exception e) {
            log.error("Failed to delete credentials in auth-service for authId: {}. Manual cleanup may be required. Error: {}", authId, e.getMessage());
        }

        userRepository.delete(userProfile);
        log.info("Successfully deleted user profile from user-db for authId: {}", authId);
    }

    @Override
    @Transactional
    public void deactivateUserProfile(String authId) {
        UserProfile userProfile = findUserByAuthIdInternal(authId);
        log.warn("Deactivating user account for authId: {}", authId);
        userProfile.setActive(false);
        userProfile.setDeactivatedAt(LocalDateTime.now());
        userRepository.save(userProfile);
    }

    @Override
    @Transactional
    public UserResponse updateCommunicationPreferences(String authId, CommunicationPreferences preferences) {
        UserProfile userProfile = findUserByAuthIdInternal(authId);
        log.info("Updating communication preferences for authId: {}", authId);
        userProfile.setCommunicationPreferences(preferences);
        UserProfile savedProfile = userRepository.save(userProfile);
        return userMapper.toUserResponse(savedProfile);
    }

    private UserProfile findUserByAuthIdInternal(String authId) {
        return userRepository.findByAuthId(authId)
                .orElseThrow(() -> new UserNotFoundException("User not found for authId: " + authId));
    }
}