package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.GameRoomRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LeaderboardService Tests")
class LeaderboardServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameRoomRepository gameRoomRepository;

    @InjectMocks
    private LeaderboardService leaderboardService;

    private GameRoom testRoom;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private static final String ROOM_CODE = "ABC123";

    @BeforeEach
    void setUp() {
        testRoom = new GameRoom();
        testRoom.setRoomCode(ROOM_CODE);

        player1 = new Player();
        player1.setId("player-1");
        player1.setName("Alice");
        player1.setScore(100);
        player1.setRoom(testRoom);

        player2 = new Player();
        player2.setId("player-2");
        player2.setName("Bob");
        player2.setScore(250);
        player2.setRoom(testRoom);

        player3 = new Player();
        player3.setId("player-3");
        player3.setName("Charlie");
        player3.setScore(150);
        player3.setRoom(testRoom);

        player4 = new Player();
        player4.setId("player-4");
        player4.setName("Diana");
        player4.setScore(50);
        player4.setRoom(testRoom);
    }

    @Nested
    @DisplayName("getSortedPlayers")
    class GetSortedPlayers {

        @Test
        @DisplayName("returns empty list when room has null players")
        void returnsEmptyList_whenRoomHasNullPlayers() {
            testRoom.setPlayers(null);

            List<Player> result = leaderboardService.getSortedPlayers(testRoom);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty list when room has no players")
        void returnsEmptyList_whenRoomHasNoPlayers() {
            testRoom.setPlayers(new ArrayList<>());

            List<Player> result = leaderboardService.getSortedPlayers(testRoom);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns players sorted by score in descending order")
        void returnsPlayersSortedByScoreDescending() {
            testRoom.setPlayers(List.of(player1, player2, player3, player4));

            List<Player> result = leaderboardService.getSortedPlayers(testRoom);

            assertEquals(4, result.size());
            assertEquals("Bob", result.get(0).getName()); // 250 points
            assertEquals("Charlie", result.get(1).getName()); // 150 points
            assertEquals("Alice", result.get(2).getName()); // 100 points
            assertEquals("Diana", result.get(3).getName()); // 50 points
        }

        @Test
        @DisplayName("returns single player when room has one player")
        void returnsSinglePlayer_whenRoomHasOnePlayer() {
            testRoom.setPlayers(List.of(player1));

            List<Player> result = leaderboardService.getSortedPlayers(testRoom);

            assertEquals(1, result.size());
            assertEquals("Alice", result.get(0).getName());
        }
    }

    @Nested
    @DisplayName("getTop3Players")
    class GetTop3Players {

        @Test
        @DisplayName("returns top 3 players when room has more than 3 players")
        void returnsTop3Players_whenRoomHasMoreThan3Players() {
            testRoom.setPlayers(List.of(player1, player2, player3, player4));

            List<Player> result = leaderboardService.getTop3Players(testRoom);

            assertEquals(3, result.size());
            assertEquals("Bob", result.get(0).getName());
            assertEquals("Charlie", result.get(1).getName());
            assertEquals("Alice", result.get(2).getName());
        }

        @Test
        @DisplayName("returns all players when room has less than 3 players")
        void returnsAllPlayers_whenRoomHasLessThan3Players() {
            testRoom.setPlayers(List.of(player1, player2));

            List<Player> result = leaderboardService.getTop3Players(testRoom);

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("returns empty list when room has no players")
        void returnsEmptyList_whenRoomHasNoPlayers() {
            testRoom.setPlayers(new ArrayList<>());

            List<Player> result = leaderboardService.getTop3Players(testRoom);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getLeaderboard")
    class GetLeaderboard {

        @Test
        @DisplayName("returns all players sorted by score")
        void returnsAllPlayersSortedByScore() {
            when(playerRepository.findAll()).thenReturn(List.of(player1, player2, player3, player4));

            List<Player> result = leaderboardService.getLeaderboard();

            assertEquals(4, result.size());
            assertEquals("Bob", result.get(0).getName());
            assertEquals("Charlie", result.get(1).getName());
            assertEquals("Alice", result.get(2).getName());
            assertEquals("Diana", result.get(3).getName());
            verify(playerRepository).findAll();
        }

        @Test
        @DisplayName("returns empty list when no players exist")
        void returnsEmptyList_whenNoPlayersExist() {
            when(playerRepository.findAll()).thenReturn(List.of());

            List<Player> result = leaderboardService.getLeaderboard();

            assertTrue(result.isEmpty());
            verify(playerRepository).findAll();
        }
    }

    @Nested
    @DisplayName("getLeaderboardByRoom")
    class GetLeaderboardByRoom {

        @Test
        @DisplayName("returns sorted players when room exists")
        void returnsSortedPlayers_whenRoomExists() {
            testRoom.setPlayers(List.of(player1, player2, player3));
            when(gameRoomRepository.findById(ROOM_CODE)).thenReturn(Optional.of(testRoom));

            List<Player> result = leaderboardService.getLeaderboardByRoom(ROOM_CODE);

            assertEquals(3, result.size());
            assertEquals("Bob", result.get(0).getName());
            verify(gameRoomRepository).findById(ROOM_CODE);
        }

        @Test
        @DisplayName("returns empty list when room does not exist")
        void returnsEmptyList_whenRoomDoesNotExist() {
            when(gameRoomRepository.findById(ROOM_CODE)).thenReturn(Optional.empty());

            List<Player> result = leaderboardService.getLeaderboardByRoom(ROOM_CODE);

            assertTrue(result.isEmpty());
            verify(gameRoomRepository).findById(ROOM_CODE);
        }
    }
}
