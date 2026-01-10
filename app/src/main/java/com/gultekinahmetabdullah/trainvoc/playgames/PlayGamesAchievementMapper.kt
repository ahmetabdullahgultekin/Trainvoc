package com.gultekinahmetabdullah.trainvoc.playgames

import com.gultekinahmetabdullah.trainvoc.gamification.Achievement

/**
 * Maps Trainvoc achievements to Google Play Games achievement IDs
 *
 * IMPORTANT: These achievement IDs must be created in Google Play Console first!
 *
 * Setup Instructions:
 * 1. Go to Play Console > Your App > Grow > Play Games Services > Setup and management > Achievements
 * 2. Create 44 achievements matching our Achievement enum
 * 3. Copy the achievement IDs here
 *
 * For now, using placeholder IDs. Replace with real IDs from Play Console.
 */
object PlayGamesAchievementMapper {

    /**
     * Maps local achievement to Play Games achievement ID
     */
    fun getPlayGamesId(achievement: Achievement): String {
        return when (achievement) {
            // Streak Achievements (5)
            Achievement.STREAK_3 -> "CgkI_trainvoc_achievement_streak_3"
            Achievement.STREAK_7 -> "CgkI_trainvoc_achievement_streak_7"
            Achievement.STREAK_30 -> "CgkI_trainvoc_achievement_streak_30"
            Achievement.STREAK_100 -> "CgkI_trainvoc_achievement_streak_100"
            Achievement.STREAK_365 -> "CgkI_trainvoc_achievement_streak_365"

            // Words Learned Achievements (6)
            Achievement.WORDS_10 -> "CgkI_trainvoc_achievement_words_10"
            Achievement.WORDS_50 -> "CgkI_trainvoc_achievement_words_50"
            Achievement.WORDS_100 -> "CgkI_trainvoc_achievement_words_100"
            Achievement.WORDS_500 -> "CgkI_trainvoc_achievement_words_500"
            Achievement.WORDS_1000 -> "CgkI_trainvoc_achievement_words_1000"
            Achievement.WORDS_5000 -> "CgkI_trainvoc_achievement_words_5000"

            // Quiz Achievements (4)
            Achievement.QUIZ_10 -> "CgkI_trainvoc_achievement_quiz_10"
            Achievement.QUIZ_50 -> "CgkI_trainvoc_achievement_quiz_50"
            Achievement.QUIZ_100 -> "CgkI_trainvoc_achievement_quiz_100"
            Achievement.QUIZ_500 -> "CgkI_trainvoc_achievement_quiz_500"

            // Perfect Score Achievements (4)
            Achievement.PERFECT_10 -> "CgkI_trainvoc_achievement_perfect_10"
            Achievement.PERFECT_25 -> "CgkI_trainvoc_achievement_perfect_25"
            Achievement.PERFECT_50 -> "CgkI_trainvoc_achievement_perfect_50"
            Achievement.PERFECT_100 -> "CgkI_trainvoc_achievement_perfect_100"

            // Daily Goals Achievements (4)
            Achievement.GOALS_7 -> "CgkI_trainvoc_achievement_goals_7"
            Achievement.GOALS_30 -> "CgkI_trainvoc_achievement_goals_30"
            Achievement.GOALS_100 -> "CgkI_trainvoc_achievement_goals_100"
            Achievement.GOALS_365 -> "CgkI_trainvoc_achievement_goals_365"

            // Review Achievements (4)
            Achievement.REVIEW_100 -> "CgkI_trainvoc_achievement_review_100"
            Achievement.REVIEW_500 -> "CgkI_trainvoc_achievement_review_500"
            Achievement.REVIEW_1000 -> "CgkI_trainvoc_achievement_review_1000"
            Achievement.REVIEW_5000 -> "CgkI_trainvoc_achievement_review_5000"

            // Time Spent Achievements (4)
            Achievement.TIME_5 -> "CgkI_trainvoc_achievement_time_5h"
            Achievement.TIME_20 -> "CgkI_trainvoc_achievement_time_20h"
            Achievement.TIME_50 -> "CgkI_trainvoc_achievement_time_50h"
            Achievement.TIME_100 -> "CgkI_trainvoc_achievement_time_100h"

            // Special Achievements (5)
            Achievement.EARLY_BIRD -> "CgkI_trainvoc_achievement_early_bird"
            Achievement.NIGHT_OWL -> "CgkI_trainvoc_achievement_night_owl"
            Achievement.WEEKEND_WARRIOR -> "CgkI_trainvoc_achievement_weekend"
            Achievement.POLYGLOT -> "CgkI_trainvoc_achievement_polyglot"
            Achievement.PERFECTIONIST -> "CgkI_trainvoc_achievement_perfectionist"

            // Level Achievements (14)
            Achievement.LEVEL_A1 -> "CgkI_trainvoc_achievement_level_a1"
            Achievement.LEVEL_A2 -> "CgkI_trainvoc_achievement_level_a2"
            Achievement.LEVEL_B1 -> "CgkI_trainvoc_achievement_level_b1"
            Achievement.LEVEL_B2 -> "CgkI_trainvoc_achievement_level_b2"
            Achievement.LEVEL_C1 -> "CgkI_trainvoc_achievement_level_c1"
            Achievement.LEVEL_C2 -> "CgkI_trainvoc_achievement_level_c2"
            Achievement.LEVEL_YDS -> "CgkI_trainvoc_achievement_level_yds"
            Achievement.EXAM_TOEFL -> "CgkI_trainvoc_achievement_exam_toefl"
            Achievement.EXAM_IELTS -> "CgkI_trainvoc_achievement_exam_ielts"
            Achievement.EXAM_YOKDIL -> "CgkI_trainvoc_achievement_exam_yokdil"
            Achievement.EXAM_KPDS -> "CgkI_trainvoc_achievement_exam_kpds"
            Achievement.ALL_LEVELS -> "CgkI_trainvoc_achievement_all_levels"
            Achievement.MASTER -> "CgkI_trainvoc_achievement_master"
            Achievement.GRANDMASTER -> "CgkI_trainvoc_achievement_grandmaster"
        }
    }

    /**
     * Get all Play Games achievement IDs
     */
    fun getAllPlayGamesIds(): List<String> {
        return Achievement.values().map { getPlayGamesId(it) }
    }

    /**
     * Check if achievement ID is valid (for debugging)
     */
    fun isValidPlayGamesId(id: String): Boolean {
        return id.startsWith("CgkI_trainvoc_achievement_")
    }
}
