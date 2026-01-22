package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GameService Tests")
class GameServiceTest {

    @Mock
    private RoomService roomService;

    @Mock
    private PlayerService playerService;

    @Mock
    private RoomPasswordService roomPasswordService;

    @Mock
    private GameStateService gameStateService;

    @InjectMocks
    private GameService gameService;

    private GameRoom testRoom;
    private Player testPlayer;
    private QuizSettings testSettings;
    private static final String ROOM_CODE = "ABC123";
    private static final String PLAYER_ID = "player-123";
    private static final String PLAYER_NAME = "TestPlayer";
    private static final String HOST_NAME = "TestHost";
    private static final Integer AVATAR_ID = 1;
    private static final String HASHED_PASSWORD = "hashed-password";

    @BeforeEach
    void setUp() {
        testRoom = new GameRoom();
        testRoom.setRoomCode(ROOM_CODE);
        testRoom.setCurrentState(GameState.LOBBY);

        testPlayer = new Player();
        testPlayer.setId(PLAYER_ID);
        testPlayer.setName(PLAYER_NAME);
        testPlayer.setRoom(testRoom);

        testSettings = new QuizSettings();
        testSettings.setQuestionDuration(30);
        testSettings.setOptionCount(4);
        testSettings.setLevel("A1");
        testSettings.setTotalQuestionCount(10);
    }

    @Nested
    @DisplayName("Room Operations")
    class RoomOperations {

        @Test
        @DisplayName("createRoom delegates to roomService")
        void createRoom_delegatesToRoomService() {
            when(roomService.createRoom(HOST_NAME, AVATAR_ID, testSettings, true, HASHED_PASSWORD))
                    .thenReturn(testRoom);

            GameRoom result = gameService.createRoom(HOST_NAME, AVATAR_ID, testSettings, true, HASHED_PASSWORD);

            assertEquals(testRoom, result);
            verify(roomService).createRoom(HOST_NAME, AVATAR_ID, testSettings, true, HASHED_PASSWORD);
        }

        @Test
        @DisplayName("getRoom delegates to roomService")
        void getRoom_delegatesToRoomService() {
            when(roomService.getRoom(ROOM_CODE)).thenReturn(testRoom);

            GameRoom result = gameService.getRoom(ROOM_CODE);

            assertEquals(testRoom, result);
            verify(roomService).getRoom(ROOM_CODE);
        }

        @Test
        @DisplayName("saveRoom delegates to roomService")
        void saveRoom_delegatesToRoomService() {
            gameService.saveRoom(testRoom);

            verify(roomService).save(testRoom);
        }

        @Test
        @DisplayName("getAllRooms delegates to roomService")
        void getAllRooms_delegatesToRoomService() {
            List<GameRoom> rooms = List.of(testRoom);
            when(roomService.getAllRooms()).thenReturn(rooms);

            List<GameRoom> result = gameService.getAllRooms();

            assertEquals(rooms, result);
            verify(roomService).getAllRooms();
        }

        @Test
        @DisplayName("startRoom delegates to roomService")
        void startRoom_delegatesToRoomService() {
            when(roomService.startRoom(ROOM_CODE)).thenReturn(true);

            boolean result = gameService.startRoom(ROOM_CODE);

            assertTrue(result);
            verify(roomService).startRoom(ROOM_CODE);
        }

        @Test
        @DisplayName("disbandRoom delegates to roomService")
        void disbandRoom_delegatesToRoomService() {
            when(roomService.disbandRoom(ROOM_CODE)).thenReturn(true);

            boolean result = gameService.disbandRoom(ROOM_CODE);

            assertTrue(result);
            verify(roomService).disbandRoom(ROOM_CODE);
        }
    }

    @Nested
    @DisplayName("Player Operations")
    class PlayerOperations {

        @Test
        @DisplayName("joinRoom returns null when room not found")
        void joinRoom_returnsNull_whenRoomNotFound() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(null);

            Player result = gameService.joinRoom(ROOM_CODE, PLAYER_NAME, AVATAR_ID);

            assertNull(result);
            verify(playerService, never()).joinRoom(any(), any(), any());
        }

        @Test
        @DisplayName("joinRoom delegates to playerService when room found")
        void joinRoom_delegatesToPlayerService_whenRoomFound() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(testRoom);
            when(playerService.joinRoom(testRoom, PLAYER_NAME, AVATAR_ID)).thenReturn(testPlayer);

            Player result = gameService.joinRoom(ROOM_CODE, PLAYER_NAME, AVATAR_ID);

