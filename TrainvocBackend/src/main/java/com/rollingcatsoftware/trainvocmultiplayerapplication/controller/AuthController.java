package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.auth.AuthResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.auth.LoginRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.auth.RegisterRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.ErrorResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.UserRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtAuthenticationFilter.FirebaseAuthentication;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication operations.
 * Handles user registration, login, and token refresh.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    /**
     * Register a new user.
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or user already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage(), "REGISTRATION_FAILED"));
        }
    }

    /**
     * Login with username/email and password.
     */
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user and returns JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid username/email or password", "INVALID_CREDENTIALS"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication failed", "AUTHENTICATION_FAILED"));
        }
    }

    /**
     * Validate token and get user info.
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns current user info based on JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User info retrieved",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            User user = authService.validateToken(token);
            AuthResponse response = authService.refreshToken(user);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage(), "INVALID_TOKEN"));
        }
    }

    /**
     * Refresh JWT token.
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Returns a new JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            User user = authService.validateToken(token);
            AuthResponse response = authService.refreshToken(user);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage(), "INVALID_TOKEN"));
        }
    }

    /**
     * Check if username is available.
     */
    @GetMapping("/check-username")
    @Operation(summary = "Check username availability", description = "Returns whether a username is available")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed",
            content = @Content(schema = @Schema(implementation = UsernameCheckResponse.class)))
    })
    public ResponseEntity<UsernameCheckResponse> checkUsername(@RequestParam String username) {
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.ok(new UsernameCheckResponse(false, "Username cannot be empty"));
        }

        if (username.length() < 3) {
            return ResponseEntity.ok(new UsernameCheckResponse(false, "Username must be at least 3 characters"));
        }

        if (username.length() > 30) {
            return ResponseEntity.ok(new UsernameCheckResponse(false, "Username must be at most 30 characters"));
        }

        boolean exists = userRepository.existsByUsernameIgnoreCase(username.trim());
        if (exists) {
            return ResponseEntity.ok(new UsernameCheckResponse(false, "Username is already taken"));
        }

        return ResponseEntity.ok(new UsernameCheckResponse(true, "Username is available"));
    }

    /**
     * Check if email is available.
     */
    @GetMapping("/check-email")
    @Operation(summary = "Check email availability", description = "Returns whether an email is available")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed",
            content = @Content(schema = @Schema(implementation = EmailCheckResponse.class)))
    })
    public ResponseEntity<EmailCheckResponse> checkEmail(@RequestParam String email) {
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.ok(new EmailCheckResponse(false, "Email cannot be empty"));
        }

        if (!email.contains("@") || !email.contains(".")) {
            return ResponseEntity.ok(new EmailCheckResponse(false, "Invalid email format"));
        }

        boolean exists = userRepository.existsByEmailIgnoreCase(email.trim());
        if (exists) {
            return ResponseEntity.ok(new EmailCheckResponse(false, "Email is already registered"));
        }

        return ResponseEntity.ok(new EmailCheckResponse(true, "Email is available"));
    }

    /**
     * Sync Firebase user to local database.
     * This endpoint is called after Firebase authentication to ensure
     * the user exists in the local database and to get the local user info.
     *
     * The Firebase ID token is verified by the JwtAuthenticationFilter,
     * which also syncs the user. This endpoint returns the synced user info.
     */
    @GetMapping("/firebase-sync")
    @Operation(summary = "Sync Firebase user",
            description = "Syncs authenticated Firebase user to local database and returns user info")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User synced successfully",
            content = @Content(schema = @Schema(implementation = FirebaseSyncResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or missing Firebase token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> firebaseSync(
            @org.springframework.security.core.annotation.AuthenticationPrincipal Object principal) {

        if (principal instanceof FirebaseAuthentication firebaseAuth) {
            User user = userRepository.findById(firebaseAuth.userId())
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("User sync failed", "USER_NOT_FOUND"));
            }

            return ResponseEntity.ok(new FirebaseSyncResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getDisplayName(),
                    user.getFirebaseUid(),
                    user.isEmailVerified(),
                    user.getAuthProvider().name(),
                    user.getTotalGamesPlayed(),
                    user.getTotalScore(),
                    user.getGamesWon()
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Firebase authentication required", "FIREBASE_AUTH_REQUIRED"));
    }

    // Response records for availability checks
    private record UsernameCheckResponse(boolean available, String message) {}
    private record EmailCheckResponse(boolean available, String message) {}

    // Response record for Firebase sync
    private record FirebaseSyncResponse(
            Long id,
            String username,
            String email,
            String displayName,
            String firebaseUid,
            boolean emailVerified,
            String authProvider,
            int totalGamesPlayed,
            int totalScore,
            int gamesWon
    ) {}
}
