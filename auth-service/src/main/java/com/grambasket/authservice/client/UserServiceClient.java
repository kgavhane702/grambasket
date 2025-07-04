package com.grambasket.authservice.client;

import com.grambasket.authservice.dto.InternalCreateUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
@FeignClient(name = "user-service")
public interface UserServiceClient {
    @PostMapping("/api/user-service/users/internal/create")
    void createUserProfile(@RequestBody InternalCreateUserRequest request);
}