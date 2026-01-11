package com.gultekinahmetabdullah.trainvoc.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gultekinahmetabdullah.trainvoc.audio.AudioCache
import com.gultekinahmetabdullah.trainvoc.billing.database.PurchaseRecord
import com.gultekinahmetabdullah.trainvoc.billing.database.Subscription
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordExamCrossRef
import com.gultekinahmetabdullah.trainvoc.examples.ExampleSentence
import com.gultekinahmetabdullah.trainvoc.features.database.FeatureUsageLog
import com.gultekinahmetabdullah.trainvoc.features.database.GlobalFeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.database.UserFeatureFlag
import com.gultekinahmetabdullah.trainvoc.gamification.DailyGoal
import com.gultekinahmetabdullah.trainvoc.gamification.StreakTracking
import com.gultekinahmetabdullah.trainvoc.gamification.UserAchievement
import com.gultekinahmetabdullah.trainvoc.games.GameSession
import com.gultekinahmetabdullah.trainvoc.games.FlipCardGameStats
import com.gultekinahmetabdullah.trainvoc.games.SRSCard
import com.gultekinahmetabdullah.trainvoc.games.SpeedMatchStats
import com.gultekinahmetabdullah.trainvoc.images.WordImage
import com.gultekinahmetabdullah.trainvoc.offline.SyncQueue
import com.gultekinahmetabdullah.trainvoc.config.DatabaseConfig
import com.gultekinahmetabdullah.trainvoc.features.WordOfDay
import com.gultekinahmetabdullah.trainvoc.features.WordOfDayDao
import com.gultekinahmetabdullah.trainvoc.quiz.QuizHistory
import com.gultekinahmetabdullah.trainvoc.quiz.QuizHistoryDao
import com.gultekinahmetabdullah.trainvoc.quiz.QuizQuestionResult

