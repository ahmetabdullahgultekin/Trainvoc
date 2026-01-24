package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.mapper.GameMapper;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.GameRoomResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.PlayerResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtAuthenticationFilter;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for GameController - Read-only REST endpoints.
 *
 * NOTE: Game operations (create, join, leave, start, answer) are handled via WebSocket.
 * This test class covers only the GET endpoints.
 */
@WebMvcTest(GameController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("GameController Tests")
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private GameMapper gameMapper;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private GameRoom testRoom;
    private Player testPlayer;
    private GameRoomResponse testRoomResponse;
    private PlayerResponse testPlayerResponse;

    @BeforeEach
    void setUp() {
        testRoom = new GameRoom();
        testRoom.setRoomCode("ABC12");
        testRoom.setStarted(false);
        testRoom.setCurrentState(GameState.LOBBY);
        testRoom.setPlayers(new ArrayList<>());
        testRoom.setHostId("host-1");
        testRoom.setQuestionDuration(60);
        testRoom.setOptionCount(4);
        testRoom.setLevel("A1");
        testRoom.setTotalQuestionCount(10);

        testPlayer = new Player();
        testPlayer.setId("player-1");
        testPlayer.setName("TestPlayer");
        testPlayer.setScore(0);
        testPlayer.setRoom(testRoom);

        testRoomResponse = GameRoomResponse.builder()
                .roomCode("ABC12")
                .started(false)
                .build();

        testPlayerResponse = PlayerResponse.builder()
                .id("player-1")
                .name("TestPlayer")
                .build();
    }

    @Nested
    @DisplayName("GET /api/game/{roomCode}")
    class GetRoom {

        @Test
        @WithMockUser
        @DisplayName("returns room when found")
        void returnsRoom_whenFound() throws Exception {
            when(gameService.getRoom("ABC12")).thenReturn(testRoom);
            when(gameMapper.toGameRoomResponse(any())).thenReturn(testRoomResponse);

            mockMvc.perform(get("/api/game/ABC12"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.roomCode").value("ABC12"));
        }

        @Test
        @WithMockUser
        @DisplayName("returns 404 when room not found")
        void returns404_whenRoomNotFound() throws Exception {
            when(gameService.getRoom("INVALID")).thenReturn(null);

            mockMvc.perform(get("/api/game/INVALID"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/game/rooms")
    class GetAllRooms {

        @Test
        @WithMockUser
        @DisplayName("returns all rooms")
        void returnsAllRooms() throws Exception {
            when(gameService.getAllRooms()).thenReturn(List.of(testRoom));
            when(gameMapper.toRoomListItemResponseList(any())).thenReturn(List.of());

            mockMvc.perform(get("/api/game/rooms"))
                    .andExpect(status().isOk());

            verify(gameService).getAllRooms();
        }
    }

    @Nested
    @DisplayName("GET /api/game/players")
    class GetPlayers {

        @Test
        @WithMockUser
        @DisplayName("returns players for room")
        void returnsPlayers_forRoom() throws Exception {
            when(gameService.getPlayersByRoomCode("ABC12")).thenReturn(List.of(testPlayer));
            when(gameMapper.toPlayerResponseList(any())).thenReturn(List.of(testPlayerResponse));

            mockMvc.perform(get("/api/game/players")
                            .param("roomCode", "ABC12"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        @DisplayName("returns 404 when room not found")
        void returns404_whenRoomNotFound() throws Exception {
            when(gameService.getPlayersByRoomCode("INVALID")).thenReturn(null);

            mockMvc.perform(get("/api/game/players")
                            .param("roomCode", "INVALID"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/game/state")
    class GetGameState {

        @Test
        @WithMockUser
        @DisplayName("returns game state")
        void returnsGameState() throws Exception {
            when(gameService.getGameState("ABC12", "player-1"))
                    .thenReturn(java.util.Map.of("step", "LOBBY"));

            mockMvc.perform(get("/api/game/state")
                            .param("roomCode", "ABC12")
                            .param("playerId", "player-1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.step").value("LOBBY"));
        }

        @Test
        @WithMockUser
        @DisplayName("returns 404 when not found")
        void returns404_whenNotFound() throws Exception {
            when(gameService.getGameState(anyString(), anyString())).thenReturn(null);

            mockMvc.perform(get("/api/game/state")
                            .param("roomCode", "INVALID")
                            .param("playerId", "player-1"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/game/state-simple")
    class GetSimpleState {

        @Test
        @WithMockUser
        @DisplayName("returns simple state")
        void returnsSimpleState() throws Exception {
            when(gameService.getSimpleState("ABC12", "player-1"))
                    .thenReturn(java.util.Map.of("state", "LOBBY"));

            mockMvc.perform(get("/api/game/state-simple")
                            .param("roomCode", "ABC12")
                            .param("playerId", "player-1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.state").value("LOBBY"));
        }

        @Test
        @WithMockUser
        @DisplayName("returns 404 when not found")
        void returns404_whenNotFound() throws Exception {
            when(gameService.getSimpleState(anyString(), anyString())).thenReturn(null);

            mockMvc.perform(get("/api/game/state-simple")
                            .param("roomCode", "INVALID")
                            .param("playerId", "player-1"))
                    .andExpect(status().isNotFound());
        }
    }
}
