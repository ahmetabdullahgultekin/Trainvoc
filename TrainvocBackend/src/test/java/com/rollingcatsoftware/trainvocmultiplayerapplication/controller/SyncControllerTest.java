package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync.BatchSyncResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync.SyncResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.CustomUserDetailsService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtAuthenticationFilter;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtTokenProvider;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.SyncService;
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

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies that the typed sync endpoints enforce their entity type instead of trusting the
 * client-declared {@code entityType} (the #100 hardening), and that {@code /batch} — which
 * intentionally carries mixed types — is left unguarded.
 */
@WebMvcTest(SyncController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("SyncController Tests")
class SyncControllerTest {

    private static final String BEARER = "Bearer good-token";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SyncService syncService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        when(jwtTokenProvider.resolveToken(BEARER)).thenReturn("good-token");
        when(jwtTokenProvider.validateToken("good-token")).thenReturn(true);
        when(jwtTokenProvider.isUserToken("good-token")).thenReturn(true);
        when(jwtTokenProvider.getUserId("good-token")).thenReturn(42L);
        User user = new User();
        user.setId(42L);
        when(userDetailsService.loadUserById(42L)).thenReturn(user);
    }

    private static String body(String entityType, String entityId) {
        return """
                {
                  "entityType": "%s",
                  "entityId": "%s",
                  "data": {},
                  "timestamp": 1749254400000,
                  "action": "UPDATE"
                }
                """.formatted(entityType, entityId);
    }

    @Nested
    @DisplayName("POST /api/v1/sync/words")
    class SyncWords {

        @Test
        @DisplayName("matching entityType is forwarded to the service and succeeds")
        void matchingType_isProcessed() throws Exception {
            when(syncService.processSingleSync(any(), any()))
                    .thenReturn(SyncResponse.success("word", "123"));

            mockMvc.perform(post("/api/v1/sync/words")
                            .header("Authorization", BEARER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body("word", "123")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.entityType").value("word"));

            verify(syncService).processSingleSync(any(), any());
        }

        @Test
        @DisplayName("entityType match is case-insensitive")
        void matchingType_isCaseInsensitive() throws Exception {
            when(syncService.processSingleSync(any(), any()))
                    .thenReturn(SyncResponse.success("WORD", "123"));

            mockMvc.perform(post("/api/v1/sync/words")
                            .header("Authorization", BEARER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body("WORD", "123")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(syncService).processSingleSync(any(), any());
        }

        @Test
        @DisplayName("mismatched entityType is rejected per-item and never reaches the service")
        void mismatchedType_isRejected() throws Exception {
            mockMvc.perform(post("/api/v1/sync/words")
                            .header("Authorization", BEARER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body("statistic", "123")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(containsString("mismatch")))
                    .andExpect(jsonPath("$.entityType").value("statistic"))
                    .andExpect(jsonPath("$.entityId").value("123"));

            verify(syncService, never()).processSingleSync(any(), any());
        }

        @Test
        @DisplayName("an invalid token is rejected before the type check")
        void invalidToken_isUnauthorized() throws Exception {
            when(jwtTokenProvider.validateToken("good-token")).thenReturn(false);

            mockMvc.perform(post("/api/v1/sync/words")
                            .header("Authorization", BEARER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body("statistic", "123")))
                    .andExpect(status().isUnauthorized());

            verify(syncService, never()).processSingleSync(any(), any());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/sync/statistics")
    class SyncStatistics {

        @Test
        @DisplayName("matching entityType is forwarded to the service and succeeds")
        void matchingType_isProcessed() throws Exception {
            when(syncService.processSingleSync(any(), any()))
                    .thenReturn(SyncResponse.success("statistic", "123"));

            mockMvc.perform(post("/api/v1/sync/statistics")
                            .header("Authorization", BEARER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body("statistic", "123")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.entityType").value("statistic"));

            verify(syncService).processSingleSync(any(), any());
        }

        @Test
        @DisplayName("mismatched entityType is rejected per-item and never reaches the service")
        void mismatchedType_isRejected() throws Exception {
            mockMvc.perform(post("/api/v1/sync/statistics")
                            .header("Authorization", BEARER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body("word", "123")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(containsString("mismatch")))
                    .andExpect(jsonPath("$.entityType").value("word"));

            verify(syncService, never()).processSingleSync(any(), any());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/sync/batch")
    class BatchSync {

        @Test
        @DisplayName("carries mixed entity types straight to the batch service (no type guard)")
        void mixedTypes_areForwarded() throws Exception {
            when(syncService.processBatchSync(any(), any()))
                    .thenReturn(BatchSyncResponse.fromResults(List.of(
                            SyncResponse.success("word", "1"),
                            SyncResponse.success("statistic", "2"))));

            String batch = """
                    {
                      "items": [
                        {"entityType": "word", "entityId": "1", "data": {}, "timestamp": 1, "action": "UPDATE"},
                        {"entityType": "statistic", "entityId": "2", "data": {}, "timestamp": 1, "action": "UPDATE"}
                      ],
                      "clientTimestamp": 1749254400000,
                      "deviceId": "dev-1"
                    }
                    """;

            mockMvc.perform(post("/api/v1/sync/batch")
                            .header("Authorization", BEARER)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(batch))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalItems").value(2))
                    .andExpect(jsonPath("$.successCount").value(2));

            verify(syncService).processBatchSync(any(), any());
            verify(syncService, never()).processSingleSync(any(), any());
        }
    }
}
