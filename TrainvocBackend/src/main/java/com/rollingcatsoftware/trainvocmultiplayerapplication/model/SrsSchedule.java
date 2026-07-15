package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Server-side mirror of a single FSRS review-schedule row for one
 * {@code (wordId, userId)} pair.
 *
 * <p>This is the backend half of the SRS engine's S4 (cross-device sync) slice
 * described in {@code docs/design/srs-spaced-repetition-engine.md}. The Android
 * client is the source of truth; the server is a last-write-wins mirror keyed on
 * {@link #clientUpdatedAt}. The scheduling algorithm itself (FSRS-5) runs on the
 * client — this table only persists the resulting schedule so a user who
 * re-installs or signs in on a second device can restore their due queue.</p>
 *
 * <p>Lives in the primary ({@code trainvoc}) database because the schedule is
 * user-scoped (keyed by the authenticated user id), alongside
 * {@link UserWordProgress}. Schema is managed by Hibernate {@code ddl-auto}
 * (additive — a new table only; no existing table is touched).</p>
 */
@Entity
@Table(name = "srs_schedule", indexes = {
        @Index(name = "idx_srs_schedule_user_due", columnList = "user_id, due_at"),
        @Index(name = "idx_srs_schedule_user_id", columnList = "user_id")
})
@IdClass(SrsSchedule.SrsScheduleId.class)
public class SrsSchedule {

    /**
     * Permanent numeric v18 word id (logical FK to the words DB's {@code words.id}).
     * Re-keyed from the old String lemma in #96 PR-C. {@code userId} stays a String —
     * it holds the stringified primary-DB user id (see {@code SrsController}).
     */
    @Id
    @Column(name = "word_id", nullable = false)
    private Long wordId;

    @Id
    @Column(name = "user_id", nullable = false, length = 128)
    private String userId;

    @Column(name = "due_at", nullable = false)
    private Instant dueAt;

    @Column(name = "stability", nullable = false)
    private double stability = 1.0;

    @Column(name = "difficulty", nullable = false)
    private double difficulty = 0.3;

    @Column(name = "last_reviewed_at")
    private Instant lastReviewedAt;

    @Column(name = "card_state", nullable = false, length = 16)
    private String cardState = "NEW";

    /** Last-write-wins watermark set by the client. */
    @Column(name = "client_updated_at", nullable = false)
    private Instant clientUpdatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected SrsSchedule() {
        // JPA
    }

    public SrsSchedule(Long wordId, String userId) {
        this.wordId = wordId;
        this.userId = userId;
        this.createdAt = Instant.now();
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Instant getDueAt() {
        return dueAt;
    }

    public void setDueAt(Instant dueAt) {
        this.dueAt = dueAt;
    }

    public double getStability() {
        return stability;
    }

    public void setStability(double stability) {
        this.stability = stability;
    }

    public double getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(double difficulty) {
        this.difficulty = difficulty;
    }

    public Instant getLastReviewedAt() {
        return lastReviewedAt;
    }

    public void setLastReviewedAt(Instant lastReviewedAt) {
        this.lastReviewedAt = lastReviewedAt;
    }

    public String getCardState() {
        return cardState;
    }

    public void setCardState(String cardState) {
        this.cardState = cardState;
    }

    public Instant getClientUpdatedAt() {
        return clientUpdatedAt;
    }

    public void setClientUpdatedAt(Instant clientUpdatedAt) {
        this.clientUpdatedAt = clientUpdatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /** Composite primary key for {@link SrsSchedule} ({@code wordId} numeric since #96 PR-C). */
    public static class SrsScheduleId implements Serializable {
        private Long wordId;
        private String userId;

        public SrsScheduleId() {
        }

        public SrsScheduleId(Long wordId, String userId) {
            this.wordId = wordId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SrsScheduleId that = (SrsScheduleId) o;
            return Objects.equals(wordId, that.wordId) && Objects.equals(userId, that.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(wordId, userId);
        }
    }
}
