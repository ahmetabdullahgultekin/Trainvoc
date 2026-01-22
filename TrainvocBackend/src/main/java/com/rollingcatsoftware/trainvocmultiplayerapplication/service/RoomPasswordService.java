package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.exception.RoomPasswordException;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import org.springframework.stereotype.Service;

/**
 * Service responsible for room password validation.
 * Uses timing-safe comparison to prevent timing attacks.
 */
@Service
public class RoomPasswordService {

    private final RoomService roomService;

    public RoomPasswordService(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * Checks if the provided password matches the room's password.
     * @return true if password matches or room has no password, false otherwise
     */
    public boolean checkPassword(String roomCode, String hashedPassword) {
        GameRoom room = roomService.findByRoomCode(roomCode);
        if (room == null) {
            return false;
        }

        String roomHash = room.getHashedPassword();
        if (roomHash == null || roomHash.isEmpty()) {
            return true; // Room has no password
        }

        return timingSafeEquals(roomHash, hashedPassword);
    }

    /**
     * Validates password and throws exception if invalid.
     * @throws RoomPasswordException if password validation fails
     */
    public void validatePassword(String roomCode, String hashedPassword) {
        GameRoom room = roomService.findByRoomCode(roomCode);
        if (room == null) {
            throw new RoomPasswordException("RoomNotFound", "Room not found.");
        }

        String roomHash = room.getHashedPassword();
        if (roomHash == null || roomHash.isEmpty()) {
            return; // Room has no password
        }

        if (hashedPassword == null || hashedPassword.isEmpty()) {
            throw new RoomPasswordException("RoomPasswordRequired", "Password is required for this room.");
        }

        if (!timingSafeEquals(roomHash, hashedPassword)) {
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
