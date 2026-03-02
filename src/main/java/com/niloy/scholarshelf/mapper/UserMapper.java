package com.niloy.scholarshelf.mapper;

import com.niloy.scholarshelf.dto.response.UserResponse;
import com.niloy.scholarshelf.entity.User;

/**
 * Mapper to convert User entity to UserResponse DTO.
 */
public class UserMapper {

    private UserMapper() {}

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole().name())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

