package com.rollingcatsoftware.trainvocmultiplayerapplication.dto.srs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * A single FSRS schedule row pushed by the client (one per {@code wordId}).
 *
 * <p>Timestamps are epoch milliseconds (matching the Android client's
 * {@code System.currentTimeMillis()} clock) so the contract is language-neutral.
 * The server stores them as {@code Instant}.</p>
 *
 * <p><strong>wordId is the permanent numeric v18 word id</strong> (#96 PR-C re-key). Only
 * shared-dictionary ids are cross-device syncable, so {@code wordId} is bounded to
 * {@code [1, 999_999]}: client custom-word ids ({@code >= 1_000_000}) are per-device
 * AUTOINCREMENT values, not globally unique, and are rejected here with a 400 (cross-device
 * sync of custom words is explicitly deferred).</p>
 *
 * <p>{@code lemma} and {@code languageCode} are OPTIONAL defensive natural-key hints carried
 * for future reconciliation per the #96 contract. They are accepted and bean-validated as
 * present-or-absent only — the server currently stores neither and echoes neither.</p>
 *
 * @see com.rollingcatsoftware.trainvocmultiplayerapplication.model.SrsSchedule
 */
public record SrsReviewItem(
        @NotNull(message = "wordId is required")
        @Min(value = 1, message = "wordId must be >= 1")
        @Max(value = 999_999,
                message = "wordId must be < 1000000; client custom-word ids (>= 1000000) are per-device "
                        + "and not cross-device syncable yet")
        Long wordId,

        @NotNull(message = "dueAt is required")
        @PositiveOrZero(message = "dueAt must be a positive epoch-ms timestamp")
        Long dueAt,

        @PositiveOrZero(message = "stability must be >= 0")
        double stability,

        double difficulty,

        Long lastReviewedAt,

        @NotBlank(message = "cardState is required")
        @Pattern(regexp = "NEW|LEARNING|REVIEW|RELEARNING",
                message = "cardState must be one of NEW, LEARNING, REVIEW, RELEARNING")
        String cardState,

        @NotNull(message = "clientUpdatedAt is required")
        @PositiveOrZero(message = "clientUpdatedAt must be a positive epoch-ms timestamp")
        Long clientUpdatedAt,

        // Optional defensive natural key (per the #96 contract). Accepted for forward
        // compatibility; not stored, not echoed. See the class javadoc.
        String lemma,

        String languageCode
) {}
