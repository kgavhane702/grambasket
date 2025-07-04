package com.grambasket.userservice.repository;

import com.grambasket.userservice.model.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<UserProfile, String> {
    Optional<UserProfile> findByAuthId(String authId);
}