package com.gultekinahmetabdullah.trainvoc.database

import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Database optimization utilities for improved performance
 *
 * Features:
 * - Index management for faster queries
 * - VACUUM optimization for reduced file size
 * - ANALYZE for query planner optimization
 * - WAL mode for better concurrency
 */
object DatabaseOptimization {

    private const val TAG = "DatabaseOptimization"

    /**
     * Optimizes database performance by running VACUUM and ANALYZE
     *
     * VACUUM: Rebuilds the database file, repacking it into a minimal amount of disk space
     * ANALYZE: Gathers statistics about tables and indices to help the query optimizer
     *
     * Should be run periodically (e.g., once a week) when app is idle
     *
     * @param database The Room database instance
     * @return true if optimization succeeded, false otherwise
     */
    suspend fun optimizeDatabase(database: RoomDatabase): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Starting database optimization")

                database.openHelper.writableDatabase.apply {
                    // Run VACUUM to reclaim unused space and defragment
                    execSQL("VACUUM")
                    Log.d(TAG, "VACUUM completed")

                    // Run ANALYZE to update query planner statistics
                    execSQL("ANALYZE")
                    Log.d(TAG, "ANALYZE completed")
                }

                Log.d(TAG, "Database optimization completed successfully")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error during database optimization", e)
                false
            }
        }
    }

    /**
     * Creates indices on frequently queried columns for better performance
     *
     * Indices speed up SELECT queries but slightly slow down INSERT/UPDATE/DELETE
     * Only create indices on columns that are frequently used in WHERE clauses
     *
     * @param database The SupportSQLiteDatabase instance
     */
    fun createPerformanceIndices(database: SupportSQLiteDatabase) {
        try {
            Log.d(TAG, "Creating performance indices")

            // Index on Word.level for faster level-based queries
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_Word_level ON Word(level)"
            )

            // Index on Word.lastReviewed for sorting by review date
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_Word_lastReviewed ON Word(lastReviewed)"
            )

            // Index on Word.statId for faster joins with Statistics
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_Word_statId ON Word(statId)"
            )

            // Composite index on Statistic for learned word queries
            database.execSQL(
                """CREATE INDEX IF NOT EXISTS index_Statistic_learned_composite
                   ON Statistic(learned, correctCount, wrongCount)"""
            )

            // Index on Word.exam for exam-based filtering
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_Word_exam ON Word(exam)"
            )

            Log.d(TAG, "Performance indices created successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating performance indices", e)
        }
    }

    /**
     * Enables WAL (Write-Ahead Logging) mode for better concurrency
     *
     * WAL mode allows readers and writers to operate concurrently
     * Improves performance for apps with frequent reads and writes
     *
     * @param database The SupportSQLiteDatabase instance
     */
    fun enableWalMode(database: SupportSQLiteDatabase) {
        try {
            database.execSQL("PRAGMA journal_mode=WAL")
            Log.d(TAG, "WAL mode enabled")
        } catch (e: Exception) {
            Log.e(TAG, "Error enabling WAL mode", e)
        }
    }

    /**
     * Gets database file size in MB
     *
     * @param database The Room database instance
     * @return Database file size in MB, or -1 if error
     */
    suspend fun getDatabaseSize(database: RoomDatabase): Double {
        return withContext(Dispatchers.IO) {
            try {
                val dbPath = database.openHelper.readableDatabase.path
                if (dbPath != null) {
                    val dbFile = java.io.File(dbPath)
                    if (dbFile.exists()) {
                        val sizeInBytes = dbFile.length()
                        val sizeInMB = sizeInBytes / (1024.0 * 1024.0)
                        Log.d(TAG, "Database size: ${String.format("%.2f", sizeInMB)} MB")
                        return@withContext sizeInMB
                    }
                }
                -1.0
            } catch (e: Exception) {
                Log.e(TAG, "Error getting database size", e)
                -1.0
            }
        }
    }

    /**
     * Gets query statistics and performance metrics
     *
     * @param database The SupportSQLiteDatabase instance
     * @return Map of table names to row counts
     */
    fun getQueryStatistics(database: SupportSQLiteDatabase): Map<String, Int> {
        val stats = mutableMapOf<String, Int>()

        try {
            // Get row count for Word table
            database.query("SELECT COUNT(*) FROM Word").use { cursor ->
                if (cursor.moveToFirst()) {
                    stats["Word"] = cursor.getInt(0)
                }
            }

            // Get row count for Statistic table
            database.query("SELECT COUNT(*) FROM Statistic").use { cursor ->
                if (cursor.moveToFirst()) {
                    stats["Statistic"] = cursor.getInt(0)
                }
            }

            Log.d(TAG, "Query statistics: $stats")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting query statistics", e)
        }

        return stats
    }

    /**
     * Callback to be used in RoomDatabase.Builder for initial setup
     */
    val optimizationCallback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d(TAG, "Database created, applying optimizations")
            createPerformanceIndices(db)
            enableWalMode(db)
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Log.d(TAG, "Database opened")
            enableWalMode(db)
        }
    }
}

/**
 * Database maintenance scheduler
 *
 * Determines when to run database optimization based on usage patterns
 */
object DatabaseMaintenanceScheduler {

    private const val TAG = "DBMaintenanceScheduler"
    private const val PREF_LAST_OPTIMIZATION = "last_db_optimization"
    private const val OPTIMIZATION_INTERVAL_DAYS = 7

    /**
     * Checks if database optimization should run
     *
     * @param preferences SharedPreferences instance
     * @return true if optimization is due, false otherwise
     */
    fun shouldOptimize(preferences: android.content.SharedPreferences): Boolean {
        val lastOptimization = preferences.getLong(PREF_LAST_OPTIMIZATION, 0)
        val currentTime = System.currentTimeMillis()
        val daysSinceLastOptimization = (currentTime - lastOptimization) / (1000 * 60 * 60 * 24)

        return daysSinceLastOptimization >= OPTIMIZATION_INTERVAL_DAYS
    }

    /**
     * Records that database optimization was performed
     *
     * @param preferences SharedPreferences instance
     */
    fun recordOptimization(preferences: android.content.SharedPreferences) {
        preferences.edit()
            .putLong(PREF_LAST_OPTIMIZATION, System.currentTimeMillis())
            .apply()
        Log.d(TAG, "Database optimization recorded")
    }

    /**
     * Estimates if optimization would be beneficial
     *
     * @param wordCount Number of words in database
     * @param statisticCount Number of statistics in database
     * @return true if optimization is recommended
     */
    fun isOptimizationBeneficial(wordCount: Int, statisticCount: Int): Boolean {
        // Optimize if database has significant data
        // or if there's a large disparity between word and statistic counts
        return wordCount > 1000 ||
                statisticCount > 1000 ||
                (wordCount > 100 && kotlin.math.abs(wordCount - statisticCount) > 100)
    }
}
