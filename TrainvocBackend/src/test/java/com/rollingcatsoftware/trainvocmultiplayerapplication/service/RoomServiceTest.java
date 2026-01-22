package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.GameRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoomService Tests")
class RoomServiceTest {

    @Mock
    private GameRoomRepository gameRoomRepository;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private RoomService roomService;

    private GameRoom testRoom;
    private QuizSettings testSettings;
    private static final String ROOM_CODE = "ABC123";
    private static final String HOST_NAME = "TestHost";
    private static final Integer AVATAR_ID = 1;

    @BeforeEach
    void setUp() {
        testRoom = new GameRoom();
        testRoom.setRoomCode(ROOM_CODE);
        testRoom.setStarted(false);
        testRoom.setCurrentState(GameState.LOBBY);
        testRoom.setPlayers(new ArrayList<>());

        testSettings = new QuizSettings();
        testSettings.setQuestionDuration(30);
        testSettings.setOptionCount(4);
        testSettings.setLevel("A1");
        testSettings.setTotalQuestionCount(10);
    }

    @Nested
    @DisplayName("createRoom")
    class CreateRoom {

        @Test
        @DisplayName("creates room with correct settings")
        void createsRoom_withCorrectSettings() {
            Player host = new Player();
            host.setId("host-id");
            host.setName(HOST_NAME);

            when(playerService.createPlayer(any(GameRoom.class), eq(HOST_NAME), eq(AVATAR_ID)))
                    .thenReturn(host);
            when(gameRoomRepository.save(any(GameRoom.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            GameRoom result = roomService.createRoom(HOST_NAME, AVATAR_ID, testSettings, true, null);

            assertNotNull(result);
            assertNotNull(result.getRoomCode());
            assertEquals(5, result.getRoomCode().length());
            assertEquals(testSettings.getQuestionDuration(), result.getQuestionDuration());
            assertEquals(testSettings.getOptionCount(), result.getOptionCount());
            assertEquals(testSettings.getLevel(), result.getLevel());
            assertEquals(testSettings.getTotalQuestionCount(), result.getTotalQuestionCount());
            assertFalse(result.getStarted());
        }

        @Test
        @DisplayName("sets host when hostWantsToJoin is true")
        void setsHost_whenHostWantsToJoin() {
            Player host = new Player();
            host.setId("host-id");
            host.setName(HOST_NAME);

            when(playerService.createPlayer(any(GameRoom.class), eq(HOST_NAME), eq(AVATAR_ID)))
                    .thenReturn(host);
            when(gameRoomRepository.save(any(GameRoom.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            GameRoom result = roomService.createRoom(HOST_NAME, AVATAR_ID, testSettings, true, null);

            assertEquals("host-id", result.getHostId());
            verify(playerService).save(host);
        }

        @Test
        @DisplayName("sets hashed password when provided")
        void setsHashedPassword_whenProvided() {
            String hashedPassword = "hashed-password-123";
            Player host = new Player();
            host.setId("host-id");

            when(playerService.createPlayer(any(GameRoom.class), eq(HOST_NAME), eq(AVATAR_ID)))
                    .thenReturn(host);
            when(gameRoomRepository.save(any(GameRoom.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            GameRoom result = roomService.createRoom(HOST_NAME, AVATAR_ID, testSettings, true, hashedPassword);

            assertEquals(hashedPassword, result.getHashedPassword());
        }
    }

    @Nested
    @DisplayName("getRoom")
    class GetRoom {

        @Test
        @DisplayName("returns room and updates lastUsed")
        void returnsRoom_andUpdatesLastUsed() {
            LocalDateTime before = LocalDateTime.now().minusMinutes(1);
            testRoom.setLastUsed(before);

            when(gameRoomRepository.findById(ROOM_CODE)).thenReturn(Optional.of(testRoom));
            when(gameRoomRepository.save(any(GameRoom.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            GameRoom result = roomService.getRoom(ROOM_CODE);

            assertNotNull(result);
            assertEquals(ROOM_CODE, result.getRoomCode());
            assertTrue(result.getLastUsed().isAfter(before));
            verify(gameRoomRepository).save(testRoom);
        }

        @Test
        @DisplayName("returns null when room not found")
        void returnsNull_whenRoomNotFound() {
            when(gameRoomRepository.findById(ROOM_CODE)).thenReturn(Optional.empty());

            GameRoom result = roomService.getRoom(ROOM_CODE);

            assertNull(result);
            verify(gameRoomRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("findByRoomCode")
    class FindByRoomCode {

        @Test
        @DisplayName("returns room without updating lastUsed")
        void returnsRoom_withoutUpdatingLastUsed() {
            when(gameRoomRepository.findByRoomCode(ROOM_CODE)).thenReturn(testRoom);

            GameRoom result = roomService.findByRoomCode(ROOM_CODE);

            assertNotNull(result);
            assertEquals(ROOM_CODE, result.getRoomCode());
            verify(gameRoomRepository, never()).save(any());
        }

        @Test
        @DisplayName("returns null when room not found")
        void returnsNull_whenRoomNotFound() {
            when(gameRoomRepository.findByRoomCode(ROOM_CODE)).thenReturn(null);

            GameRoom result = roomService.findByRoomCode(ROOM_CODE);

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("getAllRooms")
    class GetAllRooms {

        @Test
        @DisplayName("returns all rooms")
        void returnsAllRooms() {
            List<GameRoom> rooms = List.of(testRoom, new GameRoom());
            when(gameRoomRepository.findAll()).thenReturn(rooms);

            List<GameRoom> result = roomService.getAllRooms();

            assertEquals(2, result.size());
            verify(gameRoomRepository).findAll();
        }

        @Test
        @DisplayName("returns empty list when no rooms")
        void returnsEmptyList_whenNoRooms() {
            when(gameRoomRepository.findAll()).thenReturn(List.of());

            List<GameRoom> result = roomService.getAllRooms();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("startRoom")
    class StartRoom {

        @Test
        @DisplayName("starts room and sets state to COUNTDOWN")
        void startsRoom_andSetsStateToCountdown() {
            when(gameRoomRepository.findByRoomCode(ROOM_CODE)).thenReturn(testRoom);
            when(gameRoomRepository.save(any(GameRoom.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            boolean result = roomService.startRoom(ROOM_CODE);

            assertTrue(result);
            assertTrue(testRoom.getStarted());
            assertEquals(GameState.COUNTDOWN, testRoom.getCurrentState());
            assertNotNull(testRoom.getStateStartTime());
            verify(gameRoomRepository).save(testRoom);
        }

        @Test
        @DisplayName("returns false when room not found")
        void returnsFalse_whenRoomNotFound() {
            when(gameRoomRepository.findByRoomCode(ROOM_CODE)).thenReturn(null);

            boolean result = roomService.startRoom(ROOM_CODE);

            assertFalse(result);
            verify(gameRoomRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("disbandRoom")
    class DisbandRoom {

        @Test
        @DisplayName("deletes room when found")
        void deletesRoom_whenFound() {
            when(gameRoomRepository.findByRoomCode(ROOM_CODE)).thenReturn(testRoom);

            boolean result = roomService.disbandRoom(ROOM_CODE);

            assertTrue(result);
            verify(gameRoomRepository).delete(testRoom);
        }

        @Test
        @DisplayName("returns false when room not found")
        void returnsFalse_whenRoomNotFound() {
            when(gameRoomRepository.findByRoomCode(ROOM_CODE)).thenReturn(null);

            boolean result = roomService.disbandRoom(ROOM_CODE);

            assertFalse(result);
            verify(gameRoomRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("saves and returns room")
        void savesAndReturnsRoom() {
            when(gameRoomRepository.save(testRoom)).thenReturn(testRoom);

            GameRoom result = roomService.save(testRoom);

            assertNotNull(result);
            assertEquals(ROOM_CODE, result.getRoomCode());
            verify(gameRoomRepository).save(testRoom);
        }
    }
}
