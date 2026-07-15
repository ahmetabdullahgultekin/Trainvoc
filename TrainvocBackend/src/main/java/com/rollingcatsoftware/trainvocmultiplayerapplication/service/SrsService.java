package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.srs.SrsReviewBatchRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.srs.SrsReviewItem;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.srs.SrsScheduleResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.SrsSchedule;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.SrsScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Persists and serves the SRS review schedule for cross-device sync (S4).
 *
 * <p>Conflict resolution is <strong>last-write-wins on {@code clientUpdatedAt}</strong>:
 * an incoming row only overwrites the stored row when its {@code clientUpdatedAt}
 * is strictly newer. The client is always the source of truth; the server is a
 * mirror. This keeps offline-first semantics intact (see design doc §5c).</p>
 */
@Service
public class SrsService {

    private final SrsScheduleRepository repository;

    public SrsService(SrsScheduleRepository repository) {
        this.repository = repository;
    }

    /**
     * Upserts a batch of client schedule rows for the given user.
     *
     * @return the number of rows actually written (inserted or updated); rows
     * skipped due to a stale {@code clientUpdatedAt} are not counted.
     */
    @Transactional(transactionManager = "primaryTransactionManager")
    public int upsertReviews(String userId, SrsReviewBatchRequest request) {
        int written = 0;
        for (SrsReviewItem item : request.reviews()) {
            if (upsertOne(userId, item)) {
                written++;
            }
        }
        return written;
    }

    private boolean upsertOne(String userId, SrsReviewItem item) {
        Instant incomingClientUpdatedAt = Instant.ofEpochMilli(item.clientUpdatedAt());

        SrsSchedule row = repository.findByUserIdAndWordId(userId, item.wordId())
                .orElse(null);

        if (row == null) {
            row = new SrsSchedule(item.wordId(), userId);
        } else if (!incomingClientUpdatedAt.isAfter(row.getClientUpdatedAt())) {
            // Stale write — the stored row is newer or equal. Ignore (last-write-wins).
            return false;
        }

        row.setDueAt(Instant.ofEpochMilli(item.dueAt()));
        row.setStability(item.stability());
        row.setDifficulty(item.difficulty());
        row.setLastReviewedAt(item.lastReviewedAt() == null
                ? null : Instant.ofEpochMilli(item.lastReviewedAt()));
        row.setCardState(item.cardState());
        row.setClientUpdatedAt(incomingClientUpdatedAt);

        repository.save(row);
        return true;
    }

    /**
     * Returns the full schedule for the user plus aggregate due stats, used to
     * seed a freshly-installed client.
     */
    @Transactional(transactionManager = "primaryTransactionManager", readOnly = true)
    public SrsScheduleResponse getSchedule(String userId, Instant now) {
        List<SrsReviewItem> items = repository.findByUserIdOrderByDueAtAsc(userId).stream()
                .map(SrsService::toItem)
                .toList();
        long totalDue = repository.countDue(userId, now);
        Instant nextDueAt = repository.findNextDueAt(userId);
        return new SrsScheduleResponse(
                items,
                totalDue,
                nextDueAt == null ? null : nextDueAt.toEpochMilli()
        );
    }

    private static SrsReviewItem toItem(SrsSchedule s) {
        return new SrsReviewItem(
                s.getWordId(),
                s.getDueAt().toEpochMilli(),
                s.getStability(),
                s.getDifficulty(),
                s.getLastReviewedAt() == null ? null : s.getLastReviewedAt().toEpochMilli(),
                s.getCardState(),
                s.getClientUpdatedAt().toEpochMilli(),
                // lemma / languageCode: server stores no natural key, so nothing to echo.
                null,
                null
        );
    }
}
