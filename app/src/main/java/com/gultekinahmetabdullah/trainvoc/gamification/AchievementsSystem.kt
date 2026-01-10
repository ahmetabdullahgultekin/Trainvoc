package com.gultekinahmetabdullah.trainvoc.gamification

import com.gultekinahmetabdullah.trainvoc.analytics.LearningStats
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel

/**
 * Achievements and Gamification System
 *
 * Provides motivation through achievements, badges, and rewards to enhance
 * user engagement and maintain consistent learning habits.
 *
 * Features:
 * - 25+ achievements across different categories
 * - Bronze, Silver, Gold, Platinum tiers
 * - Progress tracking for each achievement
 * - Unlock notifications
 * - Leaderboard integration ready
 *
 * Benefits:
 * - Increased motivation
 * - Clear progression goals
 * - Sense of accomplishment
 * - Social competition (future)
 * - Habit formation
 *
 * Usage:
 * ```kotlin
 * val system = AchievementsSystem()
 *
 * // Check for newly unlocked achievements
 * val newAchievements = system.checkAchievements(learningStats)
 *
 * // Get all achievements with progress
 * val allAchievements = system.getAllAchievements(learningStats)
 *
 * // Get user's achievement summary
 * val summary = system.getAchievementSummary(learningStats)
 * ```
 */
class AchievementsSystem {

