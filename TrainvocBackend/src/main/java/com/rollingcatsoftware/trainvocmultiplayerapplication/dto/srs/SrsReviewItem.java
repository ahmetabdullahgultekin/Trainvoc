package com.rollingcatsoftware.trainvocmultiplayerapplication.dto.srs;

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
 * @see com.rollingcatsoftware.trainvocmultiplayerapplication.model.SrsSchedule
 */
public record SrsReviewItem(
        @NotBlank(message = "wordId must not be blank")
        String wordId,

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
        Long clientUpdatedAt
) {}
