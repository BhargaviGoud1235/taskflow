package com.bhargavi.taskflow.service;

import com.bhargavi.taskflow.dto.AuthResponse;
import com.bhargavi.taskflow.dto.LoginRequest;
import com.bhargavi.taskflow.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
