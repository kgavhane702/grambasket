package com.grambasket.userservice.repository;

import com.grambasket.userservice.model.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserProfile, String> {
    Optional<UserProfile> findByAuthId(String authId);

    Page<UserProfile> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    Page<UserProfile> findByEmailContainingIgnoreCaseAndActive(String email, boolean active, Pageable pageable);

    Page<UserProfile> findAllByActive(boolean active, Pageable pageable);
}