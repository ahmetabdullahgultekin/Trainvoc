package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.GameRoomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoomService Tests")
class RoomServiceTest {

    @Mock
    private GameRoomRepository gameRoomRepository;

    @Mock
    private PlayerService playerService;

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private EntityManager writeEntityManager;

    @Mock
    private EntityManager readEntityManager;

    @Mock
    private EntityTransaction entityTransaction;

    @Mock
    private Query nativeInsert;

    @Mock
    private TypedQuery<GameRoom> roomQuery;

    @Mock
    private TypedQuery<GameRoom> refetchQuery;

    private RoomService roomService;

    private GameRoom testRoom;
    private QuizSettings testSettings;
    private static final String ROOM_CODE = "ABC123";
    private static final String HOST_NAME = "TestHost";
    private static final Integer AVATAR_ID = 1;

    @BeforeEach
    void setUp() {
        roomService = new RoomService(
                gameRoomRepository, playerService, transactionManager, entityManagerFactory);

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

    /** RoomService wraps the mocked manager in a TransactionTemplate; give it a status. */
    private void stubTransactionTemplate() {
        when(transactionManager.getTransaction(any())).thenReturn(new SimpleTransactionStatus());
    }

    /**
     * createRoom bypasses the repository on purpose (WebSocket threads need a
     * self-managed EntityManager, see RoomService): native INSERT, JPQL fetch,
     * persist host, merge room, then a second EntityManager re-fetches the room
     * with players. Stub that exact flow; both fetches return {@link #testRoom}.
     */
    private void stubCreateRoomEntityManagers() {
        when(entityManagerFactory.createEntityManager())
                .thenReturn(writeEntityManager, readEntityManager);
        when(writeEntityManager.getTransaction()).thenReturn(entityTransaction);
        when(writeEntityManager.createNativeQuery(anyString())).thenReturn(nativeInsert);
        when(nativeInsert.setParameter(anyInt(), any())).thenReturn(nativeInsert);
        when(nativeInsert.executeUpdate()).thenReturn(1);
        when(writeEntityManager.createQuery(anyString(), eq(GameRoom.class))).thenReturn(roomQuery);
        when(roomQuery.setParameter(anyString(), any())).thenReturn(roomQuery);
        when(roomQuery.getSingleResult()).thenReturn(testRoom);
        when(readEntityManager.createQuery(anyString(), eq(GameRoom.class))).thenReturn(refetchQuery);
        when(refetchQuery.setParameter(anyString(), any())).thenReturn(refetchQuery);
        when(refetchQuery.getSingleResult()).thenReturn(testRoom);
    }

    @Nested
    @DisplayName("createRoom")
    class CreateRoom {

        @Test
        @DisplayName("creates room with correct settings")
        void createsRoom_withCorrectSettings() {
            stubCreateRoomEntityManagers();

            GameRoom result = roomService.createRoom(HOST_NAME, AVATAR_ID, testSettings, true, null);

            assertNotNull(result);
            assertSame(testRoom, result);

            // The settings travel to the DB through the native INSERT's positional
            // parameters (1=roomCode ... 5=duration, 6=optionCount, 7=level, 8=total).
            ArgumentCaptor<Object> params = ArgumentCaptor.forClass(Object.class);
            verify(nativeInsert, times(10)).setParameter(anyInt(), params.capture());
            List<Object> values = params.getAllValues();
            assertTrue(values.get(0) instanceof String);
            assertEquals(5, ((String) values.get(0)).length());
            assertEquals(testSettings.getQuestionDuration(), values.get(4));
            assertEquals(testSettings.getOptionCount(), values.get(5));
            assertEquals(testSettings.getLevel(), values.get(6));
            assertEquals(testSettings.getTotalQuestionCount(), values.get(7));

            verify(entityTransaction).begin();
            verify(entityTransaction).commit();
            verify(writeEntityManager).close();
            verify(readEntityManager).close();
        }

        @Test
        @DisplayName("sets host when hostWantsToJoin is true")
        void setsHost_whenHostWantsToJoin() {
            stubCreateRoomEntityManagers();

            GameRoom result = roomService.createRoom(HOST_NAME, AVATAR_ID, testSettings, true, null);

            ArgumentCaptor<Player> hostCaptor = ArgumentCaptor.forClass(Player.class);
            verify(writeEntityManager).persist(hostCaptor.capture());
            Player host = hostCaptor.getValue();
            assertEquals(HOST_NAME, host.getName());
            assertEquals(AVATAR_ID, host.getAvatarId());

            assertEquals(host.getId(), result.getHostId());
            assertTrue(result.getPlayers().contains(host));
            verify(writeEntityManager).merge(testRoom);
        }

        @Test
        @DisplayName("sets hashed password when provided")
        void setsHashedPassword_whenProvided() {
            stubCreateRoomEntityManagers();
            String hashedPassword = "hashed-password-123";

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
            stubTransactionTemplate();
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
            stubTransactionTemplate();
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
            stubTransactionTemplate();
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
            stubTransactionTemplate();
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
            stubTransactionTemplate();
            when(gameRoomRepository.findByRoomCode(ROOM_CODE)).thenReturn(testRoom);

            boolean result = roomService.disbandRoom(ROOM_CODE);

            assertTrue(result);
            verify(gameRoomRepository).delete(testRoom);
        }

        @Test
        @DisplayName("returns false when room not found")
        void returnsFalse_whenRoomNotFound() {
            stubTransactionTemplate();
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
            stubTransactionTemplate();
            when(gameRoomRepository.save(testRoom)).thenReturn(testRoom);

            GameRoom result = roomService.save(testRoom);

            assertNotNull(result);
            assertEquals(ROOM_CODE, result.getRoomCode());
            verify(gameRoomRepository).save(testRoom);
        }
    }
}
