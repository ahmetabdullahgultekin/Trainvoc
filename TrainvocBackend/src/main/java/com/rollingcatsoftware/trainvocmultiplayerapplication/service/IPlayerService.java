package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;

import java.util.List;

/**
 * Interface for player management operations.
 * Enables dependency inversion and easier testing.
 */
public interface IPlayerService {

    Player createPlayer(GameRoom room, String name, Integer avatarId);

    Player joinRoom(GameRoom room, String playerName, Integer avatarId);

    boolean leaveRoom(String roomCode, String playerId);

    List<Player> getPlayersByRoom(GameRoom room);

    Player findById(String playerId);

    Player save(Player player);

    void resetAnswersForRoom(GameRoom room);
}
