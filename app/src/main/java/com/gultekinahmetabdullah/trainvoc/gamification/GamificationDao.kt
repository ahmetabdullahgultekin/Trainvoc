package com.gultekinahmetabdullah.trainvoc.gamification

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO for gamification features
 */
@Dao
interface GamificationDao {

    // ============ Streak Tracking ============

    @Query("SELECT * FROM streak_tracking WHERE user_id = :userId LIMIT 1")
    suspend fun getStreakTracking(userId: String = "local_user"): StreakTracking?

    @Query("SELECT * FROM streak_tracking WHERE user_id = :userId LIMIT 1")
    fun getStreakTrackingFlow(userId: String = "local_user"): Flow<StreakTracking?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreakTracking(streak: StreakTracking)

    @Update
    suspend fun updateStreakTracking(streak: StreakTracking)

    @Query("UPDATE streak_tracking SET current_streak = :streak, longest_streak = :longestStreak, last_activity_date = :date WHERE user_id = :userId")
    suspend fun updateStreak(
        userId: String = "local_user",
        streak: Int,
        longestStreak: Int,
        date: Long = System.currentTimeMillis()
    )

    @Query("UPDATE streak_tracking SET current_streak = 0, streak_start_date = NULL WHERE user_id = :userId")
    suspend fun breakStreak(userId: String = "local_user")

    @Query("UPDATE streak_tracking SET streak_freeze_count = streak_freeze_count + 1 WHERE user_id = :userId")
    suspend fun useStreakFreeze(userId: String = "local_user")

    @Query("SELECT current_streak FROM streak_tracking WHERE user_id = :userId")
    suspend fun getCurrentStreak(userId: String = "local_user"): Int?

    @Query("SELECT longest_streak FROM streak_tracking WHERE user_id = :userId")
    suspend fun getLongestStreak(userId: String = "local_user"): Int?

    // ============ Daily Goals ============

    @Query("SELECT * FROM daily_goals WHERE user_id = :userId LIMIT 1")
    suspend fun getDailyGoal(userId: String = "local_user"): DailyGoal?

    @Query("SELECT * FROM daily_goals WHERE user_id = :userId LIMIT 1")
    fun getDailyGoalFlow(userId: String = "local_user"): Flow<DailyGoal?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyGoal(goal: DailyGoal)

    @Update
    suspend fun updateDailyGoal(goal: DailyGoal)

    @Query("""
        UPDATE daily_goals
        SET words_goal = :wordsGoal,
            reviews_goal = :reviewsGoal,
            quizzes_goal = :quizzesGoal,
            time_goal_minutes = :timeGoalMinutes,
            updated_at = :timestamp
        WHERE user_id = :userId
    """)
    suspend fun updateGoalTargets(
        userId: String = "local_user",
        wordsGoal: Int,
        reviewsGoal: Int,
        quizzesGoal: Int,
        timeGoalMinutes: Int,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("""
        UPDATE daily_goals
        SET words_today = :words,
            reviews_today = :reviews,
            quizzes_today = :quizzes,
            time_today_minutes = :time,
            updated_at = :timestamp
        WHERE user_id = :userId
    """)
    suspend fun updateDailyProgress(
        userId: String = "local_user",
        words: Int,
        reviews: Int,
        quizzes: Int,
        time: Int,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("UPDATE daily_goals SET words_today = words_today + 1 WHERE user_id = :userId")
    suspend fun incrementWordsToday(userId: String = "local_user")

    @Query("UPDATE daily_goals SET reviews_today = reviews_today + 1 WHERE user_id = :userId")
    suspend fun incrementReviewsToday(userId: String = "local_user")

    @Query("UPDATE daily_goals SET quizzes_today = quizzes_today + 1 WHERE user_id = :userId")
    suspend fun incrementQuizzesToday(userId: String = "local_user")

    @Query("UPDATE daily_goals SET time_today_minutes = time_today_minutes + :minutes WHERE user_id = :userId")
    suspend fun addTimeToday(userId: String = "local_user", minutes: Int)

    @Query("""
        UPDATE daily_goals
        SET words_today = 0,
            reviews_today = 0,
            quizzes_today = 0,
            time_today_minutes = 0,
            last_reset_date = :resetDate,
            updated_at = :timestamp
        WHERE user_id = :userId
    """)
    suspend fun resetDailyProgress(
        userId: String = "local_user",
        resetDate: Long = System.currentTimeMillis(),
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("UPDATE daily_goals SET goals_completed_total = goals_completed_total + 1 WHERE user_id = :userId")
    suspend fun incrementGoalsCompleted(userId: String = "local_user")

    // ============ Achievements ============

    @Query("SELECT * FROM user_achievements WHERE user_id = :userId")
    suspend fun getAllAchievements(userId: String = "local_user"): List<UserAchievement>

    @Query("SELECT * FROM user_achievements WHERE user_id = :userId")
    fun getAllAchievementsFlow(userId: String = "local_user"): Flow<List<UserAchievement>>

    @Query("SELECT * FROM user_achievements WHERE user_id = :userId AND is_unlocked = 1")
    suspend fun getUnlockedAchievements(userId: String = "local_user"): List<UserAchievement>

    @Query("SELECT * FROM user_achievements WHERE user_id = :userId AND is_unlocked = 1")
    fun getUnlockedAchievementsFlow(userId: String = "local_user"): Flow<List<UserAchievement>>

    @Query("SELECT * FROM user_achievements WHERE user_id = :userId AND achievement_id = :achievementId LIMIT 1")
    suspend fun getAchievement(userId: String = "local_user", achievementId: String): UserAchievement?

    @Query("SELECT * FROM user_achievements WHERE user_id = :userId AND is_unlocked = 1 AND notified = 0")
    suspend fun getUnnotifiedAchievements(userId: String = "local_user"): List<UserAchievement>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: UserAchievement): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<UserAchievement>)

    @Update
    suspend fun updateAchievement(achievement: UserAchievement)

    @Query("""
        UPDATE user_achievements
        SET progress = :progress,
            is_unlocked = :isUnlocked,
            unlocked_at = :unlockedAt
        WHERE user_id = :userId AND achievement_id = :achievementId
    """)
    suspend fun updateAchievementProgress(
        userId: String = "local_user",
        achievementId: String,
        progress: Int,
        isUnlocked: Boolean,
        unlockedAt: Long? = null
    )

    @Query("UPDATE user_achievements SET notified = 1 WHERE user_id = :userId AND achievement_id = :achievementId")
    suspend fun markAchievementNotified(userId: String = "local_user", achievementId: String)

    @Query("SELECT COUNT(*) FROM user_achievements WHERE user_id = :userId AND is_unlocked = 1")
    suspend fun getUnlockedAchievementCount(userId: String = "local_user"): Int

    @Query("SELECT COUNT(*) FROM user_achievements WHERE user_id = :userId AND is_unlocked = 1")
    fun getUnlockedAchievementCountFlow(userId: String = "local_user"): Flow<Int>

    // ============ Statistics ============

    @Query("SELECT total_active_days FROM streak_tracking WHERE user_id = :userId")
    suspend fun getTotalActiveDays(userId: String = "local_user"): Int?

    @Query("SELECT goals_completed_total FROM daily_goals WHERE user_id = :userId")
    suspend fun getTotalGoalsCompleted(userId: String = "local_user"): Int?
}
