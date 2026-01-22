package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlayerService Tests")
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    private GameRoom testRoom;
    private Player testPlayer;
    private static final String ROOM_CODE = "ABC123";
    private static final String PLAYER_ID = "player-123";
    private static final String PLAYER_NAME = "TestPlayer";
    private static final Integer AVATAR_ID = 5;

    @BeforeEach
    void setUp() {
        testRoom = new GameRoom();
        testRoom.setRoomCode(ROOM_CODE);
        testRoom.setCurrentState(GameState.LOBBY);

        testPlayer = new Player();
        testPlayer.setId(PLAYER_ID);
        testPlayer.setName(PLAYER_NAME);
        testPlayer.setRoom(testRoom);
        testPlayer.setAvatarId(AVATAR_ID);
        testPlayer.setScore(0);
    }

    @Nested
    @DisplayName("createPlayer")
    class CreatePlayer {

        @Test
        @DisplayName("creates player with correct attributes")
        void createsPlayer_withCorrectAttributes() {
            Player result = playerService.createPlayer(testRoom, PLAYER_NAME, AVATAR_ID);

            assertNotNull(result);
            assertNotNull(result.getId());
            assertEquals(PLAYER_NAME, result.getName());
            assertEquals(testRoom, result.getRoom());
            assertEquals(AVATAR_ID, result.getAvatarId());
            assertEquals(0, result.getScore());
            assertEquals(0, result.getCorrectCount());
            assertEquals(0, result.getWrongCount());
            assertEquals(0, result.getTotalAnswerTime());
        }

        @Test
        @DisplayName("assigns random avatar when invalid avatarId provided")
        void assignsRandomAvatar_whenInvalidAvatarId() {
            Player result = playerService.createPlayer(testRoom, PLAYER_NAME, -1);

            assertNotNull(result);
            assertTrue(result.getAvatarId() >= 0);
        }

        @Test
        @DisplayName("assigns random avatar when null avatarId provided")
        void assignsRandomAvatar_whenNullAvatarId() {
            Player result = playerService.createPlayer(testRoom, PLAYER_NAME, null);

            assertNotNull(result);
            assertTrue(result.getAvatarId() >= 0);
        }
    }

    @Nested
    @DisplayName("joinRoom")
    class JoinRoom {

        @Test
        @DisplayName("creates and saves player when room in LOBBY state")
        void createsAndSavesPlayer_whenRoomInLobbyState() {
            when(playerRepository.save(any(Player.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Player result = playerService.joinRoom(testRoom, PLAYER_NAME, AVATAR_ID);

            assertNotNull(result);
            assertEquals(PLAYER_NAME, result.getName());
            assertEquals(testRoom, result.getRoom());
            verify(playerRepository).save(any(Player.class));
        }

        @Test
        @DisplayName("throws exception when room not in LOBBY state")
        void throwsException_whenRoomNotInLobbyState() {
            testRoom.setCurrentState(GameState.QUESTION);

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> playerService.joinRoom(testRoom, PLAYER_NAME, AVATAR_ID)
            );

            assertEquals("Cannot join room after game has started.", exception.getMessage());
            verify(playerRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws exception when room in COUNTDOWN state")
        void throwsException_whenRoomInCountdownState() {
            testRoom.setCurrentState(GameState.COUNTDOWN);

            assertThrows(
                    IllegalStateException.class,
                    () -> playerService.joinRoom(testRoom, PLAYER_NAME, AVATAR_ID)
            );
        }

        @Test
        @DisplayName("throws exception when room in FINAL state")
        void throwsException_whenRoomInFinalState() {
            testRoom.setCurrentState(GameState.FINAL);

            assertThrows(
                    IllegalStateException.class,
                    () -> playerService.joinRoom(testRoom, PLAYER_NAME, AVATAR_ID)
            );
        }
    }

    @Nested
    @DisplayName("leaveRoom")
    class LeaveRoom {

        @Test
        @DisplayName("deletes player when found in correct room")
        void deletesPlayer_whenFoundInCorrectRoom() {
            when(playerRepository.findById(PLAYER_ID)).thenReturn(Optional.of(testPlayer));

            boolean result = playerService.leaveRoom(ROOM_CODE, PLAYER_ID);

            assertTrue(result);
            verify(playerRepository).delete(testPlayer);
        }

        @Test
        @DisplayName("returns false when player not found")
        void returnsFalse_whenPlayerNotFound() {
            when(playerRepository.findById(PLAYER_ID)).thenReturn(Optional.empty());

            boolean result = playerService.leaveRoom(ROOM_CODE, PLAYER_ID);

            assertFalse(result);
            verify(playerRepository, never()).delete(any());
        }

        @Test
        @DisplayName("returns false when player in different room")
        void returnsFalse_whenPlayerInDifferentRoom() {
            GameRoom differentRoom = new GameRoom();
            differentRoom.setRoomCode("DIFFERENT");
            testPlayer.setRoom(differentRoom);
            when(playerRepository.findById(PLAYER_ID)).thenReturn(Optional.of(testPlayer));

            boolean result = playerService.leaveRoom(ROOM_CODE, PLAYER_ID);

            assertFalse(result);
            verify(playerRepository, never()).delete(any());
        }

        @Test
        @DisplayName("returns false when player has no room")
        void returnsFalse_whenPlayerHasNoRoom() {
            testPlayer.setRoom(null);
            when(playerRepository.findById(PLAYER_ID)).thenReturn(Optional.of(testPlayer));

            boolean result = playerService.leaveRoom(ROOM_CODE, PLAYER_ID);

            assertFalse(result);
            verify(playerRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("getPlayersByRoom")
    class GetPlayersByRoom {

        @Test
        @DisplayName("returns players for room")
        void returnsPlayersForRoom() {
            List<Player> players = List.of(testPlayer, new Player());
            when(playerRepository.findByRoom(testRoom)).thenReturn(players);

            List<Player> result = playerService.getPlayersByRoom(testRoom);

            assertEquals(2, result.size());
            verify(playerRepository).findByRoom(testRoom);
        }

        @Test
        @DisplayName("returns empty list when no players")
        void returnsEmptyList_whenNoPlayers() {
            when(playerRepository.findByRoom(testRoom)).thenReturn(List.of());

            List<Player> result = playerService.getPlayersByRoom(testRoom);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("returns player when found")
        void returnsPlayer_whenFound() {
            when(playerRepository.findById(PLAYER_ID)).thenReturn(Optional.of(testPlayer));

            Player result = playerService.findById(PLAYER_ID);

            assertNotNull(result);
            assertEquals(PLAYER_ID, result.getId());
        }

        @Test
        @DisplayName("returns null when not found")
        void returnsNull_whenNotFound() {
            when(playerRepository.findById(PLAYER_ID)).thenReturn(Optional.empty());

            Player result = playerService.findById(PLAYER_ID);

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("resetAnswersForRoom")
    class ResetAnswersForRoom {

        @Test
        @DisplayName("resets currentAnsweredQuestionIndex for all players")
        void resetsAnswerIndex_forAllPlayers() {
            Player player1 = new Player();
            player1.setCurrentAnsweredQuestionIndex(5);
            Player player2 = new Player();
            player2.setCurrentAnsweredQuestionIndex(3);

            when(playerRepository.findByRoom(testRoom)).thenReturn(List.of(player1, player2));

            playerService.resetAnswersForRoom(testRoom);

            assertNull(player1.getCurrentAnsweredQuestionIndex());
            assertNull(player2.getCurrentAnsweredQuestionIndex());
            verify(playerRepository, times(2)).save(any(Player.class));
        }

        @Test
        @DisplayName("handles empty player list")
        void handlesEmptyPlayerList() {
            when(playerRepository.findByRoom(testRoom)).thenReturn(List.of());

            playerService.resetAnswersForRoom(testRoom);

            verify(playerRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("saves and returns player")
        void savesAndReturnsPlayer() {
            when(playerRepository.save(testPlayer)).thenReturn(testPlayer);

            Player result = playerService.save(testPlayer);

            assertNotNull(result);
            assertEquals(PLAYER_ID, result.getId());
            verify(playerRepository).save(testPlayer);
        }
    }
}
