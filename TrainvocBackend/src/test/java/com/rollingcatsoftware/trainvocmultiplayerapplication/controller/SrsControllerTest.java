package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.srs.SrsReviewItem;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.srs.SrsScheduleResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.CustomUserDetailsService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtAuthenticationFilter;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtTokenProvider;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.SrsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SrsController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("SrsController Tests")
class SrsControllerTest {

    private static final String BEARER = "Bearer good-token";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SrsService srsService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        // Happy-path auth resolution for an authenticated user with id 42.
        when(jwtTokenProvider.resolveToken(BEARER)).thenReturn("good-token");
        when(jwtTokenProvider.validateToken("good-token")).thenReturn(true);
        when(jwtTokenProvider.isUserToken("good-token")).thenReturn(true);
        when(jwtTokenProvider.getUserId("good-token")).thenReturn(42L);
        User user = new User();
        user.setId(42L);
        when(userDetailsService.loadUserById(42L)).thenReturn(user);
    }

    @Nested
    @DisplayName("POST /api/v1/srs/reviews")
    class PostReviews {

        @Test
        @DisplayName("returns 204 and forwards the batch to the service")
        void acceptsValidBatch() throws Exception {
            String body = """
                    {
                      "reviews": [
                        {
                          "wordId": "abandon",
                          "dueAt": 1749340800000,
                          "stability": 8.43,
                          "difficulty": 0.27,
                          "lastReviewedAt": 1749254400000,
                          "cardState": "REVIEW",
                          "clientUpdatedAt": 1749254400000
                        }
                      ]
                    }
                    """;

            mockMvc.perform(post("/api/v1/srs/reviews")
                            .header("Authorization", BEARER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNoContent());

            verify(srsService).upsertReviews(eq("42"), any());
        }

        @Test
        @DisplayName("returns 400 on an empty reviews list (validation)")
        void rejectsEmptyBatch() throws Exception {
            mockMvc.perform(post("/api/v1/srs/reviews")
                            .header("Authorization", BEARER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"reviews\": []}"))
                    .andExpect(status().isBadRequest());

            verify(srsService, never()).upsertReviews(any(), any());
        }

        @Test
        @DisplayName("returns 400 on an invalid cardState (validation)")
        void rejectsInvalidCardState() throws Exception {
            String body = """
                    {
                      "reviews": [
                        {
                          "wordId": "abandon",
                          "dueAt": 1749340800000,
                          "stability": 8.43,
                          "difficulty": 0.27,
                          "cardState": "BOGUS",
                          "clientUpdatedAt": 1749254400000
                        }
                      ]
                    }
                    """;

            mockMvc.perform(post("/api/v1/srs/reviews")
                            .header("Authorization", BEARER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());

            verify(srsService, never()).upsertReviews(any(), any());
        }

        @Test
        @DisplayName("returns 401 when the token is invalid")
        void rejectsBadToken() throws Exception {
            when(jwtTokenProvider.validateToken("good-token")).thenReturn(false);

            String body = """
                    {
                      "reviews": [
                        {
                          "wordId": "abandon",
                          "dueAt": 1749340800000,
                          "stability": 8.43,
                          "difficulty": 0.27,
                          "cardState": "REVIEW",
                          "clientUpdatedAt": 1749254400000
                        }
                      ]
                    }
                    """;

            mockMvc.perform(post("/api/v1/srs/reviews")
                            .header("Authorization", BEARER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnauthorized());

            verify(srsService, never()).upsertReviews(any(), any());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/srs/schedule")
    class GetSchedule {

        @Test
        @DisplayName("returns 200 with the user's schedule and aggregates")
        void returnsSchedule() throws Exception {
            SrsReviewItem item = new SrsReviewItem(
                    "abandon", 1749340800000L, 8.43, 0.27, 1749254400000L, "REVIEW", 1749254400000L);
            when(srsService.getSchedule(eq("42"), any()))
                    .thenReturn(new SrsScheduleResponse(List.of(item), 1L, 1749340800000L));

            mockMvc.perform(get("/api/v1/srs/schedule")
                            .header("Authorization", BEARER))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalDue").value(1))
                    .andExpect(jsonPath("$.nextDueAt").value(1749340800000L))
                    .andExpect(jsonPath("$.schedule[0].wordId").value("abandon"))
                    .andExpect(jsonPath("$.schedule[0].cardState").value("REVIEW"));
        }

        @Test
        @DisplayName("returns 401 when the token type is wrong")
        void rejectsNonUserToken() throws Exception {
            when(jwtTokenProvider.isUserToken("good-token")).thenReturn(false);

            mockMvc.perform(get("/api/v1/srs/schedule")
                            .header("Authorization", BEARER))
                    .andExpect(status().isUnauthorized());
        }
    }
}
