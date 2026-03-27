package com.niloy.scholarshelf.service.impl;

import com.niloy.scholarshelf.dto.response.UserResponse;
import com.niloy.scholarshelf.entity.User;
import com.niloy.scholarshelf.enums.Role;
import com.niloy.scholarshelf.exception.ResourceNotFoundException;
import com.niloy.scholarshelf.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .fullName("Test User 1")
                .email("user1@example.com")
                .role(Role.BUYER)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        testUser2 = User.builder()
                .id(2L)
                .fullName("Test User 2")
                .email("user2@example.com")
                .role(Role.SELLER)
                .active(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, testUser2));

        List<UserResponse> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("user1@example.com", users.get(0).getEmail());
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals("user1@example.com", response.getEmail());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void toggleUserActive_Deactivate_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        UserResponse response = userService.toggleUserActive(1L);

        assertNotNull(response);
        assertFalse(response.getActive());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void toggleUserActive_Activate_Success() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(testUser2));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        UserResponse response = userService.toggleUserActive(2L);

        assertNotNull(response);
        assertTrue(response.getActive());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void toggleUserActive_NotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.toggleUserActive(99L));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changeUserRole_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        UserResponse response = userService.changeUserRole(1L, "ADMIN");

        assertNotNull(response);
        assertEquals("ADMIN", response.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changeUserRole_NotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.changeUserRole(99L, "ADMIN"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getTotalUsers_Success() {
        when(userRepository.count()).thenReturn(10L);

        long count = userService.getTotalUsers();

        assertEquals(10L, count);
    }

    @Test
    void getUsersByRole_Success() {
        when(userRepository.findByRole(Role.SELLER)).thenReturn(List.of(testUser2));

        List<UserResponse> users = userService.getUsersByRole("SELLER");

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("user2@example.com", users.get(0).getEmail());
    }

    @Test
    void getUserCountByRole_Success() {
        when(userRepository.countByRole(Role.BUYER)).thenReturn(5L);

        long count = userService.getUserCountByRole("BUYER");

        assertEquals(5L, count);
    }
}
