package com.gultekinahmetabdullah.trainvoc.performance

import androidx.room.withTransaction
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase

/**
 * Database Query Optimization Utilities
 *
 * Best practices for Room database performance:
 * 1. Use indices on frequently queried columns
 * 2. Batch operations using transactions
 * 3. Use Flow for reactive queries
 * 4. Avoid N+1 query problems with @Relation
 * 5. Use LIMIT for large result sets
 * 6. Optimize JOIN queries
 * 7. Use @Transaction for consistency
 */

/**
 * Batch insert optimization
 * Wraps multiple inserts in a single transaction
 *
 * Performance gain: 10-100x faster than individual inserts
 *
 * Usage:
 * ```kotlin
 * batchInsert(database) {
 *     words.forEach { word ->
 *         wordDao().insertWord(word)
 *     }
 * }
 * ```
 */
suspend fun <R> batchInsert(
    database: AppDatabase,
    block: suspend () -> R
): R = database.withTransaction {
    block()
}

/**
 * Query optimization tips for WordDao:
 *
 * ✅ GOOD - Uses index on level:
 * SELECT * FROM words WHERE level = 'A1'
 *
 * ❌ BAD - Full table scan:
 * SELECT * FROM words WHERE UPPER(word) = 'HELLO'
 *
 * ✅ GOOD - Uses index with JOIN:
 * SELECT * FROM words w
 * JOIN statistics s ON w.stat_id = s.stat_id
 * WHERE s.learned = 1
 *
 * ❌ BAD - Correlated subquery (N+1 problem):
 * SELECT word, (SELECT COUNT(*) FROM statistics WHERE stat_id = w.stat_id)
 * FROM words w
 *
 * ✅ GOOD - Single query with JOIN:
 * SELECT w.word, COUNT(s.stat_id)
 * FROM words w
 * JOIN statistics s ON w.stat_id = s.stat_id
 * GROUP BY w.word
 */

/**
 * Database performance checklist:
 */
object DatabasePerformanceChecklist {
    /**
     * 1. INDICES ✅
     * - Words table: level, stat_id, last_reviewed
     * - Statistics table: learned, correct_count, wrong_count
     * - All foreign keys have indices
     *
     * 2. TRANSACTIONS ✅
     * - Batch inserts use transactions
     * - Multiple related operations wrapped in @Transaction
     *
     * 3. QUERY OPTIMIZATION
     * - Use LIMIT for pagination
     * - Avoid SELECT * when possible
     * - Use covering indices where applicable
     *
     * 4. FLOW vs SUSPEND
     * - Use Flow<> for UI that reacts to changes
     * - Use suspend for one-time queries
     *
     * 5. AVOID N+1 QUERIES
     * - Use @Relation for related data
     * - Use JOIN instead of multiple queries
     *
     * 6. PAGING
     * - Use Paging 3 library for large lists
     * - Load data incrementally
     */

    /**
     * Monitoring query performance
     */
    fun monitorQuery(queryName: String, durationMs: Long) {
        PerformanceMonitor.trackDatabaseQuery(queryName, durationMs)

        // Log slow queries
        if (durationMs > 50) {
            // Consider optimizing this query
            println("⚠️ Slow query detected: $queryName took ${durationMs}ms")
        }
    }
}

/**
 * Database optimization examples:
 *
 * EXAMPLE 1: Batch insert with transaction
 * ```kotlin
 * suspend fun insertMultipleWords(words: List<Word>) {
 *     batchInsert(database) {
 *         words.forEach { word ->
 *             wordDao().insertWord(word)
 *         }
 *     }
 * }
 * ```
 *
 * EXAMPLE 2: Efficient pagination
 * ```kotlin
 * @Query("SELECT * FROM words ORDER BY word ASC LIMIT :limit OFFSET :offset")
 * suspend fun getWordsPaged(limit: Int, offset: Int): List<Word>
 * ```
 *
 * EXAMPLE 3: Use @Relation instead of multiple queries
 * ```kotlin
 * data class WordWithStats(
 *     @Embedded val word: Word,
 *     @Relation(
 *         parentColumn = "stat_id",
 *         entityColumn = "stat_id"
 *     )
 *     val statistic: Statistic
 * )
 * ```
 *
 * EXAMPLE 4: Use Flow for reactive UI
 * ```kotlin
 * @Query("SELECT * FROM words WHERE learned = 1")
 * fun getLearnedWords(): Flow<List<Word>>
 * ```
 */

/**
 * Database migration best practices:
 *
 * 1. Always provide migrations - never fallbackToDestructiveMigration() in production
 * 2. Test migrations thoroughly
 * 3. Add indices in migrations, not just entity annotations
 * 4. Keep migrations simple and atomic
 * 5. Version your database incrementally
 *
 * Example migration with indices:
 * ```kotlin
 * val MIGRATION_2_3 = object : Migration(2, 3) {
 *     override fun migrate(database: SupportSQLiteDatabase) {
 *         // Add new column
 *         database.execSQL("ALTER TABLE words ADD COLUMN favorite INTEGER NOT NULL DEFAULT 0")
 *
 *         // Add index for new column
 *         database.execSQL("CREATE INDEX index_words_favorite ON words(favorite)")
 *     }
 * }
 * ```
 */

/**
 * Common performance pitfalls to avoid:
 *
 * 1. ❌ Querying on main thread
 *    Use suspend functions or Flow
 *
 * 2. ❌ Not using indices on filtered/sorted columns
 *    Add @Index to Entity annotations
 *
 * 3. ❌ SELECT * when you only need few columns
 *    Specify column names in SELECT
 *
 * 4. ❌ Multiple individual inserts without transaction
 *    Wrap in withTransaction()
 *
 * 5. ❌ Not using @Transaction for multi-table updates
 *    Ensures data consistency
 *
 * 6. ❌ Loading entire table into memory
 *    Use pagination with Paging 3
 *
 * 7. ❌ Blocking UI with database operations
 *    Always use coroutines with Dispatchers.IO
 */
