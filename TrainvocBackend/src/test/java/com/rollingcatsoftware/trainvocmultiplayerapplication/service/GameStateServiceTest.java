package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GameStateService Tests")
class GameStateServiceTest {

    @Mock
    private RoomService roomService;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private GameStateService gameStateService;

    private GameRoom testRoom;
    private Player testPlayer;
    private static final String ROOM_CODE = "ABC123";
    private static final String PLAYER_ID = "player-123";
    private static final String PLAYER_NAME = "TestPlayer";

    @BeforeEach
    void setUp() {
        testRoom = new GameRoom();
        testRoom.setRoomCode(ROOM_CODE);
        testRoom.setCurrentState(GameState.LOBBY);
        testRoom.setStateStartTime(LocalDateTime.now());
        testRoom.setQuestionDuration(30);
        testRoom.setCurrentQuestionIndex(0);
        testRoom.setTotalQuestionCount(10);

        testPlayer = new Player();
        testPlayer.setId(PLAYER_ID);
        testPlayer.setName(PLAYER_NAME);
        testPlayer.setRoom(testRoom);
        testPlayer.setScore(100);
    }

    @Nested
    @DisplayName("getGameState")
    class GetGameState {

        @Test
        @DisplayName("returns null when room not found")
        void returnsNull_whenRoomNotFound() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(null);

            Map<String, Object> result = gameStateService.getGameState(ROOM_CODE, PLAYER_ID);

            assertNull(result);
        }

        @Test
        @DisplayName("returns null when player not found")
        void returnsNull_whenPlayerNotFound() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(testRoom);
            when(playerService.findById(PLAYER_ID)).thenReturn(null);

            Map<String, Object> result = gameStateService.getGameState(ROOM_CODE, PLAYER_ID);