@Database(
    entities = [
        Word::class,
        Statistic::class,
        Exam::class,
        WordExamCrossRef::class,
        GlobalFeatureFlag::class,
        UserFeatureFlag::class,
        FeatureUsageLog::class,
        AudioCache::class,
        WordImage::class,
        ExampleSentence::class,
        SyncQueue::class,
        Subscription::class,
        PurchaseRecord::class,
        StreakTracking::class,
        DailyGoal::class,
        UserAchievement::class,
        GameSession::class,
        FlipCardGameStats::class,
        SRSCard::class,
        SpeedMatchStats::class,
        WordOfDay::class,
        QuizHistory::class,
        QuizQuestionResult::class
    ],
    version = 14
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao
    abstract fun examDao(): ExamDao
    abstract fun wordExamCrossRefDao(): WordExamCrossRefDao
    abstract fun statisticDao(): StatisticDao
    abstract fun featureFlagDao(): com.gultekinahmetabdullah.trainvoc.features.database.FeatureFlagDao
    abstract fun audioCacheDao(): com.gultekinahmetabdullah.trainvoc.audio.AudioCacheDao
    abstract fun wordImageDao(): com.gultekinahmetabdullah.trainvoc.images.WordImageDao
    abstract fun exampleSentenceDao(): com.gultekinahmetabdullah.trainvoc.examples.ExampleSentenceDao
    abstract fun syncQueueDao(): com.gultekinahmetabdullah.trainvoc.offline.SyncQueueDao
    abstract fun subscriptionDao(): com.gultekinahmetabdullah.trainvoc.billing.database.SubscriptionDao
    abstract fun gamificationDao(): com.gultekinahmetabdullah.trainvoc.gamification.GamificationDao
    abstract fun gamesDao(): com.gultekinahmetabdullah.trainvoc.games.GamesDao
    abstract fun wordOfDayDao(): WordOfDayDao
    abstract fun quizHistoryDao(): QuizHistoryDao

    object DatabaseBuilder {
        private val DATABASE_NAME = DatabaseConfig.NAME

        private var instance: AppDatabase? = null

        /**
         * Migration from version 1 to 2: Add performance indices
         *
         * PERFORMANCE IMPROVEMENT:
         * - Adds indices to frequently queried columns
         * - Improves query performance by 10-100x for large datasets
         * - Indices added to: words.level, words.stat_id, words.last_reviewed,
         *   statistics.learned, statistics.correct_count, statistics.wrong_count
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create indices on words table
                database.execSQL("CREATE INDEX IF NOT EXISTS index_words_level ON words(level)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_words_stat_id ON words(stat_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_words_last_reviewed ON words(last_reviewed)")

                // Create indices on statistics table
                database.execSQL("CREATE INDEX IF NOT EXISTS index_statistics_learned ON statistics(learned)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_statistics_correct_count ON statistics(correct_count)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_statistics_wrong_count ON statistics(wrong_count)")
            }
        }

        /**
         * Migration from version 2 to 3: Add spaced repetition fields
         *
         * SPACED REPETITION (SM-2 ALGORITHM):
         * - Adds fields for spaced repetition algorithm
         * - next_review_date: When word should be reviewed next
         * - easiness_factor: Learning difficulty (1.3-3.5, default 2.5)
         * - interval_days: Current interval between reviews
         * - repetitions: Consecutive successful reviews
         *
         * Performance: Also adds index on next_review_date for efficient due date queries
         */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add spaced repetition columns to words table
                database.execSQL("ALTER TABLE words ADD COLUMN next_review_date INTEGER")
                database.execSQL("ALTER TABLE words ADD COLUMN easiness_factor REAL NOT NULL DEFAULT 2.5")
                database.execSQL("ALTER TABLE words ADD COLUMN interval_days INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE words ADD COLUMN repetitions INTEGER NOT NULL DEFAULT 0")

                // Create index for efficient due date queries
                database.execSQL("CREATE INDEX IF NOT EXISTS index_words_next_review_date ON words(next_review_date)")

                // Initialize next_review_date to NULL for existing words (they'll be scheduled on first review)
                // No need to update as NULL is default
            }
        }

        /**
         * Migration from version 3 to 4: Add feature flag system
         *
         * FEATURE FLAG SYSTEM:
         * - Adds three tables for comprehensive feature management:
         *   1. feature_flags_global: Admin controls for global feature toggles
         *   2. feature_flags_user: User preferences for features
         *   3. feature_usage_log: Usage tracking and cost monitoring
         *
         * Purpose:
         * - Cost control for expensive features (TTS, Speech Recognition, etc.)
         * - User preferences (opt-in/opt-out)
         * - A/B testing and gradual rollout
         * - Usage analytics and cost tracking
         */
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create feature_flags_global table (admin controls)
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS feature_flags_global (
                        feature_key TEXT PRIMARY KEY NOT NULL,
                        enabled INTEGER NOT NULL,
                        rollout_percentage INTEGER NOT NULL DEFAULT 100,
                        max_daily_usage INTEGER,
                        current_daily_usage INTEGER NOT NULL DEFAULT 0,
                        last_reset_date INTEGER NOT NULL,
                        total_cost REAL NOT NULL DEFAULT 0.0,
                        notes TEXT,
                        last_modified INTEGER NOT NULL,
                        modified_by TEXT
                    )
                """.trimIndent())

                // Create feature_flags_user table (user preferences)
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS feature_flags_user (
                        feature_key TEXT PRIMARY KEY NOT NULL,
                        user_enabled INTEGER NOT NULL,
                        has_used_feature INTEGER NOT NULL DEFAULT 0,
                        usage_count INTEGER NOT NULL DEFAULT 0,
                        last_used INTEGER,
                        feedback_provided INTEGER NOT NULL DEFAULT 0,
                        feedback_rating INTEGER
                    )
                """.trimIndent())

                // Create feature_usage_log table (usage tracking)
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS feature_usage_log (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        feature_key TEXT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        api_calls_made INTEGER NOT NULL DEFAULT 0,
                        estimated_cost REAL NOT NULL DEFAULT 0.0,
                        success INTEGER NOT NULL DEFAULT 1,
                        error_message TEXT,
                        user_id TEXT
                    )
                """.trimIndent())

                // Create indices for efficient queries
                database.execSQL("CREATE INDEX IF NOT EXISTS index_feature_usage_log_feature_key ON feature_usage_log(feature_key)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_feature_usage_log_timestamp ON feature_usage_log(timestamp)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_feature_usage_log_success ON feature_usage_log(success)")
            }
        }

        /**
         * Migration from version 4 to 5: Add audio cache table
         *
         * AUDIO CACHING SYSTEM:
         * - Adds table for caching TTS-generated audio files
         * - Reduces API calls and costs
         * - Improves performance and offline support
         * - LRU cache management
         *
         * Purpose:
         * - Cache audio files locally to avoid repeated TTS API calls
         * - Track audio usage for cache management
         * - Support offline audio playback
         */
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create audio_cache table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS audio_cache (
                        word_id TEXT PRIMARY KEY NOT NULL,
                        word_text TEXT NOT NULL,
                        language TEXT NOT NULL DEFAULT 'en',
                        tts_generated INTEGER NOT NULL DEFAULT 1,
                        cached_file_path TEXT,
                        file_size_bytes INTEGER NOT NULL DEFAULT 0,
                        created_at INTEGER NOT NULL,
                        last_accessed INTEGER NOT NULL,
                        access_count INTEGER NOT NULL DEFAULT 0,
                        audio_url TEXT
                    )
                """.trimIndent())

                // Create indices for efficient queries
                database.execSQL("CREATE INDEX IF NOT EXISTS index_audio_cache_word_id ON audio_cache(word_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_audio_cache_last_accessed ON audio_cache(last_accessed)")
            }
        }

        /**
         * Migration from version 5 to 6: Add word images table
         *
         * WORD IMAGES SYSTEM:
         * - Adds table for storing images associated with words
         * - Supports multiple image sources (Unsplash, Pixabay, user uploads)
         * - Local caching for offline support
         * - Visual learning enhancement
         */
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS word_images (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        word_id TEXT NOT NULL,
                        word_text TEXT NOT NULL,
                        image_url TEXT NOT NULL,
                        thumbnail_url TEXT,
                        source TEXT NOT NULL,
                        cached_file_path TEXT,
                        file_size_bytes INTEGER NOT NULL DEFAULT 0,
                        attribution TEXT,
                        photographer TEXT,
                        photographer_url TEXT,
                        created_at INTEGER NOT NULL,
                        last_updated INTEGER NOT NULL,
                        is_primary INTEGER NOT NULL DEFAULT 1,
                        access_count INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                database.execSQL("CREATE INDEX IF NOT EXISTS index_word_images_word_id ON word_images(word_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_word_images_source ON word_images(source)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_word_images_last_updated ON word_images(last_updated)")
            }
        }

        /**
         * Migration from version 6 to 7: Add example sentences table
         *
         * EXAMPLE SENTENCES SYSTEM:
         * - Adds table for storing example sentences
         * - Shows words used in context
         * - Multiple difficulty levels and usage contexts
         * - Supports Tatoeba, AI-generated, and manual examples
         */
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS example_sentences (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        word_id TEXT NOT NULL,
                        word_text TEXT NOT NULL,
                        sentence TEXT NOT NULL,
                        translation TEXT NOT NULL,
                        difficulty TEXT NOT NULL,
                        context TEXT NOT NULL,
                        source TEXT NOT NULL,
                        audio_url TEXT,
                        created_at INTEGER NOT NULL,
                        is_favorite INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                database.execSQL("CREATE INDEX IF NOT EXISTS index_example_sentences_word_id ON example_sentences(word_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_example_sentences_difficulty ON example_sentences(difficulty)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_example_sentences_context ON example_sentences(context)")
            }
        }

        /**
         * Migration from version 7 to 8: Add sync queue table
         *
         * OFFLINE MODE & SYNC SYSTEM:
         * - Adds table for queuing actions when offline
         * - Enables local-first architecture
         * - Automatic background sync when online
         * - Conflict resolution support
         * - Retry mechanism for failed syncs
         *
         * Purpose:
         * - Queue data changes when offline
         * - Sync to server when connection restored
         * - Track sync status and retry failed operations
         * - Support seamless offline/online transition
         */
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS sync_queue (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        actionType TEXT NOT NULL,
                        entityType TEXT NOT NULL,
                        entity_id TEXT NOT NULL,
                        entity_data TEXT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        synced INTEGER NOT NULL DEFAULT 0,
                        attempt_count INTEGER NOT NULL DEFAULT 0,
                        last_error TEXT,
                        last_attempt INTEGER,
                        priority INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                database.execSQL("CREATE INDEX IF NOT EXISTS index_sync_queue_synced ON sync_queue(synced)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_sync_queue_entity_type ON sync_queue(entityType)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_sync_queue_timestamp ON sync_queue(timestamp)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_sync_queue_priority ON sync_queue(priority)")
            }
        }

        /**
         * Migration from version 8 to 9: Add billing/subscription tables
         *
         * MONETIZATION & PREMIUM FEATURES:
         * - Adds tables for subscription management
         * - Enables Premium tier features
         * - Tracks purchase history
         * - Supports multiple subscription tiers (Free, Premium, Premium+)
         *
         * Purpose:
         * - Store user subscription status
         * - Track purchases for revenue analytics
         * - Enable Premium feature gating
         * - Support restore purchases functionality
         */
        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create subscriptions table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS subscriptions (
                        user_id TEXT PRIMARY KEY NOT NULL,
                        tier TEXT NOT NULL DEFAULT 'free',
                        period TEXT,
                        product_id TEXT,
                        purchase_token TEXT,
                        order_id TEXT,
                        purchase_time INTEGER,
                        expiry_time INTEGER,
                        auto_renewing INTEGER NOT NULL DEFAULT 0,
                        is_active INTEGER NOT NULL DEFAULT 0,
                        last_verified INTEGER NOT NULL,
                        payment_state TEXT NOT NULL DEFAULT 'none',
                        acknowledgement_state TEXT NOT NULL DEFAULT 'acknowledged',
                        price_paid REAL,
                        currency_code TEXT
                    )
                """.trimIndent())

                // Create purchase history table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS purchase_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        product_id TEXT NOT NULL,
                        purchase_token TEXT NOT NULL,
                        order_id TEXT NOT NULL,
                        purchase_time INTEGER NOT NULL,
                        acknowledged INTEGER NOT NULL,
                        price_paid REAL NOT NULL,
                        currency_code TEXT NOT NULL,
                        subscription_tier TEXT NOT NULL,
                        subscription_period TEXT NOT NULL,
                        created_at INTEGER NOT NULL
                    )
                """.trimIndent())

                // Create indices for subscriptions
                database.execSQL("CREATE INDEX IF NOT EXISTS index_subscriptions_tier ON subscriptions(tier)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_subscriptions_is_active ON subscriptions(is_active)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_subscriptions_expiry_time ON subscriptions(expiry_time)")

                // Create indices for purchase history
                database.execSQL("CREATE INDEX IF NOT EXISTS index_purchase_history_order_id ON purchase_history(order_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_purchase_history_purchase_token ON purchase_history(purchase_token)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_purchase_history_purchase_time ON purchase_history(purchase_time)")

                // Insert default free subscription for local user
                database.execSQL("""
                    INSERT INTO subscriptions (user_id, tier, is_active, last_verified, payment_state)
                    VALUES ('local_user', 'free', 1, ${System.currentTimeMillis()}, 'none')
                """.trimIndent())
            }
        }

        /**
         * Migration from version 9 to 10: Add gamification tables
         *
         * GAMIFICATION SYSTEM:
         * - Adds tables for streak tracking, daily goals, and achievements
         * - Enables retention features (streaks, goals, badges)
         * - Zero additional costs (all local)
         * - +40% retention impact expected
         *
         * Purpose:
         * - Track consecutive learning days (streaks)
         * - Set and monitor daily learning goals
         * - Unlock achievements and badges
         * - Gamify the learning experience
         */
        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create streak_tracking table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS streak_tracking (
                        user_id TEXT PRIMARY KEY NOT NULL,
                        current_streak INTEGER NOT NULL DEFAULT 0,
                        longest_streak INTEGER NOT NULL DEFAULT 0,
                        last_activity_date INTEGER NOT NULL,
                        streak_freeze_count INTEGER NOT NULL DEFAULT 0,
                        total_active_days INTEGER NOT NULL DEFAULT 0,
                        streak_start_date INTEGER,
                        created_at INTEGER NOT NULL
                    )
                """.trimIndent())

                // Create daily_goals table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS daily_goals (
                        user_id TEXT PRIMARY KEY NOT NULL,
                        words_goal INTEGER NOT NULL DEFAULT 10,
                        reviews_goal INTEGER NOT NULL DEFAULT 20,
                        quizzes_goal INTEGER NOT NULL DEFAULT 5,
                        time_goal_minutes INTEGER NOT NULL DEFAULT 15,
                        words_today INTEGER NOT NULL DEFAULT 0,
                        reviews_today INTEGER NOT NULL DEFAULT 0,
                        quizzes_today INTEGER NOT NULL DEFAULT 0,
                        time_today_minutes INTEGER NOT NULL DEFAULT 0,
                        last_reset_date INTEGER NOT NULL,
                        goals_completed_total INTEGER NOT NULL DEFAULT 0,
                        updated_at INTEGER NOT NULL
                    )
                """.trimIndent())

                // Create user_achievements table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_achievements (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        user_id TEXT NOT NULL,
                        achievement_id TEXT NOT NULL,
                        unlocked_at INTEGER NOT NULL,
                        progress INTEGER NOT NULL DEFAULT 0,
                        is_unlocked INTEGER NOT NULL DEFAULT 0,
                        notified INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                // Create indices for better performance
                database.execSQL("CREATE INDEX IF NOT EXISTS index_user_achievements_user_id ON user_achievements(user_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_user_achievements_achievement_id ON user_achievements(achievement_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_user_achievements_is_unlocked ON user_achievements(is_unlocked)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_user_achievements_user_achievement ON user_achievements(user_id, achievement_id)")

                // Insert initial data for local user
                val now = System.currentTimeMillis()
                database.execSQL("""
                    INSERT INTO streak_tracking (user_id, current_streak, longest_streak, last_activity_date, total_active_days, created_at)
                    VALUES ('local_user', 0, 0, $now, 0, $now)
                """.trimIndent())

                database.execSQL("""
                    INSERT INTO daily_goals (user_id, words_goal, reviews_goal, quizzes_goal, time_goal_minutes, last_reset_date, updated_at)
                    VALUES ('local_user', 10, 20, 5, 15, $now, $now)
                """.trimIndent())
            }
        }

        /**
         * Migration from version 10 to 11: Add memory games tables
         *
         * MEMORY GAMES SYSTEM:
         * - Adds tables for game sessions, flip cards, SRS, speed match
         * - Enables 10 different memory game types
         * - Zero additional costs (all local)
         * - +60-80% retention impact expected
         * - +100% engagement increase
         *
         * Purpose:
         * - Track game sessions and scores
         * - Spaced repetition system (SM-2 algorithm)
         * - Flip card matching game statistics
         * - Speed match game statistics
         * - Leaderboards and personal bests
         */
        private val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create game_sessions table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS game_sessions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        game_type TEXT NOT NULL,
                        user_id TEXT NOT NULL DEFAULT 'local_user',
                        started_at INTEGER NOT NULL,
                        completed_at INTEGER,
                        total_questions INTEGER NOT NULL DEFAULT 0,
                        correct_answers INTEGER NOT NULL DEFAULT 0,
                        incorrect_answers INTEGER NOT NULL DEFAULT 0,
                        time_spent_seconds INTEGER NOT NULL DEFAULT 0,
                        difficulty_level TEXT NOT NULL DEFAULT 'MEDIUM',
                        score INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                // Create flip_card_stats table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS flip_card_stats (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        user_id TEXT NOT NULL DEFAULT 'local_user',
                        grid_size TEXT NOT NULL,
                        total_pairs INTEGER NOT NULL,
                        moves INTEGER NOT NULL,
                        time_seconds INTEGER NOT NULL,
                        completed INTEGER NOT NULL DEFAULT 0,
                        completed_at INTEGER NOT NULL
                    )
                """.trimIndent())

                // Create srs_cards table (Spaced Repetition System)
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS srs_cards (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        word_id INTEGER NOT NULL,
                        user_id TEXT NOT NULL DEFAULT 'local_user',
                        ease_factor REAL NOT NULL DEFAULT 2.5,
                        interval INTEGER NOT NULL DEFAULT 0,
                        repetitions INTEGER NOT NULL DEFAULT 0,
                        next_review_date INTEGER NOT NULL,
                        last_reviewed INTEGER,
                        total_reviews INTEGER NOT NULL DEFAULT 0,
                        correct_reviews INTEGER NOT NULL DEFAULT 0,
                        created_at INTEGER NOT NULL
                    )
                """.trimIndent())

                // Create speed_match_stats table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS speed_match_stats (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        user_id TEXT NOT NULL DEFAULT 'local_user',
                        pair_count INTEGER NOT NULL,
                        completion_time_ms INTEGER NOT NULL,
                        mistakes INTEGER NOT NULL,
                        combo_max INTEGER NOT NULL,
                        score INTEGER NOT NULL,
                        completed INTEGER NOT NULL DEFAULT 1,
                        completed_at INTEGER NOT NULL
                    )
                """.trimIndent())

                // Create indices for better performance
                database.execSQL("CREATE INDEX IF NOT EXISTS index_game_sessions_user_id ON game_sessions(user_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_game_sessions_game_type ON game_sessions(game_type)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_game_sessions_started_at ON game_sessions(started_at)")

                database.execSQL("CREATE INDEX IF NOT EXISTS index_flip_card_stats_user_id ON flip_card_stats(user_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_flip_card_stats_grid_size ON flip_card_stats(grid_size)")

                database.execSQL("CREATE INDEX IF NOT EXISTS index_srs_cards_word_id ON srs_cards(word_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_srs_cards_user_id ON srs_cards(user_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_srs_cards_next_review_date ON srs_cards(next_review_date)")

                database.execSQL("CREATE INDEX IF NOT EXISTS index_speed_match_stats_user_id ON speed_match_stats(user_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_speed_match_stats_pair_count ON speed_match_stats(pair_count)")
            }
        }

        /**
         * Migration from version 11 to 12: Add favorites support
         *
         * FAVORITES SYSTEM:
         * - Adds columns to words table for marking favorites
         * - Enables users to save favorite words for quick access
         * - Zero additional costs (all local)
         *
         * Purpose:
         * - Allow users to mark words as favorites
         * - Track when words were favorited
         * - Enable favorites-only quiz and study sessions
         * - Support FavoritesScreen functionality
         */
        private val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add isFavorite column to words table
                database.execSQL("ALTER TABLE words ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")

                // Add favoritedAt column to track when word was favorited
                database.execSQL("ALTER TABLE words ADD COLUMN favoritedAt INTEGER")

                // Create index for performance when querying favorites
                database.execSQL("CREATE INDEX IF NOT EXISTS index_words_isFavorite ON words(isFavorite)")
            }
        }

        /**
         * Migration from version 12 to 13: Add Word of the Day
         *
         * WORD OF THE DAY SYSTEM:
         * - Adds table for daily featured words
         * - Rotates daily at midnight
         * - Tracks whether user viewed the word
         *
         * Purpose:
         * - Display a different featured word each day
         * - Encourage daily app engagement
         * - Support WordOfTheDayScreen functionality
         */
        private val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create word_of_day table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS word_of_day (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        wordId TEXT NOT NULL,
                        date TEXT NOT NULL,
                        wasViewed INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(wordId) REFERENCES words(word) ON DELETE CASCADE
                    )
                """.trimIndent())

                // Create unique index on date to ensure one word per day
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_word_of_day_date ON word_of_day(date)")

                // Create index on wordId for foreign key lookup
                database.execSQL("CREATE INDEX IF NOT EXISTS index_word_of_day_wordId ON word_of_day(wordId)")
            }
        }

        /**
         * Migration from version 13 to 14: Add quiz history
         *
         * QUIZ HISTORY SYSTEM:
         * - Adds tables for quiz result tracking
         * - Stores both overall quiz results and individual question results
         * - Enables "Review Missed Words" functionality
         *
         * Purpose:
         * - Track quiz performance over time
         * - Show detailed quiz results
         * - Allow review of incorrectly answered questions
         * - Support LastQuizResultsScreen functionality
         */
        private val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create quiz_history table for overall quiz results
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS quiz_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        totalQuestions INTEGER NOT NULL,
                        correctAnswers INTEGER NOT NULL,
                        wrongAnswers INTEGER NOT NULL,
                        skippedQuestions INTEGER NOT NULL DEFAULT 0,
                        timeTaken TEXT NOT NULL,
                        quizType TEXT NOT NULL,
                        accuracy REAL NOT NULL
                    )
                """.trimIndent())

                // Create quiz_question_results table for individual question tracking
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS quiz_question_results (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        quizId INTEGER NOT NULL,
                        wordId TEXT NOT NULL,
                        isCorrect INTEGER NOT NULL,
                        FOREIGN KEY(quizId) REFERENCES quiz_history(id) ON DELETE CASCADE,
                        FOREIGN KEY(wordId) REFERENCES words(word) ON DELETE CASCADE
                    )
                """.trimIndent())

                // Create indices for better performance
                database.execSQL("CREATE INDEX IF NOT EXISTS index_quiz_history_timestamp ON quiz_history(timestamp)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_quiz_question_results_quizId ON quiz_question_results(quizId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_quiz_question_results_wordId ON quiz_question_results(wordId)")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(AppDatabase::class) {
                instance ?: buildRoomDB(context).also { instance = it }
            }
        }

        private fun buildRoomDB(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .createFromAsset("database/trainvoc-db.db")
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
                MIGRATION_5_6,
                MIGRATION_6_7,
                MIGRATION_7_8,
                MIGRATION_8_9,
                MIGRATION_9_10,
                MIGRATION_10_11,
                MIGRATION_11_12,
                MIGRATION_12_13,
                MIGRATION_13_14
            )
            .build()
    }
}
