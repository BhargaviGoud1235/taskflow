package com.bhargavi.taskflow;

import com.bhargavi.taskflow.dto.AuthResponse;
import com.bhargavi.taskflow.dto.RegisterRequest;
import com.bhargavi.taskflow.exception.DuplicateResourceException;
import com.bhargavi.taskflow.model.Role;
import com.bhargavi.taskflow.model.User;
import com.bhargavi.taskflow.repository.UserRepository;
import com.bhargavi.taskflow.security.JwtService;
import com.bhargavi.taskflow.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Bhargavi");
        request.setEmail("bhargavi@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("bhargavi@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
    }

    @Test
    void register_shouldCreateUser_whenEmailIsNew() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Bhargavi");
        request.setEmail("bhargavi@example.com");
        request.setPassword("password123");

        User savedUser = new User(1L, "Bhargavi", "bhargavi@example.com", "encodedPassword", Role.USER);

        when(userRepository.existsByEmail("bhargavi@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any())).thenReturn("fake-jwt-token");

        AuthResponse response = authService.register(request);

        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("bhargavi@example.com", response.getEmail());
        assertEquals("USER", response.getRole());
    }
}
