package com.gultekinahmetabdullah.trainvoc.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gultekinahmetabdullah.trainvoc.audio.AudioCache
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordExamCrossRef
import com.gultekinahmetabdullah.trainvoc.examples.ExampleSentence
import com.gultekinahmetabdullah.trainvoc.features.database.FeatureUsageLog
import com.gultekinahmetabdullah.trainvoc.features.database.GlobalFeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.database.UserFeatureFlag
import com.gultekinahmetabdullah.trainvoc.images.WordImage

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
        ExampleSentence::class
    ],
    version = 7
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

    object DatabaseBuilder {
        private const val DATABASE_NAME = "trainvoc-db"

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
                MIGRATION_6_7
            )
            .build()
    }
}
