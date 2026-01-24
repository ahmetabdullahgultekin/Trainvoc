package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.config.GameConstants;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.GameRoomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service responsible for room CRUD operations.
 * Handles room creation, retrieval, and deletion.
 * Uses programmatic transaction management for reliability.
 */
@Service
public class RoomService implements IRoomService {

    private static final Logger log = LoggerFactory.getLogger(RoomService.class);

    private final GameRoomRepository gameRoomRepository;
    private final PlayerService playerService;
    private final TransactionTemplate transactionTemplate;
    private final EntityManagerFactory entityManagerFactory;

    public RoomService(GameRoomRepository gameRoomRepository,
                       PlayerService playerService,
                       @Qualifier("primaryTransactionManager") PlatformTransactionManager transactionManager,
                       @Qualifier("primaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        this.gameRoomRepository = gameRoomRepository;
        this.playerService = playerService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Creates a new game room with the specified settings.
     * Uses direct EntityManager management to ensure proper transaction handling
     * from WebSocket threads where Spring's thread-bound EntityManager may not work.
     */
    public GameRoom createRoom(String hostName, Integer avatarId, QuizSettings settings,
                               boolean hostWantsToJoin, String hashedPassword) {
        log.info("Creating room for host: {} (using direct EntityManager)", hostName);

        // Create our own EntityManager to avoid WebSocket thread binding issues
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            log.info("Transaction started: active={}", tx.isActive());

            String roomCode = generateRoomCode();
            LocalDateTime now = LocalDateTime.now();

            log.info("Inserting room with code: {} using native SQL via EntityManager", roomCode);

            // Execute native INSERT directly
            int rowsInserted = em.createNativeQuery(
                    "INSERT INTO game_room (room_code, current_question_index, started, host_id, " +
                            "question_duration, option_count, level, total_question_count, current_state, last_used, version) " +
                            "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, 0)")
                    .setParameter(1, roomCode)
                    .setParameter(2, 0)
                    .setParameter(3, false)
                    .setParameter(4, null)
                    .setParameter(5, settings.getQuestionDuration())
                    .setParameter(6, settings.getOptionCount())
                    .setParameter(7, settings.getLevel())
                    .setParameter(8, settings.getTotalQuestionCount())
                    .setParameter(9, GameState.LOBBY.ordinal())
                    .setParameter(10, now)
                    .executeUpdate();

            log.info("Native INSERT executed, rows affected: {}", rowsInserted);

            // Fetch the room we just created
            GameRoom room = em.createQuery("SELECT r FROM GameRoom r WHERE r.roomCode = :code", GameRoom.class)
                    .setParameter("code", roomCode)
                    .getSingleResult();

            log.info("Room fetched via EntityManager, roomCode: {}", room.getRoomCode());

            // Create player
            Player host = new Player();
            host.setId(UUID.randomUUID().toString());
            host.setRoom(room);
            host.setName(hostName);
            host.setScore(0);
            host.setCorrectCount(0);
            host.setWrongCount(0);
            host.setTotalAnswerTime(0);
            host.setAvatarId(avatarId != null && GameConstants.isValidAvatarId(avatarId)
                    ? avatarId : 0);

            em.persist(host);
            log.info("Host player persisted with id: {}", host.getId());

            // Update room with host info
            room.setHostId(host.getId());
            room.setHashedPassword(hashedPassword);
            if (hostWantsToJoin) {
                room.getPlayers().add(host);
            }

            em.merge(room);
            log.info("Room merged with host info");

            // Commit the transaction
            tx.commit();
            log.info("Transaction committed successfully for room: {}", roomCode);

            // Re-fetch the room with players to avoid LazyInitializationException
            // after the EntityManager is closed
            EntityManager readEm = entityManagerFactory.createEntityManager();
            try {
                GameRoom finalRoom = readEm.createQuery(
                        "SELECT r FROM GameRoom r LEFT JOIN FETCH r.players WHERE r.roomCode = :code",
                        GameRoom.class)
                        .setParameter("code", roomCode)
                        .getSingleResult();
                log.info("Room re-fetched with {} players", finalRoom.getPlayers().size());
                return finalRoom;
            } finally {
                readEm.close();
            }
        } catch (Exception e) {
            log.error("Error creating room, rolling back transaction", e);
            if (tx.isActive()) {
                tx.rollback();
                log.info("Transaction rolled back");
            }
            throw new RuntimeException("Failed to create room: " + e.getMessage(), e);
        } finally {
            em.close();
            log.info("EntityManager closed");
        }
    }

    /**
     * Retrieves a room by its code and updates last used timestamp.
     */
    public GameRoom getRoom(String roomCode) {
        return transactionTemplate.execute(status -> {
            GameRoom room = gameRoomRepository.findById(roomCode).orElse(null);
            if (room != null) {
                room.setLastUsed(LocalDateTime.now());
                gameRoomRepository.save(room);
            }
            return room;
        });
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
        return transactionTemplate.execute(status -> gameRoomRepository.save(room));
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
        Boolean result = transactionTemplate.execute(status -> {
            GameRoom room = gameRoomRepository.findByRoomCode(roomCode);
            if (room != null) {
                room.setStarted(true);
                room.setCurrentState(GameState.COUNTDOWN);
                room.setStateStartTime(LocalDateTime.now());
                gameRoomRepository.save(room);
                return true;
            }
            return false;
        });
        return result != null && result;
    }

    /**
     * Deletes a room and all associated players.
     * @return true if room was found and deleted, false otherwise
     */
    public boolean disbandRoom(String roomCode) {
        Boolean result = transactionTemplate.execute(status -> {
            GameRoom room = gameRoomRepository.findByRoomCode(roomCode);
            if (room != null) {
                gameRoomRepository.delete(room);
                return true;
            }
            return false;
        });
        return result != null && result;
    }

    private String generateRoomCode() {
        return UUID.randomUUID().toString()
                .substring(0, GameConstants.ROOM_CODE_LENGTH)
                .toUpperCase();
    }
}
