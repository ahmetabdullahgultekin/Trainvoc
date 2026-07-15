package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.srs.SrsReviewBatchRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.srs.SrsReviewItem;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.srs.SrsScheduleResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.SrsSchedule;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.SrsScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SrsService Tests")
class SrsServiceTest {

    private static final String USER = "42";

    @Mock
    private SrsScheduleRepository repository;

    @InjectMocks
    private SrsService srsService;

    /** wordIds are permanent numeric v18 ids since the #96 PR-C re-key. */
    private SrsReviewItem item(long wordId, long dueAt, String state, long clientUpdatedAt) {
        return new SrsReviewItem(wordId, dueAt, 8.43, 0.27, dueAt - 86_400_000L, state, clientUpdatedAt, null, null);
    }

    @Nested
    @DisplayName("upsertReviews")
    class UpsertReviews {

        @Test
        @DisplayName("inserts a new row when none exists for the (user, word)")
        void insertsNewRow() {
            when(repository.findByUserIdAndWordId(USER, 101L)).thenReturn(Optional.empty());

            SrsReviewBatchRequest req = new SrsReviewBatchRequest(
                    List.of(item(101L, 1_749_340_800_000L, "REVIEW", 1_749_254_400_000L)));

            int written = srsService.upsertReviews(USER, req);

            assertEquals(1, written);
            ArgumentCaptor<SrsSchedule> saved = ArgumentCaptor.forClass(SrsSchedule.class);
            verify(repository).save(saved.capture());
            SrsSchedule row = saved.getValue();
            assertEquals(101L, row.getWordId());
            assertEquals(USER, row.getUserId());
            assertEquals("REVIEW", row.getCardState());
            assertEquals(Instant.ofEpochMilli(1_749_340_800_000L), row.getDueAt());
            assertEquals(Instant.ofEpochMilli(1_749_254_400_000L), row.getClientUpdatedAt());
        }

        @Test
        @DisplayName("updates an existing row when the incoming clientUpdatedAt is newer")
        void updatesWhenNewer() {
            SrsSchedule existing = new SrsSchedule(101L, USER);
            existing.setClientUpdatedAt(Instant.ofEpochMilli(1_000L));
            existing.setCardState("LEARNING");
            existing.setDueAt(Instant.ofEpochMilli(1_000L));
            when(repository.findByUserIdAndWordId(USER, 101L)).thenReturn(Optional.of(existing));

            SrsReviewBatchRequest req = new SrsReviewBatchRequest(
                    List.of(item(101L, 2_000L, "REVIEW", 2_000L)));

            int written = srsService.upsertReviews(USER, req);

            assertEquals(1, written);
            verify(repository).save(existing);
            assertEquals("REVIEW", existing.getCardState());
            assertEquals(Instant.ofEpochMilli(2_000L), existing.getClientUpdatedAt());
        }

        @Test
        @DisplayName("skips a stale write (last-write-wins): older clientUpdatedAt is ignored")
        void skipsStaleWrite() {
            SrsSchedule existing = new SrsSchedule(101L, USER);
            existing.setClientUpdatedAt(Instant.ofEpochMilli(5_000L));
            existing.setCardState("REVIEW");
            existing.setDueAt(Instant.ofEpochMilli(5_000L));
            when(repository.findByUserIdAndWordId(USER, 101L)).thenReturn(Optional.of(existing));

            SrsReviewBatchRequest req = new SrsReviewBatchRequest(
                    List.of(item(101L, 1_000L, "LEARNING", 1_000L)));

            int written = srsService.upsertReviews(USER, req);

            assertEquals(0, written);
            verify(repository, never()).save(any());
            // Stored row is untouched.
            assertEquals("REVIEW", existing.getCardState());
            assertEquals(Instant.ofEpochMilli(5_000L), existing.getClientUpdatedAt());
        }

