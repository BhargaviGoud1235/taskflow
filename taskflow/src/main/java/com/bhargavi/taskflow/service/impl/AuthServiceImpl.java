package com.bhargavi.taskflow.service.impl;

import com.bhargavi.taskflow.dto.AuthResponse;
import com.bhargavi.taskflow.dto.LoginRequest;
import com.bhargavi.taskflow.dto.RegisterRequest;
import com.bhargavi.taskflow.exception.DuplicateResourceException;
import com.bhargavi.taskflow.exception.InvalidCredentialsException;
import com.bhargavi.taskflow.model.Role;
import com.bhargavi.taskflow.model.User;
import com.bhargavi.taskflow.repository.UserRepository;
import com.bhargavi.taskflow.security.JwtService;
import com.bhargavi.taskflow.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with email " + request.getEmail() + " already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(toUserDetails(saved));

        return new AuthResponse(token, saved.getName(), saved.getEmail(), saved.getRole().name());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(toUserDetails(user));
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole().name());
    }

    private UserDetails toUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }
}
