package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtAuthenticationFilter;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtTokenProvider;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.LeaderboardService;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeaderboardController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("LeaderboardController Tests")
class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeaderboardService leaderboardService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Player testPlayer1;
    private Player testPlayer2;

    @BeforeEach
    void setUp() {
        testPlayer1 = new Player();
        testPlayer1.setId("player-1");
        testPlayer1.setName("TopPlayer");
        testPlayer1.setScore(100);

        testPlayer2 = new Player();
        testPlayer2.setId("player-2");
        testPlayer2.setName("SecondPlayer");
        testPlayer2.setScore(80);
    }

    @Nested
    @DisplayName("GET /api/leaderboard")
    class GetLeaderboard {

        @Test
        @WithMockUser
        @DisplayName("returns leaderboard when room code is valid")
        void returnsLeaderboard_whenRoomCodeValid() throws Exception {
            when(leaderboardService.getLeaderboardByRoom("ABC12"))
                    .thenReturn(List.of(testPlayer1, testPlayer2));

            mockMvc.perform(get("/api/leaderboard")
                            .param("roomCode", "ABC12"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("TopPlayer"))
                    .andExpect(jsonPath("$[0].score").value(100))
                    .andExpect(jsonPath("$[1].name").value("SecondPlayer"));

            verify(leaderboardService).getLeaderboardByRoom("ABC12");
        }

        @Test
        @WithMockUser
        @DisplayName("returns error when room code is missing")
        void returnsError_whenRoomCodeMissing() throws Exception {
            mockMvc.perform(get("/api/leaderboard"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.error").value("Missing or empty parameter: roomCode"));

            verify(leaderboardService, never()).getLeaderboardByRoom(anyString());
        }

        @Test
        @WithMockUser
        @DisplayName("returns error when room code is empty")
        void returnsError_whenRoomCodeEmpty() throws Exception {
            mockMvc.perform(get("/api/leaderboard")
                            .param("roomCode", ""))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.error").value("Missing or empty parameter: roomCode"));
        }

        @Test
        @WithMockUser
        @DisplayName("returns error when no leaderboard found")
        void returnsError_whenNoLeaderboardFound() throws Exception {
            when(leaderboardService.getLeaderboardByRoom("EMPTY"))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/leaderboard")
                            .param("roomCode", "EMPTY"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.error").value("No leaderboard found for the given room code."));
        }

        @Test
        @WithMockUser
        @DisplayName("returns error when leaderboard is null")
        void returnsError_whenLeaderboardNull() throws Exception {
            when(leaderboardService.getLeaderboardByRoom("UNKNOWN"))
                    .thenReturn(null);

            mockMvc.perform(get("/api/leaderboard")
                            .param("roomCode", "UNKNOWN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.error").value("No leaderboard found for the given room code."));
        }

        @Test
        @WithMockUser
        @DisplayName("returns single player leaderboard")
        void returnsSinglePlayerLeaderboard() throws Exception {
            when(leaderboardService.getLeaderboardByRoom("SOLO"))
                    .thenReturn(List.of(testPlayer1));

            mockMvc.perform(get("/api/leaderboard")
                            .param("roomCode", "SOLO"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].name").value("TopPlayer"));
        }
    }
}