        @Test
        @DisplayName("equal clientUpdatedAt is treated as stale (strictly-newer wins)")
        void equalTimestampIsStale() {
            SrsSchedule existing = new SrsSchedule(101L, USER);
            existing.setClientUpdatedAt(Instant.ofEpochMilli(3_000L));
            existing.setDueAt(Instant.ofEpochMilli(3_000L));
            existing.setCardState("REVIEW");
            when(repository.findByUserIdAndWordId(USER, 101L)).thenReturn(Optional.of(existing));

            SrsReviewBatchRequest req = new SrsReviewBatchRequest(
                    List.of(item(101L, 9_000L, "RELEARNING", 3_000L)));

            int written = srsService.upsertReviews(USER, req);

            assertEquals(0, written);
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("processes a mixed batch and counts only the rows actually written")
        void mixedBatch() {
            // first word: new -> written
            when(repository.findByUserIdAndWordId(USER, 201L)).thenReturn(Optional.empty());
            // second word: stale -> skipped
            SrsSchedule beta = new SrsSchedule(202L, USER);
            beta.setClientUpdatedAt(Instant.ofEpochMilli(10_000L));
            beta.setDueAt(Instant.ofEpochMilli(10_000L));
            when(repository.findByUserIdAndWordId(USER, 202L)).thenReturn(Optional.of(beta));

            SrsReviewBatchRequest req = new SrsReviewBatchRequest(List.of(
                    item(201L, 1_000L, "NEW", 1_000L),
                    item(202L, 2_000L, "REVIEW", 2_000L)));

            int written = srsService.upsertReviews(USER, req);

            assertEquals(1, written);
            verify(repository, times(1)).save(any());
        }

        @Test
        @DisplayName("null lastReviewedAt is persisted as null")
        void nullLastReviewed() {
            when(repository.findByUserIdAndWordId(USER, 301L)).thenReturn(Optional.empty());
            SrsReviewItem withNull = new SrsReviewItem(
                    301L, 1_000L, 1.0, 0.3, null, "NEW", 1_000L, null, null);

            srsService.upsertReviews(USER, new SrsReviewBatchRequest(List.of(withNull)));

            ArgumentCaptor<SrsSchedule> saved = ArgumentCaptor.forClass(SrsSchedule.class);
            verify(repository).save(saved.capture());
            assertNull(saved.getValue().getLastReviewedAt());
        }
    }

    @Nested
    @DisplayName("getSchedule")
    class GetSchedule {

        @Test
        @DisplayName("maps rows to items and reports due count + nextDueAt")
        void returnsScheduleWithAggregates() {
            SrsSchedule a = new SrsSchedule(101L, USER);
            a.setDueAt(Instant.ofEpochMilli(1_000L));
            a.setClientUpdatedAt(Instant.ofEpochMilli(900L));
            a.setLastReviewedAt(Instant.ofEpochMilli(800L));
            a.setCardState("REVIEW");
            a.setStability(8.43);
            a.setDifficulty(0.27);

            when(repository.findByUserIdOrderByDueAtAsc(USER)).thenReturn(List.of(a));
            Instant now = Instant.ofEpochMilli(5_000L);
            when(repository.countDue(USER, now)).thenReturn(1L);
            when(repository.findNextDueAt(USER)).thenReturn(Instant.ofEpochMilli(1_000L));

            SrsScheduleResponse resp = srsService.getSchedule(USER, now);

            assertEquals(1, resp.schedule().size());
            SrsReviewItem mapped = resp.schedule().get(0);
            assertEquals(101L, mapped.wordId());
            assertEquals(1_000L, mapped.dueAt());
            assertEquals(800L, mapped.lastReviewedAt());
            assertEquals("REVIEW", mapped.cardState());
            // Server stores no natural key, so these round-trip as null.
            assertNull(mapped.lemma());
            assertNull(mapped.languageCode());
            assertEquals(1L, resp.totalDue());
            assertEquals(1_000L, resp.nextDueAt());
        }

        @Test
        @DisplayName("empty schedule reports null nextDueAt and zero due")
        void emptySchedule() {
            Instant now = Instant.ofEpochMilli(5_000L);
            when(repository.findByUserIdOrderByDueAtAsc(USER)).thenReturn(List.of());
            when(repository.countDue(eq(USER), any())).thenReturn(0L);
            when(repository.findNextDueAt(USER)).thenReturn(null);

            SrsScheduleResponse resp = srsService.getSchedule(USER, now);

            assertTrue(resp.schedule().isEmpty());
            assertEquals(0L, resp.totalDue());
            assertNull(resp.nextDueAt());
        }
    }
}
