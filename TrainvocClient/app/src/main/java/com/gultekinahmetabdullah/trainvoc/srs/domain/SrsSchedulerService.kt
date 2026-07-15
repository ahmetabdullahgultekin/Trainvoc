package com.gultekinahmetabdullah.trainvoc.srs.domain

import android.util.Log
import com.gultekinahmetabdullah.trainvoc.database.ReviewScheduleDao
import com.gultekinahmetabdullah.trainvoc.database.ReviewScheduleRow
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlagManager
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsAlgorithm
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsCard
import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsRating
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default [ISrsSchedulerService]: FSRS math (pure [FsrsAlgorithm]) applied to the
 * durable [ReviewScheduleDao] store, gated by the `srs_engine_enabled` flag.
 *
 * Rating is the one shared write path for both the Review Queue (S2) and the
 * quiz hook (S3): load the current row (or a fresh [FsrsCard] on first sight),
 * [FsrsAlgorithm.schedule] it, and upsert. `created_at` is preserved across
 * updates; a new/updated row is written dirty so the S4 sync can push it.
 */
@Singleton
class SrsSchedulerService @Inject constructor(
    private val reviewScheduleDao: ReviewScheduleDao,
    private val fsrsAlgorithm: FsrsAlgorithm,
    private val featureFlags: FeatureFlagManager
) : ISrsSchedulerService {

    override suspend fun isEnabled(): Boolean = try {
        featureFlags.isEnabled(FeatureFlag.SRS_ENGINE)
    } catch (e: Exception) {
        // Null-safe fallback (design doc R5): treat any flag-read failure as OFF.
        Log.w(TAG, "SRS flag check failed; treating as disabled: ${e.message}")
        false
    }

    override suspend fun onQuizAnswer(wordId: Long, wasCorrect: Boolean, now: Long) {
        if (!isEnabled()) return
        schedule(wordId, FsrsRating.fromQuizCorrect(wasCorrect), now)
    }

    override suspend fun rate(wordId: Long, rating: FsrsRating, now: Long): ReviewScheduleRow? {
        if (!isEnabled()) return null
        return schedule(wordId, rating, now)
    }

    /** FSRS + persist. Callers gate on [isEnabled] before reaching here. */
    private suspend fun schedule(wordId: Long, rating: FsrsRating, now: Long): ReviewScheduleRow {
        val existing = reviewScheduleDao.getByWord(wordId)
        val card = existing?.toFsrsCard() ?: FsrsCard.newCard(now)
        val advanced = fsrsAlgorithm.schedule(card, rating, now)
        val row = ReviewScheduleRow.fromFsrsCard(
            wordId = wordId,
            card = advanced,
            now = now,
            createdAt = existing?.createdAt ?: now
        )
        reviewScheduleDao.upsert(row)
        return row
    }

    override suspend fun dueQueue(now: Long, limit: Int): List<ReviewScheduleRow> =
        reviewScheduleDao.getDue(now, limit = limit)

    override suspend fun dueCount(now: Long): Int = reviewScheduleDao.getDueCount(now)

    override fun dueCountFlow(now: Long): Flow<Int> = reviewScheduleDao.getDueCountFlow(now)

    private companion object {
        const val TAG = "SrsSchedulerService"
    }
}
