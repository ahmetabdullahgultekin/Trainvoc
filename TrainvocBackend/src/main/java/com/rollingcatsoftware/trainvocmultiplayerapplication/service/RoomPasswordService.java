package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.exception.RoomPasswordException;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for room password validation.
 * Uses BCrypt for secure password hashing on the server side.
 */
@Service
public class RoomPasswordService {

    private final RoomService roomService;
    private final PasswordEncoder passwordEncoder;

    public RoomPasswordService(RoomService roomService, PasswordEncoder passwordEncoder) {
        this.roomService = roomService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Hashes a raw password using BCrypt.
     * @param rawPassword the plain text password
     * @return BCrypt hash of the password
     */
    public String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            return null;
        }
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Checks if the provided password matches the room's password.
     * Supports both legacy SHA-256 hashes and new BCrypt hashes.
     * @return true if password matches or room has no password, false otherwise
     */
    public boolean checkPassword(String roomCode, String password) {
        GameRoom room = roomService.findByRoomCode(roomCode);
        if (room == null) {
            return false;
        }

        String roomHash = room.getHashedPassword();
        if (roomHash == null || roomHash.isEmpty()) {
            return true; // Room has no password
        }

        if (password == null || password.isEmpty()) {
            return false;
        }

        // Check if it's a BCrypt hash (starts with $2a$, $2b$, or $2y$)
        if (roomHash.startsWith("$2")) {
            return passwordEncoder.matches(password, roomHash);
        }

        // Legacy support: compare SHA-256 hashes directly
        return timingSafeEquals(roomHash, password);
    }

    /**
     * Validates password and throws exception if invalid.
     * @throws RoomPasswordException if password validation fails
     */
    public void validatePassword(String roomCode, String password) {
        GameRoom room = roomService.findByRoomCode(roomCode);
        if (room == null) {
            throw new RoomPasswordException("RoomNotFound", "Room not found.");
        }

        String roomHash = room.getHashedPassword();
        if (roomHash == null || roomHash.isEmpty()) {
            return; // Room has no password
        }

        if (password == null || password.isEmpty()) {
            throw new RoomPasswordException("RoomPasswordRequired", "Password is required for this room.");
        }

        boolean matches;
        if (roomHash.startsWith("$2")) {
            matches = passwordEncoder.matches(password, roomHash);
        } else {
            // Legacy support
            matches = timingSafeEquals(roomHash, password);
        }

        if (!matches) {
            throw new RoomPasswordException("InvalidRoomPassword", "Incorrect password.");
        }
    }

    /**
     * Checks if a room has a password set.
     */
    public boolean hasPassword(String roomCode) {
        GameRoom room = roomService.findByRoomCode(roomCode);
        if (room == null) {
            return false;
        }
        String roomHash = room.getHashedPassword();
        return roomHash != null && !roomHash.isEmpty();
    }

    /**
     * Timing-safe string comparison to prevent timing attacks.
     * Used for legacy SHA-256 hash comparison.
     */
    private boolean timingSafeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.length() != b.length()) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
