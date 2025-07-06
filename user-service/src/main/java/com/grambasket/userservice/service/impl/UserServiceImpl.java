package com.grambasket.userservice.service.impl;

import com.grambasket.userservice.delegate.AuthServiceDelegate;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String USER_PROFILE_CACHE = "userProfiles";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthServiceDelegate authServiceDelegate;

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
    @Cacheable(value = USER_PROFILE_CACHE, key = "#authId")
    public UserResponse getUserProfileByAuthId(String authId) {
        log.info("CACHE MISS: Fetching user profile from DB for authId: {}", authId);
        UserProfile userProfile = findUserByAuthIdInternal(authId);
        return userMapper.toUserResponse(userProfile);
    }

    @Override
    @Transactional
    @CachePut(value = USER_PROFILE_CACHE, key = "#authId")
    public UserResponse updateUserProfile(String authId, UserUpdateRequest updateRequest) {
        log.info("CACHE UPDATE on user profile update for authId: {}", authId);
        UserProfile userProfile = findUserByAuthIdInternal(authId);
        userMapper.updateUserFromDto(updateRequest, userProfile);
        userProfile.setUpdatedAt(LocalDateTime.now());
        UserProfile savedProfile = userRepository.save(userProfile);
        return userMapper.toUserResponse(savedProfile);
    }

    @Override
    @Transactional
    @CacheEvict(value = USER_PROFILE_CACHE, key = "#authId")
    public void deleteUserProfile(String authId) {
        log.warn("CACHE EVICT on user profile deletion for authId: {}", authId);
        UserProfile userProfile = findUserByAuthIdInternal(authId);

        authServiceDelegate.deleteUserCredentials(authId);
        userRepository.delete(userProfile);
        log.info("Successfully deleted user profile from user-db for authId: {}", authId);
    }

    @Override
    @Transactional
    @CacheEvict(value = USER_PROFILE_CACHE, key = "#authId")
    public void deactivateUserProfile(String authId) {
        log.warn("CACHE EVICT on user profile deactivation for authId: {}", authId);
        UserProfile userProfile = findUserByAuthIdInternal(authId);
        userProfile.setActive(false);
        userProfile.setDeactivatedAt(LocalDateTime.now());
        userRepository.save(userProfile);
    }

    @Override
    @Transactional
    @CachePut(value = USER_PROFILE_CACHE, key = "#authId")
    public UserResponse updateCommunicationPreferences(String authId, CommunicationPreferences preferences) {
        log.info("CACHE UPDATE on preference update for authId: {}", authId);
        UserProfile userProfile = findUserByAuthIdInternal(authId);
        userProfile.setCommunicationPreferences(preferences);
        userProfile.setUpdatedAt(LocalDateTime.now());
        UserProfile savedProfile = userRepository.save(userProfile);
        return userMapper.toUserResponse(savedProfile);
    }

    private UserProfile findUserByAuthIdInternal(String authId) {
        return userRepository.findByAuthId(authId)
                .orElseThrow(() -> new UserNotFoundException("User not found for authId: " + authId));
    }
}