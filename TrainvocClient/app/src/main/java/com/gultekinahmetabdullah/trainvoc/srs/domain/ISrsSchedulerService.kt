package com.gultekinahmetabdullah.trainvoc.srs.domain

import com.gultekinahmetabdullah.trainvoc.database.ReviewScheduleRow
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsRating
import kotlinx.coroutines.flow.Flow

/**
 * Orchestrates the pure [com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsAlgorithm]
 * against the `review_schedule` Room store — the domain seam between the FSRS
 * math and persistence (design doc §7). It is the single place that decides
 * whether the SRS engine runs at all: every mutating call is a no-op unless the
 * `srs_engine_enabled` flag is on, so wiring this into the quiz flow (S3) changes
 * nothing while the flag is OFF (design doc §8).
 *
 * The `now` clock is a parameter (default = wall clock) so scheduling stays
 * deterministic and unit-testable, matching the algorithm's injectable clock.
 *
 * Extracted behind an interface for test injection and so the Review Queue (S2)
 * and the quiz hook (S3) share one rating/persistence implementation.
 */
interface ISrsSchedulerService {

    /** Whether the SRS engine flag is on. A missing flag reads as false. */
    suspend fun isEnabled(): Boolean

    /**
     * Quiz-outcome auto-schedule hook (design doc §4/§9 S3). Maps a binary quiz
     * result to an FSRS grade (correct → Good, incorrect → Again) and advances
     * the word's schedule, lazily creating a row on first encounter. No-op when
     * the flag is OFF, so the quiz behaves identically to today.
     */
    suspend fun onQuizAnswer(
        wordId: Long,
        wasCorrect: Boolean,
        now: Long = System.currentTimeMillis()
    )

    /**
     * Apply an explicit Review Queue [rating] to a word (design doc §4 step 5-6),
     * run FSRS, and persist. Returns the updated row, or null when the flag is OFF.
     */
    suspend fun rate(
        wordId: Long,
        rating: FsrsRating,
        now: Long = System.currentTimeMillis()
    ): ReviewScheduleRow?

    /** Stable, ordered snapshot of the cards due for a review session. */
    suspend fun dueQueue(
        now: Long = System.currentTimeMillis(),
        limit: Int = MAX_SESSION_CARDS
    ): List<ReviewScheduleRow>

    /** One-shot due count (for the Home badge one-shot load). */
    suspend fun dueCount(now: Long = System.currentTimeMillis()): Int

    /** Live due count as a Flow (design doc §7 service surface). */
    fun dueCountFlow(now: Long = System.currentTimeMillis()): Flow<Int>

    companion object {
        /** Cards loaded per review session (design doc §4 step 3). */
        const val MAX_SESSION_CARDS = 50
    }
}
