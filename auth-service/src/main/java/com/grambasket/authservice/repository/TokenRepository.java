package com.grambasket.authservice.repository;

import com.grambasket.authservice.model.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {
    Optional<Token> findByRefreshToken(String refreshToken);
    void deleteByUserId(String userId);
    void deleteByRefreshToken(String refreshToken);

}