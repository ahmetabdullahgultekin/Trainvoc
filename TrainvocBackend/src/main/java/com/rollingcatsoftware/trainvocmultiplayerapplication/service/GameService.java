package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Facade service that orchestrates game operations.
 * Delegates to focused services for specific responsibilities.
 *
 * This class maintains backward compatibility while individual services
 * handle their specific concerns following SRP.
 */
@Service
public class GameService {

    private final RoomService roomService;
    private final PlayerService playerService;
    private final RoomPasswordService roomPasswordService;
    private final GameStateService gameStateService;

    public GameService(RoomService roomService,
                       PlayerService playerService,
                       RoomPasswordService roomPasswordService,
                       GameStateService gameStateService) {
        this.roomService = roomService;
        this.playerService = playerService;
        this.roomPasswordService = roomPasswordService;
        this.gameStateService = gameStateService;
    }

    // ==================== Room Operations ====================

    /**
     * Creates a room with server-side password hashing.
     * @param rawPassword The raw password (will be hashed server-side)
     */
    public GameRoom createRoom(String hostName, Integer avatarId, QuizSettings settings,
                               boolean hostWantsToJoin, String rawPassword) {
        String hashedPassword = roomPasswordService.hashPassword(rawPassword);
        return roomService.createRoom(hostName, avatarId, settings, hostWantsToJoin, hashedPassword);
    }

    public GameRoom getRoom(String roomCode) {
        return roomService.getRoom(roomCode);
    }

    public void saveRoom(GameRoom room) {
        roomService.save(room);
    }

    public List<GameRoom> getAllRooms() {
        return roomService.getAllRooms();
    }

    public boolean startRoom(String roomCode) {
        return roomService.startRoom(roomCode);
    }

    public boolean disbandRoom(String roomCode) {
        return roomService.disbandRoom(roomCode);
    }

    // ==================== Player Operations ====================

    public Player joinRoom(String roomCode, String playerName, Integer avatarId) {
        GameRoom room = roomService.findByRoomCode(roomCode);
        if (room == null) {
            return null;
        }
        return playerService.joinRoom(room, playerName, avatarId);
    }

    public boolean leaveRoom(String roomCode, String playerId) {
        return playerService.leaveRoom(roomCode, playerId);
    }

    public List<Player> getPlayersByRoomCode(String roomCode) {
        GameRoom room = roomService.findByRoomCode(roomCode);
        if (room == null) {
            return null;
        }
        return playerService.getPlayersByRoom(room);
    }

    // ==================== Password Operations ====================

    public boolean checkRoomPassword(String roomCode, String hashedPassword) {
        return roomPasswordService.checkPassword(roomCode, hashedPassword);
    }

    public void throwIfRoomPasswordInvalid(String roomCode, String hashedPassword) {
        roomPasswordService.validatePassword(roomCode, hashedPassword);
    }

    // ==================== Game State Operations ====================

    public Map<String, Object> getGameState(String roomCode, String playerId) {
        return gameStateService.getGameState(roomCode, playerId);
    }

    public Map<String, Object> getSimpleState(String roomCode, String playerId) {
        return gameStateService.getSimpleState(roomCode, playerId);
    }

    public boolean goToNextQuestion(GameRoom room) {
        return gameStateService.goToNextQuestion(room);
    }

    // ==================== Direct Service Access ====================

    public RoomService getRoomService() {
        return roomService;
    }

    public PlayerService getPlayerService() {
        return playerService;
    }

    public GameStateService getGameStateService() {
        return gameStateService;
    }

    public RoomPasswordService getRoomPasswordService() {
        return roomPasswordService;
    }
}
