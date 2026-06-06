package com.gultekinahmetabdullah.trainvoc.srs.algorithm

import kotlin.math.max

/**
 * Best-effort seeding of FSRS state from the legacy SM-2 fields (design doc §5a).
 *
 * The existing `srs_cards` table carries SM-2 `interval`, `easeFactor`, and
 * `repetitions`. FSRS needs `stability`, `difficulty`, and a lifecycle `state`.
 * This is the Kotlin equivalent of the SQL seeding in Room migration V18 and is
 * the single place where the SM-2 → FSRS approximation lives, so it can be unit
 * tested against the documented mapping table:
 *
 * | SM-2 repetitions | FSRS state   |
 * |------------------|--------------|
 * | 0                | NEW          |
 * | 1–2              | LEARNING     |
 * | >= 3             | REVIEW       |
 *
 * Pure Kotlin, no Android dependencies.
 */
object Sm2ToFsrsMigrator {

    /**
     * @param intervalDays  SM-2 interval (days until next review); seeds stability.
     * @param easeFactor    SM-2 easiness factor (1.3–3.5); seeds difficulty inversely.
     * @param repetitions   SM-2 consecutive-correct count; seeds the lifecycle state.
     * @param nextReviewDate epoch-ms of the next scheduled review (becomes `due`).
     * @param lastReviewed  epoch-ms of the last review, if any.
     */
    fun seed(
        intervalDays: Int,
        easeFactor: Float,
        repetitions: Int,
        nextReviewDate: Long,
        lastReviewed: Long?
    ): FsrsCard {
        val stability = if (intervalDays > 0) {
            max(intervalDays.toDouble(), FsrsAlgorithm.MIN_STABILITY)
        } else {
            1.0
        }

        // SM-2 ease 1.3 (hardest) .. 3.5 (easiest) → FSRS difficulty 10 (hard) .. 1 (easy).
        val difficulty = easeToDifficulty(easeFactor)

        val state = stateFor(repetitions)

        return FsrsCard(
            stability = stability,
            difficulty = difficulty,
            due = nextReviewDate,
            state = state,
            lastReview = lastReviewed,
            reps = repetitions,
            lapses = 0,
            scheduledDays = max(0, intervalDays).toLong()
        )
    }

    /** Documented SM-2 repetitions → FSRS state mapping (design doc §5a). */
    fun stateFor(repetitions: Int): FsrsState = when {
        repetitions <= 0 -> FsrsState.NEW
        repetitions < 3 -> FsrsState.LEARNING
        else -> FsrsState.REVIEW
    }

    /** Maps SM-2 ease (1.3..3.5) to FSRS difficulty (10..1), clamped. */
    fun easeToDifficulty(easeFactor: Float): Double {
        val ease = easeFactor.toDouble().coerceIn(1.3, 3.5)
        val normalized = (ease - 1.3) / (3.5 - 1.3) // 0 (hard) .. 1 (easy)
        return (10.0 - normalized * 9.0).coerceIn(1.0, 10.0)
    }
}