    /**
     * All available achievements in the system
     */
    val allAchievements: List<Achievement> = listOf(
        // Learning Milestones
        Achievement(
            id = "first_word",
            title = "First Steps",
            description = "Learn your first word",
            icon = "🎯",
            tier = AchievementTier.BRONZE,
            category = AchievementCategory.LEARNING,
            requirement = { it.learnedWords >= 1 },
            progressCalculator = { minOf(it.learnedWords, 1) to 1 }
        ),
        Achievement(
            id = "ten_words",
            title = "Getting Started",
            description = "Learn 10 words",
            icon = "📚",
            tier = AchievementTier.BRONZE,
            category = AchievementCategory.LEARNING,
            requirement = { it.learnedWords >= 10 },
            progressCalculator = { minOf(it.learnedWords, 10) to 10 }
        ),
        Achievement(
            id = "fifty_words",
            title = "Building Vocabulary",
            description = "Learn 50 words",
            icon = "📖",
            tier = AchievementTier.SILVER,
            category = AchievementCategory.LEARNING,
            requirement = { it.learnedWords >= 50 },
            progressCalculator = { minOf(it.learnedWords, 50) to 50 }
        ),
        Achievement(
            id = "hundred_words",
            title = "Centurion",
            description = "Learn 100 words",
            icon = "🏆",
            tier = AchievementTier.GOLD,
            category = AchievementCategory.LEARNING,
            requirement = { it.learnedWords >= 100 },
            progressCalculator = { minOf(it.learnedWords, 100) to 100 }
        ),
        Achievement(
            id = "five_hundred_words",
            title = "Vocabulary Master",
            description = "Learn 500 words",
            icon = "👑",
            tier = AchievementTier.PLATINUM,
            category = AchievementCategory.LEARNING,
            requirement = { it.learnedWords >= 500 },
            progressCalculator = { minOf(it.learnedWords, 500) to 500 }
        ),
        Achievement(
            id = "thousand_words",
            title = "Linguist",
            description = "Learn 1,000 words",
            icon = "⭐",
            tier = AchievementTier.PLATINUM,
            category = AchievementCategory.LEARNING,
            requirement = { it.learnedWords >= 1000 },
            progressCalculator = { minOf(it.learnedWords, 1000) to 1000 }
        ),

        // Streak Achievements
        Achievement(
            id = "three_day_streak",
            title = "Consistent Learner",
            description = "Study for 3 days in a row",
            icon = "🔥",
            tier = AchievementTier.BRONZE,
            category = AchievementCategory.STREAKS,
            requirement = { it.currentStreak >= 3 },
            progressCalculator = { minOf(it.currentStreak, 3) to 3 }
        ),
        Achievement(
            id = "week_streak",
            title = "Week Warrior",
            description = "Study for 7 days in a row",
            icon = "🔥🔥",
            tier = AchievementTier.SILVER,
            category = AchievementCategory.STREAKS,
            requirement = { it.currentStreak >= 7 },
            progressCalculator = { minOf(it.currentStreak, 7) to 7 }
        ),
        Achievement(
            id = "two_week_streak",
            title = "Dedicated Student",
            description = "Study for 14 days in a row",
            icon = "🔥🔥🔥",
            tier = AchievementTier.GOLD,
            category = AchievementCategory.STREAKS,
            requirement = { it.currentStreak >= 14 },
            progressCalculator = { minOf(it.currentStreak, 14) to 14 }
        ),
        Achievement(
            id = "month_streak",
            title = "Unstoppable",
            description = "Study for 30 days in a row",
            icon = "🔥🔥🔥🔥",
            tier = AchievementTier.PLATINUM,
            category = AchievementCategory.STREAKS,
            requirement = { it.currentStreak >= 30 },
            progressCalculator = { minOf(it.currentStreak, 30) to 30 }
        ),

        // Accuracy Achievements
        Achievement(
            id = "accurate_learner",
            title = "Sharp Mind",
            description = "Achieve 80% accuracy",
            icon = "🎯",
            tier = AchievementTier.BRONZE,
            category = AchievementCategory.ACCURACY,
            requirement = { it.averageAccuracy >= 80f },
            progressCalculator = { minOf(it.averageAccuracy.toInt(), 80) to 80 }
        ),
        Achievement(
            id = "perfectionist",
            title = "Perfectionist",
            description = "Achieve 90% accuracy",
            icon = "💎",
            tier = AchievementTier.SILVER,
            category = AchievementCategory.ACCURACY,
            requirement = { it.averageAccuracy >= 90f },
            progressCalculator = { minOf(it.averageAccuracy.toInt(), 90) to 90 }
        ),
        Achievement(
            id = "flawless",
            title = "Flawless",
            description = "Achieve 95% accuracy",
            icon = "⭐",
            tier = AchievementTier.GOLD,
            category = AchievementCategory.ACCURACY,
            requirement = { it.averageAccuracy >= 95f },
            progressCalculator = { minOf(it.averageAccuracy.toInt(), 95) to 95 }
        ),

        // Speed Achievements
        Achievement(
            id = "speed_learner",
            title = "Quick Thinker",
            description = "Average 5 seconds per word",
            icon = "⚡",
            tier = AchievementTier.SILVER,
            category = AchievementCategory.SPEED,
            requirement = { it.averageTimePerWord <= 5 },
            progressCalculator = {
                val progress = maxOf(0, 10 - it.averageTimePerWord)
                progress to 5
            }
        ),
        Achievement(
            id = "lightning_fast",
            title = "Lightning Fast",
            description = "Average 3 seconds per word",
            icon = "⚡⚡",
            tier = AchievementTier.GOLD,
            category = AchievementCategory.SPEED,
            requirement = { it.averageTimePerWord <= 3 },
            progressCalculator = {
                val progress = maxOf(0, 10 - it.averageTimePerWord)
                progress to 7
            }
        ),

        // Study Time Achievements
        Achievement(
            id = "one_hour",
            title = "First Hour",
            description = "Study for 1 hour total",
            icon = "⏰",
            tier = AchievementTier.BRONZE,
            category = AchievementCategory.TIME,
            requirement = { it.totalStudyTime >= 3600 },
            progressCalculator = { minOf(it.totalStudyTime, 3600) to 3600 }
        ),
        Achievement(
            id = "ten_hours",
            title = "Committed Learner",
            description = "Study for 10 hours total",
            icon = "⏰⏰",
            tier = AchievementTier.SILVER,
            category = AchievementCategory.TIME,
            requirement = { it.totalStudyTime >= 36000 },
            progressCalculator = { minOf(it.totalStudyTime, 36000) to 36000 }
        ),
        Achievement(
            id = "fifty_hours",
            title = "Dedicated Scholar",
            description = "Study for 50 hours total",
            icon = "⏰⏰⏰",
            tier = AchievementTier.GOLD,
            category = AchievementCategory.TIME,
            requirement = { it.totalStudyTime >= 180000 },
            progressCalculator = { minOf(it.totalStudyTime, 180000) to 180000 }
        ),

        // Progress Achievements
        Achievement(
            id = "weekly_warrior",
            title = "Weekly Warrior",
            description = "Learn 50 words in one week",
            icon = "📅",
            tier = AchievementTier.SILVER,
            category = AchievementCategory.PROGRESS,
            requirement = { it.weeklyProgress >= 50 },
            progressCalculator = { minOf(it.weeklyProgress, 50) to 50 }
        ),
        Achievement(
            id = "monthly_master",
            title = "Monthly Master",
            description = "Learn 200 words in one month",
            icon = "📆",
            tier = AchievementTier.GOLD,
            category = AchievementCategory.PROGRESS,
            requirement = { it.monthlyProgress >= 200 },
            progressCalculator = { minOf(it.monthlyProgress, 200) to 200 }
        ),

        // Level Achievements
        Achievement(
            id = "basic_complete",
            title = "Basic Foundations",
            description = "Master all A1 level words",
            icon = "🎓",
            tier = AchievementTier.BRONZE,
            category = AchievementCategory.LEVELS,
            requirement = {
                it.levelDistribution[WordLevel.A1]?.let { count ->
                    count >= 100  // Assuming 100 A1 words
                } ?: false
            },
            progressCalculator = {
                val progress = it.levelDistribution[WordLevel.A1] ?: 0
                minOf(progress, 100) to 100
            }
        ),
        Achievement(
            id = "intermediate_complete",
            title = "Intermediate Scholar",
            description = "Master all B1 level words",
            icon = "🎓",
            tier = AchievementTier.SILVER,
            category = AchievementCategory.LEVELS,
            requirement = {
                it.levelDistribution[WordLevel.B1]?.let { count ->
                    count >= 100
                } ?: false
            },
            progressCalculator = {
                val progress = it.levelDistribution[WordLevel.B1] ?: 0
                minOf(progress, 100) to 100
            }
        ),
        Achievement(
            id = "advanced_complete",
            title = "Advanced Expert",
            description = "Master all C1 level words",
            icon = "🎓",
            tier = AchievementTier.GOLD,
            category = AchievementCategory.LEVELS,
            requirement = {
                it.levelDistribution[WordLevel.C1]?.let { count ->
                    count >= 100
                } ?: false
            },
            progressCalculator = {
                val progress = it.levelDistribution[WordLevel.C1] ?: 0
                minOf(progress, 100) to 100
            }
        ),

        // Review Achievements
        Achievement(
            id = "diligent_reviewer",
            title = "Diligent Reviewer",
            description = "Complete 50 reviews",
            icon = "🔄",
            tier = AchievementTier.BRONZE,
            category = AchievementCategory.REVIEW,
            requirement = { it.wordsReviewedToday >= 50 },
            progressCalculator = { minOf(it.wordsReviewedToday, 50) to 50 }
        ),
        Achievement(
            id = "review_champion",
            title = "Review Champion",
            description = "Complete 200 reviews",
            icon = "🔄🔄",
            tier = AchievementTier.SILVER,
            category = AchievementCategory.REVIEW,
            requirement = { it.wordsReviewedToday >= 200 },
            progressCalculator = { minOf(it.wordsReviewedToday, 200) to 200 }
        )
    )

