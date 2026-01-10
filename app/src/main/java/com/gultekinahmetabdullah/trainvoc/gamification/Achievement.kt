package com.gultekinahmetabdullah.trainvoc.gamification

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Achievement definitions
 * All available achievements in the app
 */
enum class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String, // Emoji icon
    val category: AchievementCategory,
    val tier: AchievementTier,
    val requirement: Int
) {
    // Streak Achievements
    STREAK_3("streak_3", "Getting Started", "Maintain a 3-day streak", "🔥", AchievementCategory.STREAK, AchievementTier.BRONZE, 3),
    STREAK_7("streak_7", "Week Warrior", "Maintain a 7-day streak", "🔥", AchievementCategory.STREAK, AchievementTier.SILVER, 7),
    STREAK_30("streak_30", "Monthly Master", "Maintain a 30-day streak", "🔥", AchievementCategory.STREAK, AchievementTier.GOLD, 30),
    STREAK_100("streak_100", "Unstoppable", "Maintain a 100-day streak", "🔥", AchievementCategory.STREAK, AchievementTier.PLATINUM, 100),
    STREAK_365("streak_365", "Year Long Learner", "Maintain a 365-day streak", "🔥", AchievementCategory.STREAK, AchievementTier.DIAMOND, 365),

    // Words Learned Achievements
    WORDS_10("words_10", "First Steps", "Learn 10 words", "📚", AchievementCategory.WORDS, AchievementTier.BRONZE, 10),
    WORDS_50("words_50", "Vocabulary Builder", "Learn 50 words", "📚", AchievementCategory.WORDS, AchievementTier.BRONZE, 50),
    WORDS_100("words_100", "Word Collector", "Learn 100 words", "📚", AchievementCategory.WORDS, AchievementTier.SILVER, 100),
    WORDS_500("words_500", "Linguist", "Learn 500 words", "📚", AchievementCategory.WORDS, AchievementTier.GOLD, 500),
    WORDS_1000("words_1000", "Master Linguist", "Learn 1,000 words", "📚", AchievementCategory.WORDS, AchievementTier.PLATINUM, 1000),
    WORDS_5000("words_5000", "Word Wizard", "Learn 5,000 words", "📚", AchievementCategory.WORDS, AchievementTier.DIAMOND, 5000),

    // Quiz Achievements
    QUIZ_10("quiz_10", "Quiz Starter", "Complete 10 quizzes", "🎯", AchievementCategory.QUIZ, AchievementTier.BRONZE, 10),
    QUIZ_50("quiz_50", "Quiz Enthusiast", "Complete 50 quizzes", "🎯", AchievementCategory.QUIZ, AchievementTier.SILVER, 50),
    QUIZ_100("quiz_100", "Quiz Master", "Complete 100 quizzes", "🎯", AchievementCategory.QUIZ, AchievementTier.GOLD, 100),
    QUIZ_500("quiz_500", "Quiz Champion", "Complete 500 quizzes", "🎯", AchievementCategory.QUIZ, AchievementTier.PLATINUM, 500),

    // Perfect Score Achievements
    PERFECT_10("perfect_10", "Perfect Start", "10 perfect quizzes", "⭐", AchievementCategory.PERFECT, AchievementTier.BRONZE, 10),
    PERFECT_25("perfect_25", "Perfectionist", "25 perfect quizzes", "⭐", AchievementCategory.PERFECT, AchievementTier.SILVER, 25),
    PERFECT_50("perfect_50", "Flawless", "50 perfect quizzes", "⭐", AchievementCategory.PERFECT, AchievementTier.GOLD, 50),
    PERFECT_100("perfect_100", "Ace", "100 perfect quizzes", "⭐", AchievementCategory.PERFECT, AchievementTier.PLATINUM, 100),

    // Daily Goal Achievements
    GOALS_7("goals_7", "Goal Getter", "Meet daily goals 7 times", "🎯", AchievementCategory.GOALS, AchievementTier.BRONZE, 7),
    GOALS_30("goals_30", "Consistent Learner", "Meet daily goals 30 times", "🎯", AchievementCategory.GOALS, AchievementTier.SILVER, 30),
    GOALS_100("goals_100", "Dedicated Student", "Meet daily goals 100 times", "🎯", AchievementCategory.GOALS, AchievementTier.GOLD, 100),
    GOALS_365("goals_365", "Goal Master", "Meet daily goals 365 times", "🎯", AchievementCategory.GOALS, AchievementTier.PLATINUM, 365),

    // Review Achievements
    REVIEWS_100("reviews_100", "Reviewer", "Review 100 words", "🔄", AchievementCategory.REVIEW, AchievementTier.BRONZE, 100),
    REVIEWS_500("reviews_500", "Review Champion", "Review 500 words", "🔄", AchievementCategory.REVIEW, AchievementTier.SILVER, 500),
    REVIEWS_1000("reviews_1000", "Master Reviewer", "Review 1,000 words", "🔄", AchievementCategory.REVIEW, AchievementTier.GOLD, 1000),
    REVIEWS_5000("reviews_5000", "Review Legend", "Review 5,000 words", "🔄", AchievementCategory.REVIEW, AchievementTier.PLATINUM, 5000),

    // Time Achievements
    TIME_5("time_5", "Time Investor", "Spend 5 hours learning", "⏰", AchievementCategory.TIME, AchievementTier.BRONZE, 300),
    TIME_20("time_20", "Time Committed", "Spend 20 hours learning", "⏰", AchievementCategory.TIME, AchievementTier.SILVER, 1200),
    TIME_50("time_50", "Time Master", "Spend 50 hours learning", "⏰", AchievementCategory.TIME, AchievementTier.GOLD, 3000),
    TIME_100("time_100", "Time Legend", "Spend 100 hours learning", "⏰", AchievementCategory.TIME, AchievementTier.PLATINUM, 6000),

    // Special Achievements
    EARLY_BIRD("early_bird", "Early Bird", "Practice before 7 AM (10 times)", "🌅", AchievementCategory.SPECIAL, AchievementTier.SILVER, 10),
    NIGHT_OWL("night_owl", "Night Owl", "Practice after 10 PM (10 times)", "🦉", AchievementCategory.SPECIAL, AchievementTier.SILVER, 10),
    WEEKEND_WARRIOR("weekend_warrior", "Weekend Warrior", "Practice on 20 weekends", "🎉", AchievementCategory.SPECIAL, AchievementTier.GOLD, 20),
    SPEED_DEMON("speed_demon", "Speed Demon", "Complete quiz in under 1 minute (10 times)", "⚡", AchievementCategory.SPECIAL, AchievementTier.GOLD, 10),
    COMEBACK("comeback", "Comeback Kid", "Return after 30-day break", "💪", AchievementCategory.SPECIAL, AchievementTier.SILVER, 1);

    companion object {
        fun fromId(id: String): Achievement? {
            return values().find { it.id == id }
        }

        fun getByCategory(category: AchievementCategory): List<Achievement> {
            return values().filter { it.category == category }
        }

        fun getByTier(tier: AchievementTier): List<Achievement> {
            return values().filter { it.tier == tier }
        }
    }
}

