package com.rollingcatsoftware.trainvocmultiplayerapplication.dto.auth;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Response DTO for authentication operations.
 * Contains JWT token and user information.
 */
public record AuthResponse(
    String token,
    String tokenType,
    Long userId,
    String username,
    String email,
    String displayName,
    Set<String> roles
) {
    /**
     * Creates an AuthResponse from a User entity and JWT token.
     */
    public static AuthResponse fromUser(User user, String token) {
        return new AuthResponse(
            token,
            "Bearer",
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getDisplayName(),
            user.getRoles().stream()
                .map(User.Role::name)
                .collect(Collectors.toSet())
        );
    }
}
