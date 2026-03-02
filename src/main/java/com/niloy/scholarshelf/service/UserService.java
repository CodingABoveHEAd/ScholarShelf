package com.niloy.scholarshelf.service;

import com.niloy.scholarshelf.dto.response.UserResponse;

import java.util.List;

/**
 * Service interface for user management operations (admin).
 */
public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long userId);

    UserResponse toggleUserActive(Long userId);

    UserResponse changeUserRole(Long userId, String newRole);

    long getTotalUsers();

    long getUserCountByRole(String role);
}

