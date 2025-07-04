package com.grambasket.userservice.mapper;

import com.grambasket.userservice.dto.UserResponse;
import com.grambasket.userservice.dto.UserUpdateRequest;
import com.grambasket.userservice.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Maps a UserProfile entity to a UserResponse DTO.
     */
    UserResponse toUserResponse(UserProfile userProfile);

    /**
     * Updates an existing UserProfile entity from a UserUpdateRequest DTO.
     * @param dto The source DTO with new values.
     * @param userProfile The target entity to be updated.
     */
    void updateUserFromDto(UserUpdateRequest dto, @MappingTarget UserProfile userProfile);
}