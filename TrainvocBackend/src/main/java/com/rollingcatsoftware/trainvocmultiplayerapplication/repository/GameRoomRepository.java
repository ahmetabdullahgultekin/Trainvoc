package com.rollingcatsoftware.trainvocmultiplayerapplication.repository;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    /**
     * Insert a room using native SQL for debugging.
     * flushAutomatically ensures the query is executed immediately.
     * clearAutomatically clears the persistence context after execution.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO game_room (room_code, current_question_index, started, host_id, question_duration, option_count, level, total_question_count, current_state, last_used, version) " +
            "VALUES (:roomCode, :currentQuestionIndex, :started, :hostId, :questionDuration, :optionCount, :level, :totalQuestionCount, :currentState, :lastUsed, 0)",
            nativeQuery = true)
    void insertRoom(@Param("roomCode") String roomCode,
                    @Param("currentQuestionIndex") int currentQuestionIndex,
                    @Param("started") boolean started,
                    @Param("hostId") String hostId,
                    @Param("questionDuration") int questionDuration,
                    @Param("optionCount") int optionCount,
                    @Param("level") String level,
                    @Param("totalQuestionCount") int totalQuestionCount,
                    @Param("currentState") int currentState,
                    @Param("lastUsed") LocalDateTime lastUsed);
}
