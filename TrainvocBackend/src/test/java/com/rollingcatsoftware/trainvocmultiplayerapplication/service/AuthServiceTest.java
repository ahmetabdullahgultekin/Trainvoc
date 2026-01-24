package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.auth.AuthResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.auth.LoginRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.auth.RegisterRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.UserRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private static final Long USER_ID = 1L;
    private static final String USERNAME = "testuser";
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encoded-password";
    private static final String JWT_TOKEN = "jwt-token-12345";
    private static final String DISPLAY_NAME = "Test User";

    @BeforeEach
    void setUp() {
        testUser = new User(USERNAME, EMAIL, ENCODED_PASSWORD);
        testUser.setId(USER_ID);
        testUser.setDisplayName(DISPLAY_NAME);
        testUser.setRoles(Set.of(User.Role.USER));
    }

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("successfully registers new user")
        void successfullyRegistersNewUser() {
            RegisterRequest request = new RegisterRequest(USERNAME, EMAIL, PASSWORD, DISPLAY_NAME);

            when(userRepository.existsByUsernameIgnoreCase(USERNAME)).thenReturn(false);
            when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(false);
            when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(jwtTokenProvider.createUserToken(anyLong(), anyString(), any())).thenReturn(JWT_TOKEN);

            AuthResponse result = authService.register(request);

            assertNotNull(result);
            assertEquals(JWT_TOKEN, result.token());
            verify(userRepository).existsByUsernameIgnoreCase(USERNAME);
            verify(userRepository).existsByEmailIgnoreCase(EMAIL);
            verify(passwordEncoder).encode(PASSWORD);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("throws exception when username already exists")
        void throwsException_whenUsernameExists() {
            RegisterRequest request = new RegisterRequest(USERNAME, EMAIL, PASSWORD, DISPLAY_NAME);

            when(userRepository.existsByUsernameIgnoreCase(USERNAME)).thenReturn(true);

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
            );

            assertEquals("Username already exists", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws exception when email already exists")
        void throwsException_whenEmailExists() {
            RegisterRequest request = new RegisterRequest(USERNAME, EMAIL, PASSWORD, DISPLAY_NAME);

            when(userRepository.existsByUsernameIgnoreCase(USERNAME)).thenReturn(false);
            when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(true);

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
            );

            assertEquals("Email already exists", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("sets display name from username when not provided")
        void setsDisplayNameFromUsername_whenNotProvided() {
            RegisterRequest request = new RegisterRequest(USERNAME, EMAIL, PASSWORD, null);

            when(userRepository.existsByUsernameIgnoreCase(USERNAME)).thenReturn(false);
            when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(false);
            when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(jwtTokenProvider.createUserToken(anyLong(), anyString(), any())).thenReturn(JWT_TOKEN);

            authService.register(request);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertEquals(USERNAME, userCaptor.getValue().getDisplayName());
        }
    }

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("successfully logs in user with valid credentials")
        void successfullyLogsInUser() {
            LoginRequest request = new LoginRequest(USERNAME, PASSWORD);

            when(userRepository.findByUsernameOrEmail(USERNAME)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(jwtTokenProvider.createUserToken(anyLong(), anyString(), any())).thenReturn(JWT_TOKEN);

            AuthResponse result = authService.login(request);

            assertNotNull(result);
            assertEquals(JWT_TOKEN, result.token());
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(userRepository).findByUsernameOrEmail(USERNAME);
            verify(userRepository).save(testUser);
            assertNotNull(testUser.getLastLogin());
        }

        @Test
        @DisplayName("throws exception when credentials are invalid")
        void throwsException_whenCredentialsInvalid() {
            LoginRequest request = new LoginRequest(USERNAME, "wrong-password");

            when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

            assertThrows(BadCredentialsException.class, () -> authService.login(request));

            verify(userRepository, never()).findByUsernameOrEmail(any());
        }

        @Test
        @DisplayName("throws exception when user not found after authentication")
        void throwsException_whenUserNotFoundAfterAuth() {
            LoginRequest request = new LoginRequest(USERNAME, PASSWORD);

            when(userRepository.findByUsernameOrEmail(USERNAME)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
            );

            assertEquals("User not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("validateToken")
    class ValidateToken {

        @Test
        @DisplayName("returns user when token is valid")
        void returnsUser_whenTokenIsValid() {
            when(jwtTokenProvider.validateToken(JWT_TOKEN)).thenReturn(true);
            when(jwtTokenProvider.getUserId(JWT_TOKEN)).thenReturn(USER_ID);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            User result = authService.validateToken(JWT_TOKEN);

            assertEquals(testUser, result);
            verify(jwtTokenProvider).validateToken(JWT_TOKEN);
            verify(jwtTokenProvider).getUserId(JWT_TOKEN);
            verify(userRepository).findById(USER_ID);
        }

        @Test
        @DisplayName("throws exception when token is invalid")
        void throwsException_whenTokenIsInvalid() {
            when(jwtTokenProvider.validateToken(JWT_TOKEN)).thenReturn(false);

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.validateToken(JWT_TOKEN)
            );

            assertEquals("Invalid token", exception.getMessage());
            verify(jwtTokenProvider, never()).getUserId(any());
        }

        @Test
        @DisplayName("throws exception when user not found for token")
        void throwsException_whenUserNotFoundForToken() {
            when(jwtTokenProvider.validateToken(JWT_TOKEN)).thenReturn(true);
            when(jwtTokenProvider.getUserId(JWT_TOKEN)).thenReturn(USER_ID);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.validateToken(JWT_TOKEN)
            );

            assertEquals("User not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("refreshToken")
    class RefreshToken {

        @Test
        @DisplayName("returns new token for user")
        void returnsNewTokenForUser() {
            String newToken = "new-jwt-token";
            when(jwtTokenProvider.createUserToken(USER_ID, USERNAME, testUser.getRoles()))
                .thenReturn(newToken);

            AuthResponse result = authService.refreshToken(testUser);

            assertNotNull(result);
            assertEquals(newToken, result.token());
            verify(jwtTokenProvider).createUserToken(USER_ID, USERNAME, testUser.getRoles());
        }
    }
}
