package com.niloy.scholarshelf.service.impl;

import com.niloy.scholarshelf.dto.response.UserResponse;
import com.niloy.scholarshelf.entity.User;
import com.niloy.scholarshelf.enums.Role;
import com.niloy.scholarshelf.exception.ResourceNotFoundException;
import com.niloy.scholarshelf.mapper.UserMapper;
import com.niloy.scholarshelf.repository.UserRepository;
import com.niloy.scholarshelf.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of user management service (admin operations).
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse toggleUserActive(Long userId) {
        log.info("Toggling active status for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setActive(!user.getActive());
        user = userRepository.save(user);

        log.info("User {} active status set to: {}", userId, user.getActive());
        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse changeUserRole(Long userId, String newRole) {
        log.info("Changing role for user {} to: {}", userId, newRole);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setRole(Role.valueOf(newRole.toUpperCase()));
        user = userRepository.save(user);

        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUsers() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(String role) {
        Role roleEnum = Role.valueOf(role.toUpperCase());
        return userRepository.findByRole(roleEnum).stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUserCountByRole(String role) {
        return userRepository.countByRole(Role.valueOf(role.toUpperCase()));
    }
}

