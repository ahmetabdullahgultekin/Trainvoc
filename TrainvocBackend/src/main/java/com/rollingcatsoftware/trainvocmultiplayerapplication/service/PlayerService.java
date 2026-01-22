package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.config.GameConstants;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Service responsible for player management operations.
 * Handles player creation, joining rooms, and leaving rooms.
 */
@Service
public class PlayerService implements IPlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * Creates a new player with the given attributes.
     */
    public Player createPlayer(GameRoom room, String name, Integer avatarId) {
        Player player = new Player();
        player.setId(UUID.randomUUID().toString());
        player.setRoom(room);
        player.setName(name);
        player.setScore(0);
        player.setCorrectCount(0);
        player.setWrongCount(0);
        player.setTotalAnswerTime(0);

        if (GameConstants.isValidAvatarId(avatarId)) {
            player.setAvatarId(avatarId);
        } else {
            player.setAvatarId(getRandomAvatarId());
        }

        return player;
    }

    /**
     * Creates and saves a player who is joining a room.
     * @throws IllegalStateException if the room is not in LOBBY state
     */
    public Player joinRoom(GameRoom room, String playerName, Integer avatarId) {
        if (room.getCurrentState() != GameState.LOBBY) {
            throw new IllegalStateException("Cannot join room after game has started.");
        }

        Player player = createPlayer(room, playerName, avatarId);
        return playerRepository.save(player);
    }

    /**
     * Removes a player from a room.
     * @return true if player was found and removed, false otherwise
     */
    public boolean leaveRoom(String roomCode, String playerId) {
        Player player = playerRepository.findById(playerId).orElse(null);
        if (player != null && player.getRoom() != null &&
                player.getRoom().getRoomCode().equals(roomCode)) {
            playerRepository.delete(player);
            return true;
        }
        return false;
    }

    /**
     * Gets all players in a room.
     */
    public List<Player> getPlayersByRoom(GameRoom room) {
        return playerRepository.findByRoom(room);
    }

    /**
     * Finds a player by ID.
     */
    public Player findById(String playerId) {
        return playerRepository.findById(playerId).orElse(null);
    }

    /**
     * Saves a player.
     */
    public Player save(Player player) {
        return playerRepository.save(player);
    }

    /**
     * Resets answer tracking for all players in a room (for new question).
     */
    public void resetAnswersForRoom(GameRoom room) {
        List<Player> players = playerRepository.findByRoom(room);
        for (Player player : players) {
            player.setCurrentAnsweredQuestionIndex(null);
            playerRepository.save(player);
        }
    }

    private int getRandomAvatarId() {
        return ThreadLocalRandom.current().nextInt(GameConstants.getAvatarCount());
    }
}