            assertNull(result);
        }

        @Test
        @DisplayName("returns null when player not in room")
        void returnsNull_whenPlayerNotInRoom() {
            GameRoom otherRoom = new GameRoom();
            otherRoom.setRoomCode("OTHER");
            testPlayer.setRoom(otherRoom);

            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(testRoom);
            when(playerService.findById(PLAYER_ID)).thenReturn(testPlayer);

            Map<String, Object> result = gameStateService.getGameState(ROOM_CODE, PLAYER_ID);

            assertNull(result);
        }

        @Test
        @DisplayName("returns game state for valid request")
        void returnsGameState_forValidRequest() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(testRoom);
            when(playerService.findById(PLAYER_ID)).thenReturn(testPlayer);
            when(playerService.getPlayersByRoom(testRoom)).thenReturn(List.of(testPlayer));

            Map<String, Object> result = gameStateService.getGameState(ROOM_CODE, PLAYER_ID);

            assertNotNull(result);
            assertEquals(GameState.LOBBY.ordinal(), result.get("state"));
            assertEquals(0, result.get("remainingTime"));
            assertEquals(0, result.get("currentQuestionIndex"));
            assertEquals(10, result.get("totalQuestionCount"));
            assertNotNull(result.get("players"));
        }

        @Test
        @DisplayName("returns state without player validation when playerId is null")
        void returnsState_whenPlayerIdIsNull() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(testRoom);
            when(playerService.getPlayersByRoom(testRoom)).thenReturn(List.of(testPlayer));

            Map<String, Object> result = gameStateService.getGameState(ROOM_CODE, null);

            assertNotNull(result);
            verify(playerService, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("getSimpleState")
    class GetSimpleState {

        @Test
        @DisplayName("returns null when room not found")
        void returnsNull_whenRoomNotFound() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(null);

            Map<String, Object> result = gameStateService.getSimpleState(ROOM_CODE, PLAYER_ID);

            assertNull(result);
        }

        @Test
        @DisplayName("returns null when player not found")
        void returnsNull_whenPlayerNotFound() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(testRoom);
            when(playerService.findById(PLAYER_ID)).thenReturn(null);

            Map<String, Object> result = gameStateService.getSimpleState(ROOM_CODE, PLAYER_ID);

            assertNull(result);
        }

        @Test
        @DisplayName("returns simple state for valid request")
        void returnsSimpleState_forValidRequest() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(testRoom);
            when(playerService.findById(PLAYER_ID)).thenReturn(testPlayer);

            Map<String, Object> result = gameStateService.getSimpleState(ROOM_CODE, PLAYER_ID);

            assertNotNull(result);
            assertEquals("lobby", result.get("state"));
            assertEquals(0, result.get("remainingTime"));
            assertEquals(2, result.size()); // Only state and remainingTime
        }
    }

    @Nested
    @DisplayName("goToNextQuestion")
    class GoToNextQuestion {

        @Test
        @DisplayName("returns false when room is null")
        void returnsFalse_whenRoomIsNull() {
            boolean result = gameStateService.goToNextQuestion(null);

            assertFalse(result);
            verify(roomService, never()).save(any());
        }

        @Test
        @DisplayName("advances to next question when not on last question")
        void advancesToNextQuestion_whenNotOnLastQuestion() {
            testRoom.setCurrentQuestionIndex(3);
            testRoom.setTotalQuestionCount(10);

            boolean result = gameStateService.goToNextQuestion(testRoom);

            assertTrue(result);
            assertEquals(4, testRoom.getCurrentQuestionIndex());
            assertEquals(GameState.COUNTDOWN, testRoom.getCurrentState());
            verify(roomService).save(testRoom);
            verify(playerService).resetAnswersForRoom(testRoom);
        }

        @Test
        @DisplayName("transitions to ranking when on last question")
        void transitionsToRanking_whenOnLastQuestion() {
            testRoom.setCurrentQuestionIndex(9);
            testRoom.setTotalQuestionCount(10);

            boolean result = gameStateService.goToNextQuestion(testRoom);

            assertFalse(result);
            assertEquals(GameState.RANKING, testRoom.getCurrentState());
            assertEquals(9, testRoom.getCurrentQuestionIndex()); // Index unchanged
            verify(roomService).save(testRoom);
            verify(playerService, never()).resetAnswersForRoom(any());
        }

        @Test
        @DisplayName("handles edge case of single question")
        void handlesEdgeCase_singleQuestion() {
            testRoom.setCurrentQuestionIndex(0);
            testRoom.setTotalQuestionCount(1);

            boolean result = gameStateService.goToNextQuestion(testRoom);

            assertFalse(result);
            assertEquals(GameState.RANKING, testRoom.getCurrentState());
        }
    }

    @Nested
    @DisplayName("State Transitions")
    class StateTransitions {

        @Test
        @DisplayName("countdown transitions to question after countdown seconds")
        void countdownTransitionsToQuestion() {
            testRoom.setCurrentState(GameState.COUNTDOWN);
            testRoom.setStateStartTime(LocalDateTime.now().minusSeconds(4)); // 4 seconds ago

            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(testRoom);
            when(playerService.findById(PLAYER_ID)).thenReturn(testPlayer);
            when(playerService.getPlayersByRoom(testRoom)).thenReturn(List.of(testPlayer));

            Map<String, Object> result = gameStateService.getGameState(ROOM_CODE, PLAYER_ID);

            // Should transition when countdown is over
            assertNotNull(result);
            verify(roomService, atLeastOnce()).findByRoomCode(ROOM_CODE);
        }

        @Test
        @DisplayName("question transitions to answer reveal after question duration")
        void questionTransitionsToAnswerReveal() {
            testRoom.setCurrentState(GameState.QUESTION);
            testRoom.setQuestionDuration(30);
            testRoom.setStateStartTime(LocalDateTime.now().minusSeconds(31)); // 31 seconds ago

            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(testRoom);
            when(playerService.findById(PLAYER_ID)).thenReturn(testPlayer);
            when(playerService.getPlayersByRoom(testRoom)).thenReturn(List.of(testPlayer));

            Map<String, Object> result = gameStateService.getGameState(ROOM_CODE, PLAYER_ID);

            assertNotNull(result);
            // State should have transitioned
            assertEquals(GameState.ANSWER_REVEAL.ordinal(), result.get("state"));
        }

        @Test
        @DisplayName("lobby state has zero remaining time")
        void lobbyStateHasZeroRemainingTime() {
            testRoom.setCurrentState(GameState.LOBBY);

            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(testRoom);
            when(playerService.findById(PLAYER_ID)).thenReturn(testPlayer);
            when(playerService.getPlayersByRoom(testRoom)).thenReturn(List.of(testPlayer));

            Map<String, Object> result = gameStateService.getGameState(ROOM_CODE, PLAYER_ID);

            assertEquals(0, result.get("remainingTime"));
        }

        @Test
        @DisplayName("final state has zero remaining time")
        void finalStateHasZeroRemainingTime() {
            testRoom.setCurrentState(GameState.FINAL);
            testRoom.setStateStartTime(LocalDateTime.now());

            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(testRoom);
            when(playerService.findById(PLAYER_ID)).thenReturn(testPlayer);
            when(playerService.getPlayersByRoom(testRoom)).thenReturn(List.of(testPlayer));

            Map<String, Object> result = gameStateService.getGameState(ROOM_CODE, PLAYER_ID);

            assertEquals(0, result.get("remainingTime"));
        }
    }
}
