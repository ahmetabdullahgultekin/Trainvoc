package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.mapper.GameMapper;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.GameRoomResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.PlayerResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameState;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizSettings;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.PlayerRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtAuthenticationFilter;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtTokenProvider;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("GameController Tests")
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameService gameService;

    @MockBean
    private PlayerRepository playerRepo;

    @MockBean
    private GameMapper gameMapper;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private GameRoom testRoom;
    private Player testPlayer;
    private QuizSettings testSettings;
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

        testSettings = new QuizSettings();
        testSettings.setQuestionDuration(60);
        testSettings.setOptionCount(4);
        testSettings.setLevel("A1");
        testSettings.setTotalQuestionCount(10);

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
    @DisplayName("POST /api/game/create")
    class CreateRoom {

        @Test
        @WithMockUser
        @DisplayName("creates room successfully")
        void createsRoom_successfully() throws Exception {
            when(gameService.createRoom(anyString(), any(), any(), anyBoolean(), any()))
                    .thenReturn(testRoom);
            when(gameMapper.toGameRoomResponse(any())).thenReturn(testRoomResponse);

            mockMvc.perform(post("/api/game/create")
                            .with(csrf())
                            .param("hostName", "TestHost")
                            .param("hostWantsToJoin", "true")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testSettings)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.roomCode").value("ABC12"));

            verify(gameService).createRoom(eq("TestHost"), isNull(), any(QuizSettings.class), eq(true), isNull());
        }

        @Test
        @WithMockUser
        @DisplayName("validates host name is required")
        void validatesHostName_isRequired() throws Exception {
            mockMvc.perform(post("/api/game/create")
                            .with(csrf())
                            .param("hostName", "")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testSettings)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("validates host name length")
        void validatesHostName_length() throws Exception {
            mockMvc.perform(post("/api/game/create")
                            .with(csrf())
                            .param("hostName", "A") // Too short
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testSettings)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/game/join")
    class JoinRoom {

        @Test
        @WithMockUser
        @DisplayName("joins room successfully")
        void joinsRoom_successfully() throws Exception {
            when(gameService.checkRoomPassword(anyString(), any())).thenReturn(true);
            when(gameService.joinRoom(anyString(), anyString(), any())).thenReturn(testPlayer);
            when(gameMapper.toPlayerResponse(any())).thenReturn(testPlayerResponse);

            mockMvc.perform(post("/api/game/join")
                            .with(csrf())
                            .param("roomCode", "ABC12")
                            .param("playerName", "TestPlayer"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("player-1"))
                    .andExpect(jsonPath("$.name").value("TestPlayer"));
        }

        @Test
        @WithMockUser
        @DisplayName("returns 403 when password is incorrect")
        void returns403_whenPasswordIncorrect() throws Exception {
            when(gameService.checkRoomPassword(anyString(), any())).thenReturn(false);

            mockMvc.perform(post("/api/game/join")
                            .with(csrf())
                            .param("roomCode", "ABC12")
                            .param("playerName", "TestPlayer")
                            .param("hashedPassword", "wrong"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser
        @DisplayName("returns 400 when room not found")
        void returns400_whenRoomNotFound() throws Exception {
            when(gameService.checkRoomPassword(anyString(), any())).thenReturn(true);
            when(gameService.joinRoom(anyString(), anyString(), any())).thenReturn(null);

            mockMvc.perform(post("/api/game/join")
                            .with(csrf())
                            .param("roomCode", "INVALID")
                            .param("playerName", "TestPlayer"))
                    .andExpect(status().isBadRequest());
        }
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
    @DisplayName("POST /api/game/rooms/{roomCode}/start")
    class StartRoom {

        @Test
        @WithMockUser
        @DisplayName("starts room successfully")
        void startsRoom_successfully() throws Exception {
            when(gameService.checkRoomPassword(anyString(), any())).thenReturn(true);
            when(gameService.startRoom("ABC12")).thenReturn(true);

            mockMvc.perform(post("/api/game/rooms/ABC12/start")
                            .with(csrf()))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        @DisplayName("returns 404 when room not found")
        void returns404_whenRoomNotFound() throws Exception {
            when(gameService.checkRoomPassword(anyString(), any())).thenReturn(true);
            when(gameService.startRoom("INVALID")).thenReturn(false);

            mockMvc.perform(post("/api/game/rooms/INVALID/start")
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("returns 403 when password incorrect")
        void returns403_whenPasswordIncorrect() throws Exception {
            when(gameService.checkRoomPassword(anyString(), any())).thenReturn(false);

            mockMvc.perform(post("/api/game/rooms/ABC12/start")
                            .with(csrf())
                            .param("hashedPassword", "wrong"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("POST /api/game/rooms/{roomCode}/disband")
    class DisbandRoom {

        @Test
        @WithMockUser
        @DisplayName("disbands room successfully")
        void disbandsRoom_successfully() throws Exception {
            when(gameService.checkRoomPassword(anyString(), any())).thenReturn(true);
            when(gameService.disbandRoom("ABC12")).thenReturn(true);

            mockMvc.perform(post("/api/game/rooms/ABC12/disband")
                            .with(csrf()))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        @DisplayName("returns 404 when room not found")
        void returns404_whenRoomNotFound() throws Exception {
            when(gameService.checkRoomPassword(anyString(), any())).thenReturn(true);
            when(gameService.disbandRoom("INVALID")).thenReturn(false);

            mockMvc.perform(post("/api/game/rooms/INVALID/disband")
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/game/rooms/{roomCode}/leave")
    class LeaveRoom {

        @Test
        @WithMockUser
        @DisplayName("leaves room successfully")
        void leavesRoom_successfully() throws Exception {
            when(gameService.leaveRoom("ABC12", "player-1")).thenReturn(true);

            mockMvc.perform(post("/api/game/rooms/ABC12/leave")
                            .with(csrf())
                            .param("playerId", "player-1"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        @DisplayName("returns 404 when player not found")
        void returns404_whenPlayerNotFound() throws Exception {
            when(gameService.leaveRoom(anyString(), anyString())).thenReturn(false);

            mockMvc.perform(post("/api/game/rooms/ABC12/leave")
                            .with(csrf())
                            .param("playerId", "invalid"))
                    .andExpect(status().isNotFound());
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
}
