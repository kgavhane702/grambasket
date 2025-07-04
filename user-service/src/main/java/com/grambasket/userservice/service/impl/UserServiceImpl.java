package com.grambasket.userservice.service.impl;

import com.grambasket.userservice.dto.UserResponse;
import com.grambasket.userservice.dto.UserUpdateRequest;
import com.grambasket.userservice.exception.UserNotFoundException;
import com.grambasket.userservice.mapper.UserMapper;
import com.grambasket.userservice.model.UserProfile;
import com.grambasket.userservice.repository.UserRepository;
import com.grambasket.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse createUserProfile(String authId, String email) {
        if (userRepository.findByAuthId(authId).isPresent()) {
            throw new IllegalStateException("User profile already exists for authId: " + authId);
        }
        UserProfile newUserProfile = UserProfile.builder()
                .authId(authId)
                .email(email)
                .build();
        return userMapper.toUserResponse(userRepository.save(newUserProfile));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserProfileByAuthId(String authId) {
        UserProfile userProfile = userRepository.findByAuthId(authId)
                .orElseThrow(() -> new UserNotFoundException("User not found for authId: " + authId));
        return userMapper.toUserResponse(userProfile);
    }

    @Override
    @Transactional
    public UserResponse updateUserProfile(String authId, UserUpdateRequest updateRequest) {
        UserProfile userProfile = userRepository.findByAuthId(authId)
                .orElseThrow(() -> new UserNotFoundException("User not found for authId: " + authId));
        userMapper.updateUserFromDto(updateRequest, userProfile);
        return userMapper.toUserResponse(userRepository.save(userProfile));
    }

    @Override
    @Transactional
    public void deleteUserProfile(String authId) {
        UserProfile userProfile = userRepository.findByAuthId(authId)
                .orElseThrow(() -> new UserNotFoundException("User not found for authId: " + authId));
        userRepository.delete(userProfile);
    }
}