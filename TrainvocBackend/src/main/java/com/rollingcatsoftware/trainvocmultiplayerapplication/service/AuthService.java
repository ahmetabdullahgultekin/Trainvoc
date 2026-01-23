package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.auth.AuthResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.auth.LoginRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.auth.RegisterRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.UserRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for authentication operations.
 * Handles user registration, login, and token generation.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Register a new user.
     *
     * @param request Registration request with user details
     * @return AuthResponse with JWT token and user info
     * @throws IllegalArgumentException if username or email already exists
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create new user
        User user = new User(
            request.username(),
            request.email(),
            passwordEncoder.encode(request.password())
        );
        user.setDisplayName(request.getEffectiveDisplayName());

        // Save user
        User savedUser = userRepository.save(user);

        // Generate token
        String token = jwtTokenProvider.createUserToken(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getRoles()
        );

        return AuthResponse.fromUser(savedUser, token);
    }

    /**
     * Authenticate user and return JWT token.
     *
     * @param request Login request with credentials
     * @return AuthResponse with JWT token and user info
     * @throws AuthenticationException if authentication fails
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Authenticate using Spring Security
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.identifier(),
                request.password()
            )
        );

        // Find user (authentication passed, so user exists)
        User user = userRepository.findByUsernameOrEmail(request.identifier())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate token
        String token = jwtTokenProvider.createUserToken(
            user.getId(),
            user.getUsername(),
            user.getRoles()
        );

        return AuthResponse.fromUser(user, token);
    }

    /**
     * Validate a token and return user info.
     *
     * @param token JWT token to validate
     * @return User if token is valid
     * @throws IllegalArgumentException if token is invalid
     */
    public User validateToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        Long userId = jwtTokenProvider.getUserId(token);
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Refresh user's token.
     *
     * @param user Current user
     * @return New AuthResponse with fresh token
     */
    public AuthResponse refreshToken(User user) {
        String token = jwtTokenProvider.createUserToken(
            user.getId(),
            user.getUsername(),
            user.getRoles()
        );
        return AuthResponse.fromUser(user, token);
    }
}
