package com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.PlayerService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.RoomService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AnswerHandler.
 * Tests robustness, edge cases, and concurrency handling.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AnswerHandler Tests")
class AnswerHandlerTest {

    @Mock
    private RoomService roomService;

    @Mock
    private PlayerService playerService;

    @Mock
    private WebSocketContext context;

    @Mock
    private WebSocketSession session;

    private AnswerHandler answerHandler;
    private GameRoom testRoom;
    private Player testPlayer;

    @BeforeEach
    void setUp() {
        answerHandler = new AnswerHandler(roomService, playerService, context);

        testRoom = new GameRoom();
        testRoom.setRoomCode("TEST1");
        testRoom.setCurrentState(GameState.QUESTION);
        testRoom.setCurrentQuestionIndex(0);
        testRoom.setQuestionDuration(60);
        testRoom.setPlayers(new ArrayList<>());

        testPlayer = new Player();
        testPlayer.setId("player-1");
        testPlayer.setName("TestPlayer");
        testPlayer.setScore(0);
        testPlayer.setCorrectCount(0);
        testPlayer.setWrongCount(0);
        testPlayer.setTotalAnswerTime(0);
        testPlayer.setCurrentAnsweredQuestionIndex(null);
        testPlayer.setRoom(testRoom);

        testRoom.getPlayers().add(testPlayer);
    }

    @Nested
    @DisplayName("Message Type")
    class MessageType {

        @Test
        @DisplayName("returns 'answer' as message type")
        void returnsAnswerAsMessageType() {
            assertThat(answerHandler.getMessageType()).isEqualTo("answer");
        }
    }

    @Nested
    @DisplayName("Valid Answer Submission")
    class ValidAnswerSubmission {

        @Test
        @DisplayName("accepts valid answer and updates score")
        void acceptsValidAnswer() throws Exception {
            when(roomService.findByRoomCode("TEST1")).thenReturn(testRoom);
            when(playerService.findById("player-1")).thenReturn(testPlayer);
            when(playerService.getPlayersByRoom(testRoom)).thenReturn(List.of(testPlayer));

            JSONObject message = new JSONObject();
            message.put("roomCode", "TEST1");
            message.put("playerId", "player-1");
            message.put("answerIndex", 0);
            message.put("answerTime", 10);
            message.put("isCorrect", true);

            answerHandler.handle(session, message, context);

            verify(playerService).save(testPlayer);
            assertThat(testPlayer.getScore()).isGreaterThan(0);
            assertThat(testPlayer.getCurrentAnsweredQuestionIndex()).isEqualTo(0);
        }

        @Test
        @DisplayName("calculates time bonus for fast answers")
        void calculatesTimeBonus() throws Exception {
            when(roomService.findByRoomCode("TEST1")).thenReturn(testRoom);
            when(playerService.findById("player-1")).thenReturn(testPlayer);
            when(playerService.getPlayersByRoom(testRoom)).thenReturn(List.of(testPlayer));

            JSONObject message = new JSONObject();
            message.put("roomCode", "TEST1");
            message.put("playerId", "player-1");
            message.put("answerIndex", 0);
            message.put("answerTime", 1); // Very fast answer
            message.put("isCorrect", true);

            answerHandler.handle(session, message, context);

            // Fast answer should get higher score (base 50 + time bonus up to 20)
            // With 1 second out of 60, time ratio ≈ 0.98, so bonus ≈ 19
            assertThat(testPlayer.getScore()).isGreaterThan(60); // 50 + some bonus
        }