            assertEquals(testPlayer, result);
            verify(playerService).joinRoom(testRoom, PLAYER_NAME, AVATAR_ID);
        }

        @Test
        @DisplayName("leaveRoom delegates to playerService")
        void leaveRoom_delegatesToPlayerService() {
            when(playerService.leaveRoom(ROOM_CODE, PLAYER_ID)).thenReturn(true);

            boolean result = gameService.leaveRoom(ROOM_CODE, PLAYER_ID);

            assertTrue(result);
            verify(playerService).leaveRoom(ROOM_CODE, PLAYER_ID);
        }

        @Test
        @DisplayName("getPlayersByRoomCode returns null when room not found")
        void getPlayersByRoomCode_returnsNull_whenRoomNotFound() {
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(null);

            List<Player> result = gameService.getPlayersByRoomCode(ROOM_CODE);

            assertNull(result);
            verify(playerService, never()).getPlayersByRoom(any());
        }

        @Test
        @DisplayName("getPlayersByRoomCode delegates to playerService when room found")
        void getPlayersByRoomCode_delegatesToPlayerService_whenRoomFound() {
            List<Player> players = List.of(testPlayer);
            when(roomService.findByRoomCode(ROOM_CODE)).thenReturn(testRoom);
            when(playerService.getPlayersByRoom(testRoom)).thenReturn(players);

            List<Player> result = gameService.getPlayersByRoomCode(ROOM_CODE);

            assertEquals(players, result);
            verify(playerService).getPlayersByRoom(testRoom);
        }
    }

    @Nested
    @DisplayName("Password Operations")
    class PasswordOperations {

        @Test
        @DisplayName("checkRoomPassword delegates to roomPasswordService")
        void checkRoomPassword_delegatesToPasswordService() {
            when(roomPasswordService.checkPassword(ROOM_CODE, HASHED_PASSWORD)).thenReturn(true);

            boolean result = gameService.checkRoomPassword(ROOM_CODE, HASHED_PASSWORD);

            assertTrue(result);
            verify(roomPasswordService).checkPassword(ROOM_CODE, HASHED_PASSWORD);
        }

        @Test
        @DisplayName("throwIfRoomPasswordInvalid delegates to roomPasswordService")
        void throwIfRoomPasswordInvalid_delegatesToPasswordService() {
            gameService.throwIfRoomPasswordInvalid(ROOM_CODE, HASHED_PASSWORD);

            verify(roomPasswordService).validatePassword(ROOM_CODE, HASHED_PASSWORD);
        }
    }

    @Nested
    @DisplayName("Game State Operations")
    class GameStateOperations {

        @Test
        @DisplayName("getGameState delegates to gameStateService")
        void getGameState_delegatesToGameStateService() {
            Map<String, Object> state = Map.of("step", "QUESTION");
            when(gameStateService.getGameState(ROOM_CODE, PLAYER_ID)).thenReturn(state);

            Map<String, Object> result = gameService.getGameState(ROOM_CODE, PLAYER_ID);

            assertEquals(state, result);
            verify(gameStateService).getGameState(ROOM_CODE, PLAYER_ID);
        }

        @Test
        @DisplayName("getSimpleState delegates to gameStateService")
        void getSimpleState_delegatesToGameStateService() {
            Map<String, Object> state = Map.of("step", "LOBBY");
            when(gameStateService.getSimpleState(ROOM_CODE, PLAYER_ID)).thenReturn(state);

            Map<String, Object> result = gameService.getSimpleState(ROOM_CODE, PLAYER_ID);

            assertEquals(state, result);
            verify(gameStateService).getSimpleState(ROOM_CODE, PLAYER_ID);
        }

        @Test
        @DisplayName("goToNextQuestion delegates to gameStateService")
        void goToNextQuestion_delegatesToGameStateService() {
            when(gameStateService.goToNextQuestion(testRoom)).thenReturn(true);

            boolean result = gameService.goToNextQuestion(testRoom);

            assertTrue(result);
            verify(gameStateService).goToNextQuestion(testRoom);
        }
    }

    @Nested
    @DisplayName("Service Access")
    class ServiceAccess {

        @Test
        @DisplayName("getRoomService returns roomService")
        void getRoomService_returnsRoomService() {
            assertEquals(roomService, gameService.getRoomService());
        }

        @Test
        @DisplayName("getPlayerService returns playerService")
        void getPlayerService_returnsPlayerService() {
            assertEquals(playerService, gameService.getPlayerService());
        }

        @Test
        @DisplayName("getGameStateService returns gameStateService")
        void getGameStateService_returnsGameStateService() {
            assertEquals(gameStateService, gameService.getGameStateService());
        }

        @Test
        @DisplayName("getRoomPasswordService returns roomPasswordService")
        void getRoomPasswordService_returnsRoomPasswordService() {
            assertEquals(roomPasswordService, gameService.getRoomPasswordService());
        }
    }
}
