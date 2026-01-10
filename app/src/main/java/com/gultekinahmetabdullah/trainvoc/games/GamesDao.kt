package com.gultekinahmetabdullah.trainvoc.games

import androidx.room.*
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import kotlinx.coroutines.flow.Flow

/**
 * DAO for all memory games data
 */
@Dao
interface GamesDao {

    // ========== Words for Games ==========

    /**
     * Get random words for games, filtered by level range
     */
    @Query("""
        SELECT * FROM words
        WHERE level >= :minLevel AND level <= :maxLevel
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun getWordsForGames(
        minLevel: String,
        maxLevel: String,
        limit: Int
    ): List<Word>

    // ========== Game Sessions ==========

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameSession(session: GameSession): Long

    @Update
    suspend fun updateGameSession(session: GameSession)

    @Query("SELECT * FROM game_sessions WHERE id = :sessionId")
    suspend fun getGameSession(sessionId: Long): GameSession?

    @Query("SELECT * FROM game_sessions WHERE user_id = :userId ORDER BY started_at DESC LIMIT 20")
    fun getRecentSessions(userId: String = "local_user"): Flow<List<GameSession>>

    @Query("SELECT * FROM game_sessions WHERE user_id = :userId AND game_type = :gameType ORDER BY started_at DESC LIMIT 10")
    fun getSessionsByType(userId: String = "local_user", gameType: String): Flow<List<GameSession>>

    @Query("""
        SELECT AVG(CAST(correct_answers AS FLOAT) / CAST(total_questions AS FLOAT) * 100)
        FROM game_sessions
        WHERE user_id = :userId AND game_type = :gameType AND completed_at IS NOT NULL
    """)
    suspend fun getAverageAccuracy(userId: String = "local_user", gameType: String): Float?

    @Query("SELECT SUM(correct_answers) FROM game_sessions WHERE user_id = :userId AND game_type = :gameType")
    suspend fun getTotalCorrectAnswers(userId: String = "local_user", gameType: String): Int?

    @Query("SELECT COUNT(*) FROM game_sessions WHERE user_id = :userId AND game_type = :gameType AND completed_at IS NOT NULL")
    suspend fun getCompletedGamesCount(userId: String = "local_user", gameType: String): Int

    @Query("SELECT COUNT(*) FROM game_sessions WHERE user_id = :userId AND completed_at IS NOT NULL")
    suspend fun getCompletedGamesCount(userId: String = "local_user"): Int

    @Query("SELECT * FROM game_sessions WHERE user_id = :userId ORDER BY score DESC LIMIT 1")
    suspend fun getHighestScoreSession(userId: String = "local_user"): GameSession?

    @Query("SELECT * FROM game_sessions WHERE user_id = :userId AND game_type = :gameType ORDER BY started_at DESC LIMIT :limit")
    suspend fun getGameSessions(userId: String = "local_user", gameType: String, limit: Int = 100): List<GameSession>

    @Query("SELECT * FROM game_sessions WHERE user_id = :userId ORDER BY started_at DESC LIMIT :limit")
    suspend fun getAllGameSessions(userId: String = "local_user", limit: Int = 1000): List<GameSession>

    // ========== Flip Cards Game Stats ==========

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlipCardStats(stats: FlipCardGameStats): Long

    @Query("SELECT * FROM flip_card_stats WHERE user_id = :userId ORDER BY completed_at DESC LIMIT 10")
    fun getFlipCardStats(userId: String = "local_user"): Flow<List<FlipCardGameStats>>

    @Query("SELECT MIN(moves) FROM flip_card_stats WHERE user_id = :userId AND grid_size = :gridSize AND completed = 1")
    suspend fun getBestMoves(userId: String = "local_user", gridSize: String): Int?

    @Query("SELECT MIN(time_seconds) FROM flip_card_stats WHERE user_id = :userId AND grid_size = :gridSize AND completed = 1")
    suspend fun getBestTime(userId: String = "local_user", gridSize: String): Int?

    // ========== SRS (Spaced Repetition) ==========

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSRSCard(card: SRSCard): Long

    @Update
    suspend fun updateSRSCard(card: SRSCard)

    @Query("SELECT * FROM srs_cards WHERE word_id = :wordId AND user_id = :userId LIMIT 1")
    suspend fun getSRSCard(wordId: Long, userId: String = "local_user"): SRSCard?

    @Query("""
        SELECT * FROM srs_cards
        WHERE user_id = :userId AND next_review_date <= :now
        ORDER BY next_review_date ASC
        LIMIT :limit
    """)
    suspend fun getDueCards(
        userId: String = "local_user",
        now: Long = System.currentTimeMillis(),
        limit: Int = 50
    ): List<SRSCard>

    @Query("SELECT COUNT(*) FROM srs_cards WHERE user_id = :userId AND next_review_date <= :now")
    suspend fun getDueCount(userId: String = "local_user", now: Long = System.currentTimeMillis()): Int

    @Query("SELECT COUNT(*) FROM srs_cards WHERE user_id = :userId AND next_review_date <= :now")
    fun getDueCountFlow(userId: String = "local_user", now: Long = System.currentTimeMillis()): Flow<Int>

    @Query("SELECT COUNT(*) FROM srs_cards WHERE user_id = :userId AND repetitions >= 5")
    suspend fun getMasteredCount(userId: String = "local_user"): Int

    @Query("DELETE FROM srs_cards WHERE user_id = :userId")
    suspend fun clearAllSRSCards(userId: String = "local_user")

    // ========== Speed Match Stats ==========

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeedMatchStats(stats: SpeedMatchStats): Long

    @Query("SELECT * FROM speed_match_stats WHERE user_id = :userId ORDER BY completed_at DESC LIMIT 10")
    fun getSpeedMatchStats(userId: String = "local_user"): Flow<List<SpeedMatchStats>>

    @Query("SELECT MIN(completion_time_ms) FROM speed_match_stats WHERE user_id = :userId AND pair_count = :pairCount AND completed = 1")
    suspend fun getBestSpeedTime(userId: String = "local_user", pairCount: Int): Long?

    @Query("SELECT MAX(score) FROM speed_match_stats WHERE user_id = :userId")
    suspend fun getHighestSpeedScore(userId: String = "local_user"): Int?
}

// ========== Flip Cards Game Stats Entity ==========

@Entity(tableName = "flip_card_stats")
data class FlipCardGameStats(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: String = "local_user",

    @ColumnInfo(name = "grid_size")
    val gridSize: String, // "4x3", "4x4", "6x4", etc.

    @ColumnInfo(name = "total_pairs")
    val totalPairs: Int,

    @ColumnInfo(name = "moves")
    val moves: Int,

    @ColumnInfo(name = "time_seconds")
    val timeSeconds: Int,

    @ColumnInfo(name = "completed")
    val completed: Boolean = false,

    @ColumnInfo(name = "completed_at")
    val completedAt: Long = System.currentTimeMillis()
)

// ========== SRS Card Entity ==========

@Entity(tableName = "srs_cards")
data class SRSCard(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "word_id")
    val wordId: Long,

    @ColumnInfo(name = "user_id")
    val userId: String = "local_user",

    // SM-2 Algorithm fields
    @ColumnInfo(name = "ease_factor")
    val easeFactor: Float = 2.5f, // Default 2.5

    @ColumnInfo(name = "interval")
    val interval: Int = 0, // Days until next review

    @ColumnInfo(name = "repetitions")
    val repetitions: Int = 0, // Number of successful reviews

    @ColumnInfo(name = "next_review_date")
    val nextReviewDate: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "last_reviewed")
    val lastReviewed: Long? = null,

    @ColumnInfo(name = "total_reviews")
    val totalReviews: Int = 0,

    @ColumnInfo(name = "correct_reviews")
    val correctReviews: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * SM-2 Algorithm: Calculate next review based on quality (0-5)
     * 0-2: Incorrect, restart
     * 3: Correct but difficult
     * 4: Correct
     * 5: Perfect
     */
    fun calculateNext(quality: Int): SRSCard {
        require(quality in 0..5) { "Quality must be between 0 and 5" }

        val newTotalReviews = totalReviews + 1
        val newCorrectReviews = if (quality >= 3) correctReviews + 1 else correctReviews

        // Quality < 3: restart
        if (quality < 3) {
            return copy(
                easeFactor = maxOf(1.3f, easeFactor - 0.2f),
                interval = 0,
                repetitions = 0,
                nextReviewDate = System.currentTimeMillis() + (10 * 60 * 1000), // 10 minutes
                lastReviewed = System.currentTimeMillis(),
                totalReviews = newTotalReviews,
                correctReviews = newCorrectReviews
            )
        }

        // Calculate new ease factor
        val newEaseFactor = easeFactor + (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))
        val clampedEaseFactor = maxOf(1.3f, newEaseFactor)

        // Calculate new interval
        val newInterval = when (repetitions) {
            0 -> 1 // First review: 1 day
            1 -> 6 // Second review: 6 days
            else -> (interval * clampedEaseFactor).toInt()
        }

        val nextReview = System.currentTimeMillis() + (newInterval * 24 * 60 * 60 * 1000L)

        return copy(
            easeFactor = clampedEaseFactor,
            interval = newInterval,
            repetitions = repetitions + 1,
            nextReviewDate = nextReview,
            lastReviewed = System.currentTimeMillis(),
            totalReviews = newTotalReviews,
            correctReviews = newCorrectReviews
        )
    }

    fun getAccuracy(): Float {
        if (totalReviews == 0) return 0f
        return (correctReviews.toFloat() / totalReviews.toFloat()) * 100f
    }

    fun isMastered(): Boolean = repetitions >= 5 && getAccuracy() >= 80f
}

// ========== Speed Match Stats Entity ==========

@Entity(tableName = "speed_match_stats")
data class SpeedMatchStats(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: String = "local_user",

    @ColumnInfo(name = "pair_count")
    val pairCount: Int,

    @ColumnInfo(name = "completion_time_ms")
    val completionTimeMs: Long,

    @ColumnInfo(name = "mistakes")
    val mistakes: Int,

    @ColumnInfo(name = "combo_max")
    val comboMax: Int,

    @ColumnInfo(name = "score")
    val score: Int,

    @ColumnInfo(name = "completed")
    val completed: Boolean = true,

    @ColumnInfo(name = "completed_at")
    val completedAt: Long = System.currentTimeMillis()
)
