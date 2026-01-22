package com.gultekinahmetabdullah.trainvoc.config

/**
 * Centralized configuration for the Trainvoc app.
 * Contains all magic numbers, thresholds, and configuration values
 * that were previously hardcoded throughout the codebase.
 *
 * Benefits:
 * - Single source of truth for all configuration
 * - Easy to adjust values without code changes
 * - Better testability
 * - Clear documentation of what each value means
 */

/**
 * Adaptive difficulty algorithm configuration
 * Controls how the quiz difficulty adjusts based on user performance
 */
object AdaptiveDifficultyConfig {
    // Accuracy thresholds for difficulty adjustment
    const val ACCURACY_TOO_HIGH = 0.90f      // User is finding it too easy
    const val ACCURACY_OPTIMAL_HIGH = 0.70f  // Upper bound of optimal zone
    const val ACCURACY_OPTIMAL_LOW = 0.50f   // Lower bound of optimal zone
    const val ACCURACY_GOOD = 0.80f          // Good performance threshold
    const val ACCURACY_POOR = 0.60f          // Struggling threshold
    const val ACCURACY_LOW = 0.70f           // Below average threshold

    // Speed thresholds (seconds per answer)
    const val SPEED_FAST_SECONDS = 5         // User is answering very quickly
    const val SPEED_NORMAL_SECONDS = 10      // Normal response time
    const val SPEED_SLOW_SECONDS = 20        // User is taking longer

    // Consistency thresholds for performance stability
    const val CONSISTENCY_HIGH = 0.15f       // Very consistent performance
    const val CONSISTENCY_MEDIUM = 0.30f     // Moderate variation

    // Word count thresholds for user progression
    const val WORDS_DUE_HIGH = 20            // Heavy workload
    const val WORDS_DUE_MEDIUM = 10          // Moderate workload
    const val LEARNED_WORDS_BEGINNER = 50    // New user threshold
    const val LEARNED_WORDS_INTERMEDIATE = 100
    const val LEARNED_WORDS_ADVANCED = 200

    // Time thresholds
    const val AVG_TIME_SLOW_SECONDS = 15     // Slow average response

    // Streak milestones
    const val STREAK_MILESTONE = 7           // One week streak

    // Algorithm weights
    const val CONFIDENCE_SIZE_WEIGHT = 0.6f
    const val CONFIDENCE_CONSISTENCY_WEIGHT = 0.4f

    // Quiz window for recent performance
    const val RECENT_QUIZ_WINDOW = 10
}

/**
 * Daily goal default values
 */
object DailyGoalConfig {
    const val DEFAULT_WORDS_GOAL = 10
    const val DEFAULT_REVIEWS_GOAL = 20
    const val DEFAULT_QUIZZES_GOAL = 5
    const val DEFAULT_TIME_GOAL_MINUTES = 15

    // Beginner goals (easier)
    const val BEGINNER_WORDS_GOAL = 5
    const val BEGINNER_REVIEWS_GOAL = 10
    const val BEGINNER_QUIZZES_GOAL = 3
    const val BEGINNER_TIME_GOAL_MINUTES = 10

    // Advanced goals (harder)
    const val ADVANCED_WORDS_GOAL = 20
    const val ADVANCED_REVIEWS_GOAL = 40
    const val ADVANCED_QUIZZES_GOAL = 10
    const val ADVANCED_TIME_GOAL_MINUTES = 30
}

/**
 * Database configuration
 */
object DatabaseConfig {
    const val VERSION = 14
    const val NAME = "trainvoc-db"
    const val LOCAL_USER_ID = "local_user"
}

/**
 * Spaced repetition (SM-2 algorithm) configuration
 */
object SpacedRepetitionConfig {
    const val INITIAL_EASINESS_FACTOR = 2.5
    const val MIN_EASINESS_FACTOR = 1.3
    const val EASINESS_MODIFIER = 0.1
}

/**
 * Notification configuration
 */
object NotificationConfig {
    const val ID_DAILY_REMINDER = 1001
    const val ID_STREAK_ALERT = 1002
    const val ID_WORD_OF_DAY = 1003
    const val ID_WORD_QUIZ = 1004

    const val CHANNEL_ID_REMINDERS = "trainvoc_reminders"
    const val CHANNEL_ID_ACHIEVEMENTS = "trainvoc_achievements"
}

/**
 * Cloud backup configuration
 */
object CloudBackupConfig {
    const val DEFAULT_BACKUP_INTERVAL_HOURS = 24L
    const val DEFAULT_WIFI_ONLY = true

    const val PREFS_NAME = "cloud_backup_prefs"
    const val KEY_LAST_SYNC = "last_sync_time"
    const val KEY_AUTO_BACKUP_ENABLED = "auto_backup_enabled"
    const val KEY_WIFI_ONLY = "wifi_only"

    const val BACKUP_WORK_TAG = "cloud_backup_work"
    const val SYNC_WORK_TAG = "cloud_sync_work"
}

/**
 * Animation timing configuration
 */
object AnimationConfig {
    const val SHIMMER_DURATION_MS = 1300
    const val SHIMMER_ANIMATION_RANGE = 1000f
    const val DEBOUNCE_SEARCH_MS = 300L
    const val BUTTON_DEBOUNCE_MS = 500L
}

/**
 * UI configuration
 */
object UiConfig {
    // Touch targets (Material Design minimum)
    const val MIN_TOUCH_TARGET_DP = 48

    // Selection feedback
    const val SELECTED_SCALE = 1.08f

    // Skeleton loading placeholder widths
    const val SKELETON_TITLE_WIDTH_RATIO = 0.6f
    const val SKELETON_SUBTITLE_WIDTH_RATIO = 0.4f
    const val SKELETON_CARD_HEADER_WIDTH_RATIO = 0.5f
}

/**
 * Cache configuration
 */
object CacheConfig {
    const val AUDIO_CACHE_MAX_SIZE_BYTES = 100 * 1024 * 1024  // 100 MB
    const val AUDIO_CACHE_MAX_COUNT = 1000
    const val CACHE_CLEANUP_PERCENTAGE = 0.2f  // Delete 20% oldest when full
}

/**
 * TTS (Text-to-Speech) configuration
 *
 * NOTE: Android's built-in TTS is FREE and doesn't require any API keys or payments.
 * The cost tracking values below are for internal analytics only, not actual billing.
 */
object TtsConfig {
    // These are for analytics tracking only - Android TTS is FREE
    const val ANALYTICS_COST_PER_CALL = 0.001  // Not actual cost - just tracking metric

    // Speed multipliers
    const val SPEED_SLOW = 0.5f
    const val SPEED_NORMAL = 1.0f
    const val SPEED_FAST = 1.5f
    const val SPEED_VERY_FAST = 2.0f
}
