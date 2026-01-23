package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync.BatchSyncRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync.BatchSyncResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync.SyncRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync.SyncResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SyncService Tests")
class SyncServiceTest {

    @InjectMocks
    private SyncService syncService;

    private User testUser;
    private static final Long USER_ID = 1L;
    private static final String USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        testUser = new User(USERNAME, "test@example.com", "password");
        testUser.setId(USER_ID);
    }

    @Nested
    @DisplayName("processSingleSync")
    class ProcessSingleSync {

        @Test
        @DisplayName("successfully syncs word entity")
        void successfullySyncsWord() {
            SyncRequest request = new SyncRequest(
                "word",
                "word-123",
                Map.of(
                    "word", "hello",
                    "lastReviewed", 1234567890L,
                    "easinessFactor", 2.5f
                ),
                System.currentTimeMillis()
            );

            SyncResponse result = syncService.processSingleSync(request, testUser);

            assertTrue(result.success());
            assertEquals("word", result.entityType());
            assertEquals("word-123", result.entityId());
        }

        @Test
        @DisplayName("successfully syncs statistic entity")
        void successfullySyncsStatistic() {
            SyncRequest request = new SyncRequest(
                "statistic",
                "stat-123",
                Map.of(
                    "wordId", "word-123",
                    "correctCount", 10,
                    "wrongCount", 2
                ),
                System.currentTimeMillis()
            );

            SyncResponse result = syncService.processSingleSync(request, testUser);

            assertTrue(result.success());
            assertEquals("statistic", result.entityType());
            assertEquals("stat-123", result.entityId());
        }

        @Test
        @DisplayName("successfully syncs exam entity")
        void successfullySyncsExam() {
            SyncRequest request = new SyncRequest(
                "exam",
                "exam-123",
                Map.of(
                    "examId", "exam-123",
                    "score", 85,
                    "totalQuestions", 20
                ),
                System.currentTimeMillis()
            );

            SyncResponse result = syncService.processSingleSync(request, testUser);

            assertTrue(result.success());
            assertEquals("exam", result.entityType());
            assertEquals("exam-123", result.entityId());
        }

        @Test
        @DisplayName("successfully syncs achievement entity")
        void successfullySyncsAchievement() {
            SyncRequest request = new SyncRequest(
                "achievement",
                "ach-123",
                Map.of(
                    "achievementId", "first_word",
                    "progress", 100,
                    "isUnlocked", true
                ),
                System.currentTimeMillis()
            );

            SyncResponse result = syncService.processSingleSync(request, testUser);

            assertTrue(result.success());
            assertEquals("achievement", result.entityType());
            assertEquals("ach-123", result.entityId());
        }

        @Test
        @DisplayName("successfully syncs userProfile entity")
        void successfullySyncsUserProfile() {
            SyncRequest request = new SyncRequest(
                "userprofile",
                "profile-1",
                Map.of(
                    "displayName", "New Name",
                    "avatarId", 5,
                    "streak", 10
                ),
                System.currentTimeMillis()
            );

            SyncResponse result = syncService.processSingleSync(request, testUser);

            assertTrue(result.success());
            assertEquals("userprofile", result.entityType());
            assertEquals("profile-1", result.entityId());
            assertEquals("New Name", testUser.getDisplayName());
        }

        @Test
        @DisplayName("successfully syncs backup entity")
        void successfullySyncsBackup() {
            SyncRequest request = new SyncRequest(
                "backup",
                "backup-123",
                Map.of(
                    "backupId", "backup-123",
                    "size", 1024L,
                    "checksum", "abc123"
                ),
                System.currentTimeMillis()
            );

            SyncResponse result = syncService.processSingleSync(request, testUser);

            assertTrue(result.success());
            assertEquals("backup", result.entityType());
            assertEquals("backup-123", result.entityId());
        }

        @Test
        @DisplayName("successfully syncs featureFlag entity")
        void successfullySyncsFeatureFlag() {
            SyncRequest request = new SyncRequest(
                "featureflag",
                "flag-123",
                Map.of(
                    "flagName", "dark_mode",
                    "isEnabled", true
                ),
                System.currentTimeMillis()
            );

            SyncResponse result = syncService.processSingleSync(request, testUser);

            assertTrue(result.success());
            assertEquals("featureflag", result.entityType());
            assertEquals("flag-123", result.entityId());
        }

        @Test
        @DisplayName("returns failure for unknown entity type")
        void returnsFailure_forUnknownEntityType() {
            SyncRequest request = new SyncRequest(
                "unknown",
                "unknown-123",
                Map.of(),
                System.currentTimeMillis()
            );

            SyncResponse result = syncService.processSingleSync(request, testUser);

            assertFalse(result.success());
            assertEquals("unknown", result.entityType());
            assertEquals("unknown-123", result.entityId());
            assertTrue(result.errorMessage().contains("Unknown entity type"));
        }

        @Test
        @DisplayName("handles case-insensitive entity types")
        void handlesCaseInsensitiveEntityTypes() {
            SyncRequest request = new SyncRequest(
                "WORD",
                "word-123",
                Map.of("word", "hello"),
                System.currentTimeMillis()
            );

            SyncResponse result = syncService.processSingleSync(request, testUser);

            assertTrue(result.success());
            assertEquals("WORD", result.entityType());
        }
    }

    @Nested
    @DisplayName("processBatchSync")
    class ProcessBatchSync {

        @Test
        @DisplayName("successfully processes batch of sync requests")
        void successfullyProcessesBatch() {
            List<SyncRequest> items = List.of(
                new SyncRequest("word", "word-1", Map.of("word", "hello"), System.currentTimeMillis()),
                new SyncRequest("word", "word-2", Map.of("word", "world"), System.currentTimeMillis()),
                new SyncRequest("statistic", "stat-1", Map.of("correctCount", 5), System.currentTimeMillis())
            );
            BatchSyncRequest request = new BatchSyncRequest(items);

            BatchSyncResponse result = syncService.processBatchSync(request, testUser);

            assertEquals(3, result.totalCount());
            assertEquals(3, result.successCount());
            assertEquals(0, result.failureCount());
            assertTrue(result.results().stream().allMatch(SyncResponse::success));
        }

        @Test
        @DisplayName("reports failures in batch results")
        void reportsFailuresInBatchResults() {
            List<SyncRequest> items = List.of(
                new SyncRequest("word", "word-1", Map.of("word", "hello"), System.currentTimeMillis()),
                new SyncRequest("unknown", "unknown-1", Map.of(), System.currentTimeMillis()),
                new SyncRequest("statistic", "stat-1", Map.of("correctCount", 5), System.currentTimeMillis())
            );
            BatchSyncRequest request = new BatchSyncRequest(items);

            BatchSyncResponse result = syncService.processBatchSync(request, testUser);

            assertEquals(3, result.totalCount());
            assertEquals(2, result.successCount());
            assertEquals(1, result.failureCount());
        }

        @Test
        @DisplayName("handles empty batch")
        void handlesEmptyBatch() {
            BatchSyncRequest request = new BatchSyncRequest(List.of());

            BatchSyncResponse result = syncService.processBatchSync(request, testUser);

            assertEquals(0, result.totalCount());
            assertEquals(0, result.successCount());
            assertEquals(0, result.failureCount());
            assertTrue(result.results().isEmpty());
        }
    }

    @Nested
    @DisplayName("getServerChanges")
    class GetServerChanges {

        @Test
        @DisplayName("returns empty list for now (TODO implementation)")
        void returnsEmptyList() {
            List<Map<String, Object>> result = syncService.getServerChanges(USER_ID, System.currentTimeMillis());

            assertTrue(result.isEmpty());
        }
    }
}