    /**
     * Check for newly unlocked achievements
     *
     * @param stats Current learning statistics
     * @param previouslyUnlocked Set of previously unlocked achievement IDs
     * @return List of newly unlocked achievements
     */
    fun checkNewlyUnlockedAchievements(
        stats: LearningStats,
        previouslyUnlocked: Set<String>
    ): List<Achievement> {
        return allAchievements.filter { achievement ->
            achievement.id !in previouslyUnlocked && achievement.isUnlocked(stats)
        }
    }

    /**
     * Get all achievements with current progress
     *
     * @param stats Current learning statistics
     * @param unlockedIds Set of unlocked achievement IDs
     * @return List of achievements with progress
     */
    fun getAllAchievementsWithProgress(
        stats: LearningStats,
        unlockedIds: Set<String>
    ): List<AchievementProgress> {
        return allAchievements.map { achievement ->
            val (current, total) = achievement.calculateProgress(stats)
            val isUnlocked = achievement.id in unlockedIds

            AchievementProgress(
                achievement = achievement,
                currentProgress = current,
                totalRequired = total,
                isUnlocked = isUnlocked,
                progressPercentage = (current.toFloat() / total.toFloat() * 100).coerceIn(0f, 100f)
            )
        }
    }

    /**
     * Get achievement summary statistics
     *
     * @param stats Learning statistics
     * @param unlockedIds Set of unlocked achievement IDs
     * @return Achievement summary
     */
    fun getAchievementSummary(
        stats: LearningStats,
        unlockedIds: Set<String>
    ): AchievementSummary {
        val totalAchievements = allAchievements.size
        val unlockedCount = unlockedIds.size
        val progressPercentage = (unlockedCount.toFloat() / totalAchievements * 100)

        val tierCounts = AchievementTier.entries.associateWith { tier ->
            val totalInTier = allAchievements.count { it.tier == tier }
            val unlockedInTier = allAchievements.count { it.id in unlockedIds && it.tier == tier }
            unlockedInTier to totalInTier
        }

        val categoryCounts = AchievementCategory.entries.associateWith { category ->
            val totalInCategory = allAchievements.count { it.category == category }
            val unlockedInCategory = allAchievements.count { it.id in unlockedIds && it.category == category }
            unlockedInCategory to totalInCategory
        }

        return AchievementSummary(
            totalAchievements = totalAchievements,
            unlockedAchievements = unlockedCount,
            progressPercentage = progressPercentage,
            tierProgress = tierCounts,
            categoryProgress = categoryCounts
        )
    }

