package com.niloy.scholarshelf.service;

import com.niloy.scholarshelf.dto.request.LoginRequest;
import com.niloy.scholarshelf.dto.request.RegisterRequest;
import com.niloy.scholarshelf.dto.response.AuthResponse;
import com.niloy.scholarshelf.entity.User;

/**
 * Service interface for authentication operations.
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    User getCurrentUser(String email);
}

