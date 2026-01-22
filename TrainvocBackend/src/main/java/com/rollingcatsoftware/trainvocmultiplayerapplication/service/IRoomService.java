package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;

import java.util.List;

/**
 * Interface for room management operations.
 * Enables dependency inversion and easier testing.
 */
public interface IRoomService {

    GameRoom createRoom(String hostName, Integer avatarId, QuizSettings settings,
                        boolean hostWantsToJoin, String hashedPassword);

    GameRoom getRoom(String roomCode);

    GameRoom findByRoomCode(String roomCode);

    GameRoom save(GameRoom room);

    List<GameRoom> getAllRooms();

    boolean startRoom(String roomCode);

    boolean disbandRoom(String roomCode);
}