        @Test
        @DisplayName("gives negative score for incorrect answers")
        void negativeScoreForIncorrect() throws Exception {
            when(roomService.findByRoomCode("TEST1")).thenReturn(testRoom);
            when(playerService.findById("player-1")).thenReturn(testPlayer);
            when(playerService.getPlayersByRoom(testRoom)).thenReturn(List.of(testPlayer));

            JSONObject message = new JSONObject();
            message.put("roomCode", "TEST1");
            message.put("playerId", "player-1");
            message.put("answerIndex", 1);
            message.put("answerTime", 10);
            message.put("isCorrect", false);

            answerHandler.handle(session, message, context);

            // Incorrect answer gets MIN_SCORE (-50)
            assertThat(testPlayer.getScore()).isEqualTo(-50);
            assertThat(testPlayer.getWrongCount()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("rejects answer when room not found")
        void rejectsWhenRoomNotFound() throws Exception {
            when(roomService.findByRoomCode("INVALID")).thenReturn(null);

            JSONObject message = new JSONObject();
            message.put("roomCode", "INVALID");
            message.put("playerId", "player-1");
            message.put("answerIndex", 0);
            message.put("answerTime", 10);

            answerHandler.handle(session, message, context);

            ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
            verify(context).sendMessage(eq(session), captor.capture());
            assertThat(captor.getValue().getString("type")).isEqualTo("error");
            assertThat(captor.getValue().getString("message")).contains("Room not found");
        }

        @Test
        @DisplayName("rejects answer when game not in QUESTION state")
        void rejectsWhenNotInQuestionState() throws Exception {
            testRoom.setCurrentState(GameState.LOBBY);
            when(roomService.findByRoomCode("TEST1")).thenReturn(testRoom);

            JSONObject message = new JSONObject();
            message.put("roomCode", "TEST1");
            message.put("playerId", "player-1");
            message.put("answerIndex", 0);
            message.put("answerTime", 10);

            answerHandler.handle(session, message, context);

            ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
            verify(context).sendMessage(eq(session), captor.capture());
            assertThat(captor.getValue().getString("type")).isEqualTo("error");
        }

        @Test
        @DisplayName("rejects answer when player not found")
        void rejectsWhenPlayerNotFound() throws Exception {
            when(roomService.findByRoomCode("TEST1")).thenReturn(testRoom);
            when(playerService.findById("unknown")).thenReturn(null);

            JSONObject message = new JSONObject();
            message.put("roomCode", "TEST1");
            message.put("playerId", "unknown");
            message.put("answerIndex", 0);
            message.put("answerTime", 10);

            answerHandler.handle(session, message, context);

            ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
            verify(context).sendMessage(eq(session), captor.capture());
            assertThat(captor.getValue().getString("type")).isEqualTo("error");
        }

        @Test
        @DisplayName("rejects duplicate answer for same question")
        void rejectsDuplicateAnswer() throws Exception {
            testPlayer.setCurrentAnsweredQuestionIndex(0); // Already answered
            when(roomService.findByRoomCode("TEST1")).thenReturn(testRoom);
            when(playerService.findById("player-1")).thenReturn(testPlayer);

            JSONObject message = new JSONObject();
            message.put("roomCode", "TEST1");
            message.put("playerId", "player-1");
            message.put("answerIndex", 0);
            message.put("answerTime", 10);

            answerHandler.handle(session, message, context);

            ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
            verify(context).sendMessage(eq(session), captor.capture());
            assertThat(captor.getValue().getString("type")).isEqualTo("error");
            assertThat(captor.getValue().getString("message")).contains("Already answered");
            verify(playerService, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Concurrency - Race Condition Prevention")
    class ConcurrencyTests {

        @Test
        @DisplayName("prevents duplicate answers under concurrent requests")
        void preventsDuplicateUnderConcurrency() throws Exception {
            // Setup: Fresh player for each call (simulating real DB fetch)
            AtomicInteger saveCount = new AtomicInteger(0);

            when(roomService.findByRoomCode("TEST1")).thenReturn(testRoom);
            when(playerService.findById("player-1")).thenAnswer(invocation -> {
                Player fresh = new Player();
                fresh.setId("player-1");
                fresh.setName("TestPlayer");
                fresh.setScore(0);
                fresh.setRoom(testRoom);
                fresh.setCurrentAnsweredQuestionIndex(saveCount.get() > 0 ? 0 : null);
                return fresh;
            });
            when(playerService.getPlayersByRoom(testRoom)).thenReturn(List.of(testPlayer));
            doAnswer(invocation -> {
                saveCount.incrementAndGet();
                return null;
            }).when(playerService).save(any(Player.class));

            // Execute concurrent requests
            int threadCount = 10;
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);

            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        JSONObject message = new JSONObject();
                        message.put("roomCode", "TEST1");
                        message.put("playerId", "player-1");
                        message.put("answerIndex", 0);
                        message.put("answerTime", 10);
                        message.put("isCorrect", true);
                        answerHandler.handle(session, message, context);
                    } catch (Exception e) {
                        // Expected for duplicate answers
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startLatch.countDown();
            doneLatch.await(10, TimeUnit.SECONDS);
            executor.shutdown();

            // Only ONE save should succeed due to synchronized block
            assertThat(saveCount.get()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Broadcasting")
    class Broadcasting {

        @Test
        @DisplayName("broadcasts rankings when all players answered")
        void broadcastsRankingsWhenAllAnswered() throws Exception {
            Player player2 = new Player();
            player2.setId("player-2");
            player2.setName("Player2");
            player2.setRoom(testRoom);
            player2.setCurrentAnsweredQuestionIndex(0); // Already answered
            testRoom.getPlayers().add(player2);

            when(roomService.findByRoomCode("TEST1")).thenReturn(testRoom);
            when(playerService.findById("player-1")).thenReturn(testPlayer);
            when(playerService.getPlayersByRoom(testRoom)).thenReturn(List.of(testPlayer, player2));

            JSONObject message = new JSONObject();
            message.put("roomCode", "TEST1");
            message.put("playerId", "player-1");
            message.put("answerIndex", 0);
            message.put("answerTime", 10);
            message.put("isCorrect", true);

            answerHandler.handle(session, message, context);

            // Should broadcast rankings
            verify(context, atLeast(1)).broadcastToRoom(eq(testRoom), any(JSONObject.class));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("handles zero answer time")
        void handlesZeroAnswerTime() throws Exception {
            when(roomService.findByRoomCode("TEST1")).thenReturn(testRoom);
            when(playerService.findById("player-1")).thenReturn(testPlayer);
            when(playerService.getPlayersByRoom(testRoom)).thenReturn(List.of(testPlayer));

            JSONObject message = new JSONObject();
            message.put("roomCode", "TEST1");
            message.put("playerId", "player-1");
            message.put("answerIndex", 0);
            message.put("answerTime", 0);
            message.put("isCorrect", true);

            answerHandler.handle(session, message, context);

            // Should still process successfully
            verify(playerService).save(testPlayer);
            assertThat(testPlayer.getTotalAnswerTime()).isEqualTo(0);
        }

        @Test
        @DisplayName("handles missing optional fields with defaults")
        void handlesMissingOptionalFields() throws Exception {
            when(roomService.findByRoomCode("TEST1")).thenReturn(testRoom);
            when(playerService.findById("player-1")).thenReturn(testPlayer);
            when(playerService.getPlayersByRoom(testRoom)).thenReturn(List.of(testPlayer));

            JSONObject message = new JSONObject();
            message.put("roomCode", "TEST1");
            message.put("playerId", "player-1");
            message.put("answerIndex", 0);
            // Missing answerTime and isCorrect - should use defaults

            answerHandler.handle(session, message, context);

            verify(playerService).save(testPlayer);
        }

        @Test
        @DisplayName("handles player in different room")
        void handlesPlayerInDifferentRoom() throws Exception {
            GameRoom otherRoom = new GameRoom();
            otherRoom.setRoomCode("OTHER");
            testPlayer.setRoom(otherRoom);

            when(roomService.findByRoomCode("TEST1")).thenReturn(testRoom);
            when(playerService.findById("player-1")).thenReturn(testPlayer);

            JSONObject message = new JSONObject();
            message.put("roomCode", "TEST1");
            message.put("playerId", "player-1");
            message.put("answerIndex", 0);
            message.put("answerTime", 10);

            answerHandler.handle(session, message, context);

            ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
            verify(context).sendMessage(eq(session), captor.capture());
            assertThat(captor.getValue().getString("type")).isEqualTo("error");
        }
    }
}
