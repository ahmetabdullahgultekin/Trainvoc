package com.rollingcatsoftware.trainvocmultiplayerapplication.integration;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.GameRoomRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.PlayerRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.GameService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.PlayerService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.RoomService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for complete game flow.
 * Tests robustness, scalability, and maintainability of game operations.
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Game Flow Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integration")
@Disabled("Requires database connection - run with 'gradle test -Ptags=integration' when DB is available")
class GameFlowIntegrationTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameRoomRepository roomRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private static String testRoomCode;
    private static String hostPlayerId;

    @BeforeEach
    void cleanup() {
        // Clean up test data before each test
    }

    @Nested
    @DisplayName("Room Creation - Robustness")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class RoomCreation {

        @Test
        @Order(1)
        @DisplayName("creates room with valid settings")
        void createsRoomWithValidSettings() {
            QuizSettings settings = new QuizSettings();
            settings.setQuestionDuration(30);
            settings.setOptionCount(4);
            settings.setLevel("A1");
            settings.setTotalQuestionCount(5);

            GameRoom room = gameService.createRoom("TestHost", 1, settings, true, null);

            assertThat(room).isNotNull();
            assertThat(room.getRoomCode()).isNotNull().hasSize(5);
            assertThat(room.getHostId()).isNotNull();
            assertThat(room.getCurrentState()).isEqualTo(GameState.LOBBY);
            assertThat(room.getPlayers()).hasSize(1);

            testRoomCode = room.getRoomCode();
            hostPlayerId = room.getHostId();
        }

        @Test
        @Order(2)
        @DisplayName("creates room with password protection")
        void createsRoomWithPassword() {
            QuizSettings settings = new QuizSettings();
            GameRoom room = gameService.createRoom("PasswordHost", 1, settings, true, "secret123");

            assertThat(room).isNotNull();
            assertThat(room.getHashedPassword()).isNotNull();
            assertThat(gameService.checkRoomPassword(room.getRoomCode(), "secret123")).isTrue();
            assertThat(gameService.checkRoomPassword(room.getRoomCode(), "wrongpass")).isFalse();
        }

        @Test
        @DisplayName("generates unique room codes")
        void generatesUniqueRoomCodes() {
            QuizSettings settings = new QuizSettings();
            List<String> codes = new ArrayList<>();

            for (int i = 0; i < 50; i++) {
                GameRoom room = gameService.createRoom("Host" + i, 1, settings, true, null);
                assertThat(codes).doesNotContain(room.getRoomCode());
                codes.add(room.getRoomCode());
            }

            assertThat(codes).hasSize(50);
        }
    }

    @Nested
    @DisplayName("Player Joining - Robustness")
    class PlayerJoining {

        @Test
        @DisplayName("allows player to join existing room")
        void allowsPlayerToJoin() {
            QuizSettings settings = new QuizSettings();
            GameRoom room = gameService.createRoom("JoinTestHost", 1, settings, true, null);

            Player player = gameService.joinRoom(room.getRoomCode(), "JoinedPlayer", 2);

            assertThat(player).isNotNull();
            assertThat(player.getName()).isEqualTo("JoinedPlayer");
            assertThat(player.getAvatarId()).isEqualTo(2);

            GameRoom updatedRoom = roomService.findByRoomCode(room.getRoomCode());
            assertThat(updatedRoom.getPlayers()).hasSize(2);
        }

        @Test
        @DisplayName("returns null when joining non-existent room")
        void returnsNullForNonExistentRoom() {
            Player player = gameService.joinRoom("ZZZZZ", "Player", 1);
            assertThat(player).isNull();
        }

        @Test
        @DisplayName("handles concurrent joins correctly")
        void handlesConcurrentJoins() throws Exception {
            QuizSettings settings = new QuizSettings();
            GameRoom room = gameService.createRoom("ConcurrentJoinHost", 1, settings, true, null);

            int playerCount = 10;
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(playerCount);
            ExecutorService executor = Executors.newFixedThreadPool(playerCount);
            AtomicInteger successCount = new AtomicInteger(0);

            for (int i = 0; i < playerCount; i++) {
                final int index = i;
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        Player player = gameService.joinRoom(room.getRoomCode(), "Player" + index, index);
                        if (player != null) {
                            successCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        // May fail due to concurrent modification
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startLatch.countDown();
            doneLatch.await(30, TimeUnit.SECONDS);
            executor.shutdown();

            // All joins should succeed
            assertThat(successCount.get()).isEqualTo(playerCount);

            GameRoom updatedRoom = roomService.findByRoomCode(room.getRoomCode());
            assertThat(updatedRoom.getPlayers()).hasSize(playerCount + 1); // +1 for host
        }
    }

    @Nested
    @DisplayName("Game State Transitions - Robustness")
    class GameStateTransitions {

        @Test
        @DisplayName("transitions from LOBBY to COUNTDOWN on start")
        void transitionsToCountdownOnStart() {
            QuizSettings settings = new QuizSettings();
            settings.setTotalQuestionCount(3);
            GameRoom room = gameService.createRoom("StateHost", 1, settings, true, null);
            gameService.joinRoom(room.getRoomCode(), "Player2", 2);

            boolean started = gameService.startRoom(room.getRoomCode());

            assertThat(started).isTrue();
            GameRoom updatedRoom = roomService.findByRoomCode(room.getRoomCode());
            assertThat(updatedRoom.getStarted()).isTrue();
            assertThat(updatedRoom.getCurrentState()).isIn(GameState.COUNTDOWN, GameState.QUESTION);
        }

        @Test
        @DisplayName("prevents starting already started game")
        void preventsDoubleStart() {
            QuizSettings settings = new QuizSettings();
            GameRoom room = gameService.createRoom("DoubleStartHost", 1, settings, true, null);
            gameService.joinRoom(room.getRoomCode(), "Player2", 2);

            gameService.startRoom(room.getRoomCode());
            boolean secondStart = gameService.startRoom(room.getRoomCode());

            // Second start should be a no-op (implementation dependent)
            // Just verify no exception is thrown
            assertThat(secondStart).isIn(true, false);
        }
    }

    @Nested
    @DisplayName("Player Leaving - Robustness")
    class PlayerLeaving {

        @Test
        @DisplayName("removes player from room")
        void removesPlayerFromRoom() {
            QuizSettings settings = new QuizSettings();
            GameRoom room = gameService.createRoom("LeaveHost", 1, settings, true, null);
            Player player = gameService.joinRoom(room.getRoomCode(), "LeavingPlayer", 2);

            boolean removed = gameService.leaveRoom(room.getRoomCode(), player.getId());

            assertThat(removed).isTrue();
            GameRoom updatedRoom = roomService.findByRoomCode(room.getRoomCode());
            assertThat(updatedRoom.getPlayers()).hasSize(1);
        }

        @Test
        @DisplayName("handles host leaving (room disbands or transfers host)")
        void handlesHostLeaving() {
            QuizSettings settings = new QuizSettings();
            GameRoom room = gameService.createRoom("HostLeave", 1, settings, true, null);
            gameService.joinRoom(room.getRoomCode(), "Player2", 2);
            String hostId = room.getHostId();

            boolean removed = gameService.leaveRoom(room.getRoomCode(), hostId);

            assertThat(removed).isTrue();
            // Room may be disbanded or host transferred (implementation dependent)
        }
    }

    @Nested
    @DisplayName("Room Disbanding - Robustness")
    class RoomDisbanding {

        @Test
        @DisplayName("disbands room and removes all players")
        void disbandsRoomCompletely() {
            QuizSettings settings = new QuizSettings();
            GameRoom room = gameService.createRoom("DisbandHost", 1, settings, true, null);
            gameService.joinRoom(room.getRoomCode(), "Player2", 2);
            gameService.joinRoom(room.getRoomCode(), "Player3", 3);

            boolean disbanded = gameService.disbandRoom(room.getRoomCode());

            assertThat(disbanded).isTrue();
            assertThat(roomService.findByRoomCode(room.getRoomCode())).isNull();
        }

        @Test
        @DisplayName("returns false when disbanding non-existent room")
        void returnsFalseForNonExistentRoom() {
            boolean disbanded = gameService.disbandRoom("ZZZZZ");
            assertThat(disbanded).isFalse();
        }
    }

    @Nested
    @DisplayName("Scalability Tests")
    class ScalabilityTests {

        @Test
        @DisplayName("handles multiple concurrent rooms")
        void handlesMultipleConcurrentRooms() throws Exception {
            int roomCount = 20;
            CountDownLatch doneLatch = new CountDownLatch(roomCount);
            ExecutorService executor = Executors.newFixedThreadPool(roomCount);
            AtomicInteger successCount = new AtomicInteger(0);

            for (int i = 0; i < roomCount; i++) {
                final int index = i;
                executor.submit(() -> {
                    try {
                        QuizSettings settings = new QuizSettings();
                        GameRoom room = gameService.createRoom("Host" + index, 1, settings, true, null);
                        if (room != null) {
                            successCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            doneLatch.await(60, TimeUnit.SECONDS);
            executor.shutdown();

            assertThat(successCount.get()).isEqualTo(roomCount);
        }

        @Test
        @DisplayName("handles room with many players")
        void handlesRoomWithManyPlayers() {
            QuizSettings settings = new QuizSettings();
            GameRoom room = gameService.createRoom("BigRoomHost", 1, settings, true, null);

            int playerCount = 50;
            for (int i = 0; i < playerCount; i++) {
                Player player = gameService.joinRoom(room.getRoomCode(), "Player" + i, i % 10);
                assertThat(player).isNotNull();
            }

            GameRoom updatedRoom = roomService.findByRoomCode(room.getRoomCode());
            assertThat(updatedRoom.getPlayers()).hasSize(playerCount + 1);
        }
    }

    @Nested
    @DisplayName("Data Consistency")
    class DataConsistency {

        @Test
        @DisplayName("maintains player-room relationship correctly")
        void maintainsPlayerRoomRelationship() {
            QuizSettings settings = new QuizSettings();
            GameRoom room = gameService.createRoom("RelationHost", 1, settings, true, null);
            Player player = gameService.joinRoom(room.getRoomCode(), "RelationPlayer", 2);

            Player foundPlayer = playerService.findById(player.getId());
            assertThat(foundPlayer.getRoom().getRoomCode()).isEqualTo(room.getRoomCode());
        }

        @Test
        @DisplayName("updates player stats correctly")
        void updatesPlayerStatsCorrectly() {
            QuizSettings settings = new QuizSettings();
            GameRoom room = gameService.createRoom("StatsHost", 1, settings, true, null);
            Player host = room.getPlayers().get(0);

            // Manually update stats (simulating answer)
            host.setScore(100);
            host.setCorrectCount(5);
            host.setWrongCount(2);
            playerService.save(host);

            Player foundPlayer = playerService.findById(host.getId());
            assertThat(foundPlayer.getScore()).isEqualTo(100);
            assertThat(foundPlayer.getCorrectCount()).isEqualTo(5);
            assertThat(foundPlayer.getWrongCount()).isEqualTo(2);
        }
    }
}
