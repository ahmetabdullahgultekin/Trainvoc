package com.gultekinahmetabdullah.trainvoc.srs.algorithm

/**
 * Immutable FSRS memory state for a single card (design doc §5a).
 *
 * This is a pure value object with no Android dependencies, so it is fully
 * unit-testable on the JVM. It deliberately mirrors the persisted columns added
 * by Room migration V18 (`stability`, `difficulty`, `due`, `card_state`,
 * `elapsed_days`, `scheduled_days`, `reps`, `lapses`).
 *
 * Timestamps are epoch milliseconds (the client's `System.currentTimeMillis()`
 * clock) to keep the algorithm clock-injectable and deterministic in tests.
 *
 * @property stability     estimated days until retrievability drops to 90%.
 * @property difficulty    intrinsic hardness in the FSRS internal range 1.0–10.0.
 * @property due           epoch-ms of the next scheduled review.
 * @property state         lifecycle state.
 * @property lastReview    epoch-ms of the previous review, or null if never reviewed.
 * @property reps          total number of reviews.
 * @property lapses        number of times the card lapsed (rated Again while in REVIEW).
 * @property scheduledDays interval (in whole days) that was scheduled at the last review.
 */
data class FsrsCard(
    val stability: Double = 0.0,
    val difficulty: Double = 0.0,
    val due: Long = 0L,
    val state: FsrsState = FsrsState.NEW,
    val lastReview: Long? = null,
    val reps: Int = 0,
    val lapses: Int = 0,
    val scheduledDays: Long = 0L
) {
    companion object {
        /** A brand-new, never-reviewed card due immediately at [now]. */
        fun newCard(now: Long): FsrsCard = FsrsCard(state = FsrsState.NEW, due = now)
    }
}
