package com.grambasket.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    @DeleteMapping("/api/auth-service/internal/users/{authId}")
    ResponseEntity<Void> deleteUserCredentials(@PathVariable("authId") String authId);
}