    /**
     * Get achievements by category
     */
    fun getAchievementsByCategory(category: AchievementCategory): List<Achievement> {
        return allAchievements.filter { it.category == category }
    }

    /**
     * Get achievements by tier
     */
    fun getAchievementsByTier(tier: AchievementTier): List<Achievement> {
        return allAchievements.filter { it.tier == tier }
    }
}

/**
 * Individual achievement
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val tier: AchievementTier,
    val category: AchievementCategory,
    private val requirement: (LearningStats) -> Boolean,
    private val progressCalculator: (LearningStats) -> Pair<Int, Int>
) {
    fun isUnlocked(stats: LearningStats): Boolean = requirement(stats)
    fun calculateProgress(stats: LearningStats): Pair<Int, Int> = progressCalculator(stats)
}

/**
 * Achievement tier (difficulty/rarity)
 */
enum class AchievementTier(val displayName: String, val color: String) {
    BRONZE("Bronze", "#CD7F32"),
    SILVER("Silver", "#C0C0C0"),
    GOLD("Gold", "#FFD700"),
    PLATINUM("Platinum", "#E5E4E2")
}

/**
 * Achievement category
 */
enum class AchievementCategory(val displayName: String) {
    LEARNING("Learning Milestones"),
    STREAKS("Consistency"),
    ACCURACY("Accuracy"),
    SPEED("Speed"),
    TIME("Study Time"),
    PROGRESS("Progress"),
    LEVELS("Level Mastery"),
    REVIEW("Review Practice")
}

/**
 * Achievement progress information
 */
data class AchievementProgress(
    val achievement: Achievement,
    val currentProgress: Int,
    val totalRequired: Int,
    val isUnlocked: Boolean,
    val progressPercentage: Float
)

/**
 * Achievement summary statistics
 */
data class AchievementSummary(
    val totalAchievements: Int,
    val unlockedAchievements: Int,
    val progressPercentage: Float,
    val tierProgress: Map<AchievementTier, Pair<Int, Int>>,  // unlocked to total
    val categoryProgress: Map<AchievementCategory, Pair<Int, Int>>
)
