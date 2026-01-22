package com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for authentication response containing JWT token.
 * Returned when a player joins or creates a room.
 */
@Data
@Builder
public class AuthResponse {
    private String token;
    private PlayerResponse player;
    private String roomCode;
}
