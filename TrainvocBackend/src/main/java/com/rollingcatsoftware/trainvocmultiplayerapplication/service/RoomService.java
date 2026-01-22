package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.config.GameConstants;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.GameRoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service responsible for room CRUD operations.
 * Handles room creation, retrieval, and deletion.
 */
@Service
public class RoomService implements IRoomService {

    private final GameRoomRepository gameRoomRepository;
    private final PlayerService playerService;

    public RoomService(GameRoomRepository gameRoomRepository, PlayerService playerService) {
        this.gameRoomRepository = gameRoomRepository;
        this.playerService = playerService;
    }

    /**
     * Creates a new game room with the specified settings.
     */
    public GameRoom createRoom(String hostName, Integer avatarId, QuizSettings settings,
                               boolean hostWantsToJoin, String hashedPassword) {
        GameRoom room = new GameRoom();
        room.setRoomCode(generateRoomCode());
        room.setCurrentQuestionIndex(0);
        room.setStarted(false);
        room.setQuestionDuration(settings.getQuestionDuration());
        room.setOptionCount(settings.getOptionCount());
        room.setLevel(settings.getLevel());
        room.setTotalQuestionCount(settings.getTotalQuestionCount());
        room.setLastUsed(LocalDateTime.now());
        room.setHashedPassword(hashedPassword);

        room = gameRoomRepository.save(room);

        Player host = playerService.createPlayer(room, hostName, avatarId);
        playerService.save(host);

        room.getPlayers().clear();
        if (hostWantsToJoin) {
            room.getPlayers().add(host);
        }
        room.setHostId(host.getId());

        return gameRoomRepository.save(room);
    }

    /**
     * Retrieves a room by its code and updates last used timestamp.
     */
    public GameRoom getRoom(String roomCode) {
        GameRoom room = gameRoomRepository.findById(roomCode).orElse(null);
        if (room != null) {
            room.setLastUsed(LocalDateTime.now());
            gameRoomRepository.save(room);
        }
        return room;
    }

    /**
     * Retrieves a room by code without updating last used.
     */
    public GameRoom findByRoomCode(String roomCode) {
        return gameRoomRepository.findByRoomCode(roomCode);
    }

    /**
     * Saves a room.
     */
    public GameRoom save(GameRoom room) {
        return gameRoomRepository.save(room);
    }

    /**
     * Retrieves all rooms.
     */
    public List<GameRoom> getAllRooms() {
        return gameRoomRepository.findAll();
    }

    /**
     * Starts the game in a room by setting state to COUNTDOWN.
     * @return true if room was found and started, false otherwise
     */
    public boolean startRoom(String roomCode) {
        GameRoom room = gameRoomRepository.findByRoomCode(roomCode);
        if (room != null) {
            room.setStarted(true);
            room.setCurrentState(GameState.COUNTDOWN);
            room.setStateStartTime(LocalDateTime.now());
            gameRoomRepository.save(room);
            return true;
        }
        return false;
    }

    /**
     * Deletes a room and all associated players.
     * @return true if room was found and deleted, false otherwise
     */
    public boolean disbandRoom(String roomCode) {
        GameRoom room = gameRoomRepository.findByRoomCode(roomCode);
        if (room != null) {
            gameRoomRepository.delete(room);
            return true;
        }
        return false;
    }

    private String generateRoomCode() {
        return UUID.randomUUID().toString()
                .substring(0, GameConstants.ROOM_CODE_LENGTH)
                .toUpperCase();
    }
}