/**
 * Achievement categories
 */
enum class AchievementCategory(val displayName: String) {
    STREAK("Streak"),
    WORDS("Words Learned"),
    QUIZ("Quizzes"),
    PERFECT("Perfect Scores"),
    GOALS("Daily Goals"),
    REVIEW("Reviews"),
    TIME("Time Spent"),
    SPECIAL("Special")
}

/**
 * Achievement tiers (rarity)
 */
enum class AchievementTier(val displayName: String, val color: String) {
    BRONZE("Bronze", "#CD7F32"),
    SILVER("Silver", "#C0C0C0"),
    GOLD("Gold", "#FFD700"),
    PLATINUM("Platinum", "#E5E4E2"),
    DIAMOND("Diamond", "#B9F2FF")
}

/**
 * User achievement entity
 * Tracks which achievements user has unlocked
 */
@Entity(
    tableName = "user_achievements",
    indices = [
        Index(value = ["user_id"], name = "index_user_achievements_user_id"),
        Index(value = ["achievement_id"], name = "index_user_achievements_achievement_id"),
        Index(value = ["is_unlocked"], name = "index_user_achievements_is_unlocked"),
        Index(value = ["user_id", "achievement_id"], name = "index_user_achievements_user_achievement")
    ]
)
data class UserAchievement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: String = "local_user",

    @ColumnInfo(name = "achievement_id")
    val achievementId: String,

    @ColumnInfo(name = "unlocked_at")
    val unlockedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "progress")
    val progress: Int = 0,

    @ColumnInfo(name = "is_unlocked")
    val isUnlocked: Boolean = false,

    @ColumnInfo(name = "notified")
    val notified: Boolean = false
) {
    fun getAchievement(): Achievement? {
        return Achievement.fromId(achievementId)
    }

    fun getProgressPercentage(): Int {
        val achievement = getAchievement() ?: return 0
        return ((progress.toFloat() / achievement.requirement) * 100).toInt().coerceAtMost(100)
    }
}

/**
 * Achievement progress tracking
 * Tracks current progress toward achievements
 */
data class AchievementProgress(
    val achievement: Achievement,
    val currentProgress: Int,
    val isUnlocked: Boolean,
    val unlockedAt: Long?
) {
    val progressPercentage: Int
        get() = ((currentProgress.toFloat() / achievement.requirement) * 100)
            .toInt()
            .coerceAtMost(100)

    val isCompleted: Boolean
        get() = currentProgress >= achievement.requirement
}
