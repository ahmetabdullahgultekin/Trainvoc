package com.gultekinahmetabdullah.trainvoc.ui.review

/**
 * A due card surfaced in the Review Queue: the FSRS schedule row joined with the
 * dictionary word it points at (design doc §4). The scheduler state itself lives
 * in `review_schedule`; this carries only what the flip-card UI shows.
 *
 * @property wordId  the scheduled word (`words.id`).
 * @property lemma   front of the card — the word being reviewed.
 * @property meaning denormalized display gloss (back of the card, always present).
 * @property senses  sense-grouped translations (back of the card, when available).
 */
data class ReviewCard(
    val wordId: Long,
    val lemma: String,
    val meaning: String,
    val senses: List<ReviewSense> = emptyList()
)

/** One sense of a review card: its ordinal, an optional usage note, and glosses. */
data class ReviewSense(
    val senseIndex: Int,
    val note: String?,
    val translations: List<String>
)

/**
 * Summary of a finished review session (design doc §4 step 8).
 *
 * @property reviewedCount cards rated this session.
 * @property recalledCount cards rated Hard/Good/Easy (i.e. not Again).
 */
data class ReviewSummary(
    val reviewedCount: Int,
    val recalledCount: Int
) {
    /** Retention rate for the session, 0..1 (design doc §11 R1 acceptance metric). */
    val retentionRate: Float
        get() = if (reviewedCount > 0) recalledCount.toFloat() / reviewedCount else 0f
}

/**
 * Review Queue screen state (design doc §7, `ReviewUiState`).
 *
 * A session snapshots the due queue once ([ReviewQueueViewModel.startSession]) and
 * walks it: [Active] per card (front → [Active.isRevealed] → rate), then [Summary];
 * [Empty] when nothing is due.
 */
sealed interface ReviewUiState {

    data object Loading : ReviewUiState

    data object Empty : ReviewUiState

    data class Active(
        val card: ReviewCard,
        val isRevealed: Boolean,
        /** Cards remaining in the session, including the one shown. */
        val remaining: Int,
        /** Cards already rated this session. */
        val reviewedCount: Int
    ) : ReviewUiState

    data class Summary(val summary: ReviewSummary) : ReviewUiState
}
