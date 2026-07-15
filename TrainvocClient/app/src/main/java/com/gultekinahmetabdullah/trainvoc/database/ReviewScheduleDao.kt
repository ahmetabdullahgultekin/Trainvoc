package com.gultekinahmetabdullah.trainvoc.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for the `review_schedule` table (SRS engine S1, design doc §7).
 *
 * All queries are scoped by `user_id` (default `local_user` for the offline-first
 * single user). "Due" means `due_at <= now`; callers pass the wall clock so the
 * queries stay deterministic and clock-injectable in tests.
 */
@Dao
interface ReviewScheduleDao {

    /**
     * Insert or update a schedule row. `@Upsert` updates the existing row in place
     * on a `(word_id, user_id)` conflict rather than delete-then-insert, so no
     * `ON DELETE CASCADE` is ever fired against the row.
     */
    @Upsert
    suspend fun upsert(row: ReviewScheduleRow)

    @Upsert
    suspend fun upsertAll(rows: List<ReviewScheduleRow>)

    /** Due cards for a review session, soonest first. */
    @Query(
        """
        SELECT * FROM review_schedule
        WHERE user_id = :userId AND due_at <= :now
        ORDER BY due_at ASC
        LIMIT :limit
        """
    )
    fun getDueFlow(
        now: Long,
        userId: String = ReviewScheduleRow.DEFAULT_USER_ID,
        limit: Int = 50
    ): Flow<List<ReviewScheduleRow>>

    /** Live due-count for the Home badge (design doc §4). */
    @Query("SELECT COUNT(*) FROM review_schedule WHERE user_id = :userId AND due_at <= :now")
    fun getDueCountFlow(
        now: Long,
        userId: String = ReviewScheduleRow.DEFAULT_USER_ID
    ): Flow<Int>

    /** One-shot due-count (test/worker friendly). */
    @Query("SELECT COUNT(*) FROM review_schedule WHERE user_id = :userId AND due_at <= :now")
    suspend fun getDueCount(
        now: Long,
        userId: String = ReviewScheduleRow.DEFAULT_USER_ID
    ): Int

    /** Epoch-ms of the next scheduled review, or null if nothing is scheduled. */
    @Query("SELECT MIN(due_at) FROM review_schedule WHERE user_id = :userId")
    suspend fun getNextDueAt(userId: String = ReviewScheduleRow.DEFAULT_USER_ID): Long?

    /** The schedule row for a single word, or null if it has never been scheduled. */
    @Query("SELECT * FROM review_schedule WHERE user_id = :userId AND word_id = :wordId LIMIT 1")
    suspend fun getByWord(
        wordId: Long,
        userId: String = ReviewScheduleRow.DEFAULT_USER_ID
    ): ReviewScheduleRow?

    /** Rows not yet pushed to the backend (the S4 sync queue). */
    @Query("SELECT * FROM review_schedule WHERE user_id = :userId AND synced_to_server = 0")
    suspend fun getDirtyRows(userId: String = ReviewScheduleRow.DEFAULT_USER_ID): List<ReviewScheduleRow>

    /** Mark rows synced after a successful S4 push. */
    @Query(
        """
        UPDATE review_schedule SET synced_to_server = 1
        WHERE user_id = :userId AND word_id IN (:wordIds)
        """
    )
    suspend fun markSynced(wordIds: List<Long>, userId: String = ReviewScheduleRow.DEFAULT_USER_ID)

    /** Total scheduled rows for a user (test/diagnostics). */
    @Query("SELECT COUNT(*) FROM review_schedule WHERE user_id = :userId")
    suspend fun count(userId: String = ReviewScheduleRow.DEFAULT_USER_ID): Int
}
