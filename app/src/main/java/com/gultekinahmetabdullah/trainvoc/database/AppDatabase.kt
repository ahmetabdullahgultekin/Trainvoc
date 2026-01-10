package com.gultekinahmetabdullah.trainvoc.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordExamCrossRef
import com.gultekinahmetabdullah.trainvoc.features.database.FeatureUsageLog
import com.gultekinahmetabdullah.trainvoc.features.database.GlobalFeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.database.UserFeatureFlag

@Database(
    entities = [
        Word::class,
        Statistic::class,
        Exam::class,
        WordExamCrossRef::class,
        GlobalFeatureFlag::class,
        UserFeatureFlag::class,
        FeatureUsageLog::class
    ],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao
    abstract fun examDao(): ExamDao
    abstract fun wordExamCrossRefDao(): WordExamCrossRefDao
    abstract fun statisticDao(): StatisticDao
    abstract fun featureFlagDao(): com.gultekinahmetabdullah.trainvoc.features.database.FeatureFlagDao

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
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()
    }
}
