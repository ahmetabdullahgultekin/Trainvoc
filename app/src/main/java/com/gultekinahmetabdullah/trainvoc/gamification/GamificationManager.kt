package com.gultekinahmetabdullah.trainvoc.gamification

import android.content.Context
import com.gultekinahmetabdullah.trainvoc.widget.DailyGoalsWidgetProvider
import com.gultekinahmetabdullah.trainvoc.widget.StreakWidgetProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gamification Manager
 * Handles streak tracking, daily goals, and achievements
 */
@Singleton
class GamificationManager @Inject constructor(
    private val dao: GamificationDao,
    @ApplicationContext private val context: Context
) {

    // ============ Streak Tracking ============

    /**
     * Record activity for today
     */
    suspend fun recordActivity() {
        val streak = dao.getStreakTracking() ?: StreakTracking.initial()

        if (streak.isActiveToday()) {
            // Already recorded today
            return
        }

        val newStreak = if (streak.isStreakValid()) {
            // Extend streak
            val currentStreak = streak.currentStreak + 1
            streak.copy(
                currentStreak = currentStreak,
                longestStreak = maxOf(currentStreak, streak.longestStreak),
                lastActivityDate = System.currentTimeMillis(),
                totalActiveDays = streak.totalActiveDays + 1,
                streakStartDate = streak.streakStartDate ?: System.currentTimeMillis()
            )
        } else {
            // Start new streak
            streak.copy(
                currentStreak = 1,
                lastActivityDate = System.currentTimeMillis(),
                totalActiveDays = streak.totalActiveDays + 1,
                streakStartDate = System.currentTimeMillis()
            )
        }

        dao.insertStreakTracking(newStreak)

        // Check streak achievements
        checkStreakAchievements(newStreak.currentStreak)

        // Update widgets
        StreakWidgetProvider.requestUpdate(context)
    }

    /**
     * Get current streak
     */
    suspend fun getCurrentStreak(): Int {
        val streak = dao.getStreakTracking() ?: return 0
        return if (streak.isStreakValid()) streak.currentStreak else 0
    }

    /**
     * Get streak tracking flow
     */
    fun getStreakFlow(): Flow<StreakTracking> {
        return dao.getStreakTrackingFlow().map { it ?: StreakTracking.initial() }
    }

    /**
     * Use streak freeze (Premium feature)
     */
    suspend fun useStreakFreeze() {
        dao.useStreakFreeze()
    }

    // ============ Daily Goals ============

    /**
     * Initialize daily goals if not exists
     */
    suspend fun initializeDailyGoals() {
        val existing = dao.getDailyGoal()
        if (existing == null) {
            dao.insertDailyGoal(DailyGoal.default())
        }
    }

    /**
     * Get daily goals
     */
    suspend fun getDailyGoals(): DailyGoal {
        return dao.getDailyGoal() ?: DailyGoal.default()
    }

    /**
     * Get daily goals flow
     */
    fun getDailyGoalsFlow(): Flow<DailyGoal> {
        return dao.getDailyGoalFlow().map { it ?: DailyGoal.default() }
    }

    /**
     * Update goal targets
     */
    suspend fun updateGoalTargets(
        wordsGoal: Int,
        reviewsGoal: Int,
        quizzesGoal: Int,
        timeGoalMinutes: Int
    ) {
        dao.updateGoalTargets(
            wordsGoal = wordsGoal,
            reviewsGoal = reviewsGoal,
            quizzesGoal = quizzesGoal,
            timeGoalMinutes = timeGoalMinutes
        )
    }

    /**
     * Record word learned
     */
    suspend fun recordWordLearned() {
        checkAndResetDaily()
        dao.incrementWordsToday()
        checkGoalCompletion()

        // Check achievements
        val goal = getDailyGoals()
        checkAchievement(Achievement.WORDS_10, goal.wordsToday)
        checkAchievement(Achievement.WORDS_50, goal.wordsToday)
        checkAchievement(Achievement.WORDS_100, goal.wordsToday)
        checkAchievement(Achievement.WORDS_500, goal.wordsToday)
        checkAchievement(Achievement.WORDS_1000, goal.wordsToday)
        checkAchievement(Achievement.WORDS_5000, goal.wordsToday)

        // Update widgets
        DailyGoalsWidgetProvider.requestUpdate(context)
    }

    /**
     * Record word reviewed
     */
    suspend fun recordWordReviewed() {
        checkAndResetDaily()
        dao.incrementReviewsToday()
        checkGoalCompletion()

        // Check achievements
        val goal = getDailyGoals()
        checkAchievement(Achievement.REVIEWS_100, goal.reviewsToday)
        checkAchievement(Achievement.REVIEWS_500, goal.reviewsToday)
        checkAchievement(Achievement.REVIEWS_1000, goal.reviewsToday)
        checkAchievement(Achievement.REVIEWS_5000, goal.reviewsToday)

        // Update widgets
        DailyGoalsWidgetProvider.requestUpdate(context)
    }

    /**
     * Record quiz completed
     */
    suspend fun recordQuizCompleted(isPerfect: Boolean = false) {
        checkAndResetDaily()
        dao.incrementQuizzesToday()
        checkGoalCompletion()

        // Check quiz achievements
        val goal = getDailyGoals()
        checkAchievement(Achievement.QUIZ_10, goal.quizzesToday)
        checkAchievement(Achievement.QUIZ_50, goal.quizzesToday)
        checkAchievement(Achievement.QUIZ_100, goal.quizzesToday)
        checkAchievement(Achievement.QUIZ_500, goal.quizzesToday)

        // Check perfect score achievements
        if (isPerfect) {
            val achievement = dao.getAchievement(achievementId = Achievement.PERFECT_10.id)
            val perfectCount = achievement?.progress ?: 0
            checkAchievement(Achievement.PERFECT_10, perfectCount + 1)
            checkAchievement(Achievement.PERFECT_25, perfectCount + 1)
            checkAchievement(Achievement.PERFECT_50, perfectCount + 1)
            checkAchievement(Achievement.PERFECT_100, perfectCount + 1)
        }

        // Check special achievements
        checkSpecialAchievements()

        // Update widgets
        DailyGoalsWidgetProvider.requestUpdate(context)
    }

    /**
     * Record study time
     */
    suspend fun recordStudyTime(minutes: Int) {
        checkAndResetDaily()
        dao.addTimeToday(minutes = minutes)
        checkGoalCompletion()

        // Check time achievements (in minutes)
        val goal = getDailyGoals()
        checkAchievement(Achievement.TIME_5, goal.timeTodayMinutes)
        checkAchievement(Achievement.TIME_20, goal.timeTodayMinutes)
        checkAchievement(Achievement.TIME_50, goal.timeTodayMinutes)
        checkAchievement(Achievement.TIME_100, goal.timeTodayMinutes)

        // Update widgets
        DailyGoalsWidgetProvider.requestUpdate(context)
    }

    /**
     * Check and reset daily goals if new day
     */
    private suspend fun checkAndResetDaily() {
        val goal = dao.getDailyGoal() ?: return
        if (goal.needsReset()) {
            dao.resetDailyProgress()
        }
    }

    /**
     * Check if all goals completed
     */
    private suspend fun checkGoalCompletion() {
        val goal = dao.getDailyGoal() ?: return
        if (goal.isAllGoalsMet()) {
            dao.incrementGoalsCompleted()

            // Check goal achievements
            val totalCompleted = dao.getTotalGoalsCompleted() ?: 0
            checkAchievement(Achievement.GOALS_7, totalCompleted)
            checkAchievement(Achievement.GOALS_30, totalCompleted)
            checkAchievement(Achievement.GOALS_100, totalCompleted)
            checkAchievement(Achievement.GOALS_365, totalCompleted)
        }
    }

    // ============ Achievements ============

    /**
     * Initialize all achievements
     */
    suspend fun initializeAchievements() {
        val existing = dao.getAllAchievements()
        val existingIds = existing.map { it.achievementId }.toSet()

        val newAchievements = Achievement.values()
            .filter { it.id !in existingIds }
            .map { achievement ->
                UserAchievement(
                    achievementId = achievement.id,
                    progress = 0,
                    isUnlocked = false
                )
            }

        if (newAchievements.isNotEmpty()) {
            dao.insertAchievements(newAchievements)
        }
    }

    /**
     * Get all achievements with progress
     */
    suspend fun getAllAchievementsWithProgress(): List<AchievementProgress> {
        val userAchievements = dao.getAllAchievements()

        return Achievement.values().map { achievement ->
            val userAchievement = userAchievements.find { it.achievementId == achievement.id }

            AchievementProgress(
                achievement = achievement,
                currentProgress = userAchievement?.progress ?: 0,
                isUnlocked = userAchievement?.isUnlocked ?: false,
                unlockedAt = if (userAchievement?.isUnlocked == true) userAchievement.unlockedAt else null
            )
        }
    }

    /**
     * Get unlocked achievements flow
     */
    fun getUnlockedAchievementsFlow(): Flow<List<UserAchievement>> {
        return dao.getUnlockedAchievementsFlow()
    }

    /**
     * Get achievement count flow
     */
    fun getAchievementCountFlow(): Flow<Int> {
        return dao.getUnlockedAchievementCountFlow()
    }

    /**
     * Check and unlock achievement if threshold met
     */
    private suspend fun checkAchievement(achievement: Achievement, currentValue: Int) {
        val userAchievement = dao.getAchievement(achievementId = achievement.id)

        if (userAchievement == null) {
            // Create new achievement tracking
            val newAchievement = UserAchievement(
                achievementId = achievement.id,
                progress = currentValue,
                isUnlocked = currentValue >= achievement.requirement,
                unlockedAt = if (currentValue >= achievement.requirement) System.currentTimeMillis() else null
            )
            dao.insertAchievement(newAchievement)
        } else if (!userAchievement.isUnlocked && currentValue >= achievement.requirement) {
            // Unlock achievement
            dao.updateAchievementProgress(
                achievementId = achievement.id,
                progress = currentValue,
                isUnlocked = true,
                unlockedAt = System.currentTimeMillis()
            )
        } else if (!userAchievement.isUnlocked) {
            // Update progress
            dao.updateAchievementProgress(
                achievementId = achievement.id,
                progress = currentValue,
                isUnlocked = false
            )
        }
    }

    /**
     * Check streak-specific achievements
     */
    private suspend fun checkStreakAchievements(currentStreak: Int) {
        checkAchievement(Achievement.STREAK_3, currentStreak)
        checkAchievement(Achievement.STREAK_7, currentStreak)
        checkAchievement(Achievement.STREAK_30, currentStreak)
        checkAchievement(Achievement.STREAK_100, currentStreak)
        checkAchievement(Achievement.STREAK_365, currentStreak)
    }

    /**
     * Check special time-based achievements
     */
    private suspend fun checkSpecialAchievements() {
        val currentHour = LocalTime.now().hour

        // Early bird (before 7 AM)
        if (currentHour < 7) {
            val achievement = dao.getAchievement(achievementId = Achievement.EARLY_BIRD.id)
            val count = achievement?.progress ?: 0
            checkAchievement(Achievement.EARLY_BIRD, count + 1)
        }

        // Night owl (after 10 PM)
        if (currentHour >= 22) {
            val achievement = dao.getAchievement(achievementId = Achievement.NIGHT_OWL.id)
            val count = achievement?.progress ?: 0
            checkAchievement(Achievement.NIGHT_OWL, count + 1)
        }

        // Weekend warrior
        val dayOfWeek = LocalDate.now().dayOfWeek.value
        if (dayOfWeek >= 6) { // Saturday or Sunday
            val achievement = dao.getAchievement(achievementId = Achievement.WEEKEND_WARRIOR.id)
            val count = achievement?.progress ?: 0
            checkAchievement(Achievement.WEEKEND_WARRIOR, count + 1)
        }
    }

    /**
     * Get newly unlocked achievements (for notifications)
     */
    suspend fun getNewlyUnlockedAchievements(): List<UserAchievement> {
        return dao.getUnnotifiedAchievements()
    }

    /**
     * Mark achievement as notified
     */
    suspend fun markAchievementNotified(achievementId: String) {
        dao.markAchievementNotified(achievementId = achievementId)
    }

    // ============ Statistics ============

    /**
     * Get comprehensive stats
     */
    suspend fun getStats(): GamificationStats {
        val streak = dao.getStreakTracking() ?: StreakTracking.initial()
        val goal = dao.getDailyGoal() ?: DailyGoal.default()
        val achievementCount = dao.getUnlockedAchievementCount()
        val totalAchievements = Achievement.values().size

        return GamificationStats(
            currentStreak = if (streak.isStreakValid()) streak.currentStreak else 0,
            longestStreak = streak.longestStreak,
            totalActiveDays = streak.totalActiveDays,
            todayProgress = goal.getOverallProgress(),
            goalsCompleted = goal.goalsCompletedTotal,
            achievementsUnlocked = achievementCount,
            totalAchievements = totalAchievements
        )
    }
}

/**
 * Gamification statistics summary
 */
data class GamificationStats(
    val currentStreak: Int,
    val longestStreak: Int,
    val totalActiveDays: Int,
    val todayProgress: Int,
    val goalsCompleted: Int,
    val achievementsUnlocked: Int,
    val totalAchievements: Int
) {
    val achievementPercentage: Int
        get() = if (totalAchievements > 0) {
            (achievementsUnlocked * 100 / totalAchievements)
        } else 0
}
