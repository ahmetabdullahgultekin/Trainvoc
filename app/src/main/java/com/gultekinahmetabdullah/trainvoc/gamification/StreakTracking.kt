package com.gultekinahmetabdullah.trainvoc.gamification

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

/**
 * Streak tracking entity
 * Tracks consecutive days of learning activity
 */
@Entity(tableName = "streak_tracking")
data class StreakTracking(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String = "local_user",

    @ColumnInfo(name = "current_streak")
    val currentStreak: Int = 0,

    @ColumnInfo(name = "longest_streak")
    val longestStreak: Int = 0,

    @ColumnInfo(name = "last_activity_date")
    val lastActivityDate: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "streak_freeze_count")
    val streakFreezeCount: Int = 0, // Premium feature

    @ColumnInfo(name = "total_active_days")
    val totalActiveDays: Int = 0,

    @ColumnInfo(name = "streak_start_date")
    val streakStartDate: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Check if activity happened today
     */
    fun isActiveToday(): Boolean {
        val today = LocalDate.now()
        val lastActivity = LocalDate.ofEpochDay(
            TimeUnit.MILLISECONDS.toDays(lastActivityDate)
        )
        return today == lastActivity
    }

    /**
     * Check if streak is still valid (yesterday or today)
     */
    fun isStreakValid(): Boolean {
        val today = LocalDate.now()
        val lastActivity = LocalDate.ofEpochDay(
            TimeUnit.MILLISECONDS.toDays(lastActivityDate)
        )

        val daysDiff = java.time.temporal.ChronoUnit.DAYS.between(lastActivity, today)

        return when {
            daysDiff == 0L -> true // Today
            daysDiff == 1L -> true // Yesterday (can extend today)
            else -> false // Streak broken
        }
    }

    /**
     * Check if user can extend streak today
     */
    fun canExtendStreak(): Boolean {
        return !isActiveToday() && (isStreakValid() || currentStreak == 0)
    }

    /**
     * Get days until streak breaks (0 = must practice today, 1 = can skip today)
     */
    fun daysUntilBreak(): Int {
        if (isActiveToday()) return 1 // Safe for today

        val today = LocalDate.now()
        val lastActivity = LocalDate.ofEpochDay(
            TimeUnit.MILLISECONDS.toDays(lastActivityDate)
        )
        val daysDiff = java.time.temporal.ChronoUnit.DAYS.between(lastActivity, today)

        return when {
            daysDiff == 0L -> 1 // Already practiced today
            daysDiff == 1L -> 0 // Must practice today
            else -> -1 // Already broken
        }
    }

    /**
     * Get streak status message
     */
    fun getStatusMessage(): String {
        return when {
            currentStreak == 0 -> "Start your streak today!"
            isActiveToday() -> "🔥 Streak safe for today!"
            daysUntilBreak() == 0 -> "⚠️ Practice today to keep your ${currentStreak}-day streak!"
            else -> "💀 Streak broken! Start a new one!"
        }
    }

    companion object {
        /**
         * Create initial streak tracking
         */
        fun initial(): StreakTracking {
            return StreakTracking(
                currentStreak = 0,
                longestStreak = 0,
                lastActivityDate = System.currentTimeMillis(),
                totalActiveDays = 0
            )
        }
    }
}

/**
 * Daily goal entity
 * Tracks user's daily learning goals
 */
@Entity(tableName = "daily_goals")
data class DailyGoal(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String = "local_user",

    @ColumnInfo(name = "words_goal")
    val wordsGoal: Int = 10, // Learn 10 new words per day

    @ColumnInfo(name = "reviews_goal")
    val reviewsGoal: Int = 20, // Review 20 words per day

    @ColumnInfo(name = "quizzes_goal")
    val quizzesGoal: Int = 5, // Complete 5 quizzes per day

    @ColumnInfo(name = "time_goal_minutes")
    val timeGoalMinutes: Int = 15, // 15 minutes per day

    @ColumnInfo(name = "words_today")
    val wordsToday: Int = 0,

    @ColumnInfo(name = "reviews_today")
    val reviewsToday: Int = 0,

    @ColumnInfo(name = "quizzes_today")
    val quizzesToday: Int = 0,

    @ColumnInfo(name = "time_today_minutes")
    val timeTodayMinutes: Int = 0,

    @ColumnInfo(name = "last_reset_date")
    val lastResetDate: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "goals_completed_total")
    val goalsCompletedTotal: Int = 0,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Check if words goal is met
     */
    fun isWordsGoalMet(): Boolean = wordsToday >= wordsGoal

    /**
     * Check if reviews goal is met
     */
    fun isReviewsGoalMet(): Boolean = reviewsToday >= reviewsGoal

    /**
     * Check if quizzes goal is met
     */
    fun isQuizzesGoalMet(): Boolean = quizzesToday >= quizzesGoal

    /**
     * Check if time goal is met
     */
    fun isTimeGoalMet(): Boolean = timeTodayMinutes >= timeGoalMinutes

    /**
     * Check if all goals are met for today
     */
    fun isAllGoalsMet(): Boolean {
        return isWordsGoalMet() && isReviewsGoalMet() &&
               isQuizzesGoalMet() && isTimeGoalMet()
    }

    /**
     * Get overall progress percentage
     */
    fun getOverallProgress(): Int {
        val wordsProgress = (wordsToday.toFloat() / wordsGoal * 25).coerceAtMost(25f)
        val reviewsProgress = (reviewsToday.toFloat() / reviewsGoal * 25).coerceAtMost(25f)
        val quizzesProgress = (quizzesToday.toFloat() / quizzesGoal * 25).coerceAtMost(25f)
        val timeProgress = (timeTodayMinutes.toFloat() / timeGoalMinutes * 25).coerceAtMost(25f)

        return (wordsProgress + reviewsProgress + quizzesProgress + timeProgress).toInt()
    }

    /**
     * Get words progress
     */
    fun getWordsProgress(): Float {
        return (wordsToday.toFloat() / wordsGoal).coerceAtMost(1f)
    }

    /**
     * Get reviews progress
     */
    fun getReviewsProgress(): Float {
        return (reviewsToday.toFloat() / reviewsGoal).coerceAtMost(1f)
    }

    /**
     * Get quizzes progress
     */
    fun getQuizzesProgress(): Float {
        return (quizzesToday.toFloat() / quizzesGoal).coerceAtMost(1f)
    }

    /**
     * Get time progress
     */
    fun getTimeProgress(): Float {
        return (timeTodayMinutes.toFloat() / timeGoalMinutes).coerceAtMost(1f)
    }

    /**
     * Check if needs reset (new day)
     */
    fun needsReset(): Boolean {
        val today = LocalDate.now()
        val lastReset = LocalDate.ofEpochDay(
            TimeUnit.MILLISECONDS.toDays(lastResetDate)
        )
        return today != lastReset
    }

    companion object {
        /**
         * Default daily goals
         */
        fun default(): DailyGoal {
            return DailyGoal(
                wordsGoal = 10,
                reviewsGoal = 20,
                quizzesGoal = 5,
                timeGoalMinutes = 15
            )
        }

        /**
         * Beginner goals
         */
        fun beginner(): DailyGoal {
            return DailyGoal(
                wordsGoal = 5,
                reviewsGoal = 10,
                quizzesGoal = 3,
                timeGoalMinutes = 10
            )
        }

        /**
         * Advanced goals
         */
        fun advanced(): DailyGoal {
            return DailyGoal(
                wordsGoal = 20,
                reviewsGoal = 40,
                quizzesGoal = 10,
                timeGoalMinutes = 30
            )
        }
    }
}
