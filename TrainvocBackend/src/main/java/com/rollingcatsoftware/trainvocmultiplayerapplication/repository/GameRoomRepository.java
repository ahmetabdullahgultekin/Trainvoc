package com.rollingcatsoftware.trainvocmultiplayerapplication.repository;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for GameRoom entity.
 * Uses EntityGraph to prevent N+1 queries when fetching rooms with players.
 */
public interface GameRoomRepository extends JpaRepository<GameRoom, String> {

    void deleteByLastUsedBefore(LocalDateTime time);

    /**
     * Finds a room by code with players eagerly loaded.
     * Prevents N+1 query when accessing players.
     */
    @EntityGraph(attributePaths = {"players"})
    GameRoom findByRoomCode(String roomCode);

    /**
     * Finds a room by ID with players eagerly loaded.
     */
    @Override
    @EntityGraph(attributePaths = {"players"})
    Optional<GameRoom> findById(String id);

    /**
     * Finds all rooms with players eagerly loaded.
     * Prevents N+1 query when listing all rooms.
     */
    @Override
    @EntityGraph(attributePaths = {"players"})
    List<GameRoom> findAll();

    /**
     * Finds active rooms (not started) with players.
     */
    @EntityGraph(attributePaths = {"players"})
    @Query("SELECT r FROM GameRoom r WHERE r.started = false ORDER BY r.lastUsed DESC")
    List<GameRoom> findActiveRooms();

    /**
     * Finds rooms by started status with players.
     */
    @EntityGraph(attributePaths = {"players"})
    List<GameRoom> findByStarted(boolean started);
}
