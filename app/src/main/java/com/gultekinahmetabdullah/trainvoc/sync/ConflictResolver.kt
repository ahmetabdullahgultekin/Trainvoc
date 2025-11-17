package com.gultekinahmetabdullah.trainvoc.sync

import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic

/**
 * Conflict Resolution Utilities
 *
 * Provides advanced conflict resolution strategies for data sync operations.
 * Handles conflicts when the same data exists in both local and remote sources.
 *
 * Conflict Types:
 * 1. **Word Conflict**: Same word with different meanings or levels
 * 2. **Statistic Conflict**: Same word with different learning progress
 * 3. **Deletion Conflict**: Item deleted locally but modified remotely (or vice versa)
 *
 * Resolution Strategies:
 * - **REPLACE_ALL**: Replace all local data with remote (destructive)
 * - **MERGE_PREFER_LOCAL**: Keep local version for conflicts, add remote-only items
 * - **MERGE_PREFER_REMOTE**: Use remote version for conflicts, keep local-only items
 * - **MERGE_SMART**: Intelligent merge based on timestamps and data completeness
 * - **FAIL_ON_CONFLICT**: Report conflicts without resolving (manual resolution required)
 *
 * Usage:
 * ```kotlin
 * val resolver = ConflictResolver()
 *
 * // Detect conflicts
 * val conflicts = resolver.detectWordConflicts(localWords, remoteWords)
 *
 * // Resolve conflicts
 * val resolution = resolver.resolveConflicts(
 *     conflicts = conflicts,
 *     strategy = ConflictStrategy.MERGE_SMART
 * )
 *
 * // Apply resolution
 * when (resolution) {
 *     is ConflictResolution.Resolved -> {
 *         // Import resolved data
 *         database.insertWords(resolution.resolvedWords)
 *     }
 *     is ConflictResolution.RequiresManualResolution -> {
 *         // Show UI for manual resolution
 *         showConflictResolutionDialog(resolution.conflicts)
 *     }
 * }
 * ```
 */
class ConflictResolver {

    /**
     * Detect word conflicts between local and remote data
     *
     * A conflict occurs when:
     * - Same word exists in both sources
     * - BUT has different meaning, level, or other properties
     *
     * @param localWords Local word list
     * @param remoteWords Remote word list
     * @return List of detected conflicts
     */
    fun detectWordConflicts(
        localWords: List<Word>,
        remoteWords: List<WordBackup>
    ): List<DataConflict.WordConflict> {
        val conflicts = mutableListOf<DataConflict.WordConflict>()

        for (remoteWord in remoteWords) {
            val localWord = localWords.find { it.word == remoteWord.word }
            if (localWord != null) {
                // Check if there are actual differences
                if (hasWordDifferences(localWord, remoteWord)) {
                    conflicts.add(
                        DataConflict.WordConflict(
                            word = remoteWord.word,
                            localVersion = WordBackup.fromWord(localWord),
                            remoteVersion = remoteWord
                        )
                    )
                }
            }
        }

        return conflicts
    }

    /**
     * Detect statistic conflicts between local and remote data
     *
     * A conflict occurs when:
     * - Statistics for same word exist in both sources
     * - BUT have different progress (e.g., different correct/wrong counts)
     *
     * @param localStats Local statistics
     * @param remoteStats Remote statistics
     * @param localWords Local words (for word lookup)
     * @param remoteWords Remote words (for word lookup)
     * @return List of detected conflicts
     */
    fun detectStatisticConflicts(
        localStats: List<Statistic>,
        remoteStats: List<StatisticBackup>,
        localWords: List<Word>,
        remoteWords: List<WordBackup>
    ): List<DataConflict.StatisticConflict> {
        val conflicts = mutableListOf<DataConflict.StatisticConflict>()

        for (remoteStat in remoteStats) {
            // Find corresponding word
            val remoteWord = remoteWords.find { it.statId == remoteStat.statId }
            if (remoteWord != null) {
                val localWord = localWords.find { it.word == remoteWord.word }
                if (localWord != null) {
                    val localStat = localStats.find { it.statId == localWord.statId }
                    if (localStat != null && hasStatisticDifferences(localStat, remoteStat)) {
                        conflicts.add(
                            DataConflict.StatisticConflict(
                                word = remoteWord.word,
                                localVersion = StatisticBackup.fromStatistic(localStat),
                                remoteVersion = remoteStat
                            )
                        )
                    }
                }
            }
        }

        return conflicts
    }

    /**
     * Resolve conflicts using specified strategy
     *
     * @param conflicts List of conflicts to resolve
     * @param strategy Resolution strategy to use
     * @param localWords All local words
     * @param remoteWords All remote words
     * @param localStats All local statistics
     * @param remoteStats All remote statistics
     * @return ConflictResolution with resolved data or manual resolution request
     */
    fun resolveConflicts(
        conflicts: List<DataConflict>,
        strategy: ConflictStrategy,
        localWords: List<Word>,
        remoteWords: List<WordBackup>,
        localStats: List<Statistic>,
        remoteStats: List<StatisticBackup>
    ): ConflictResolution {
        return when (strategy) {
            ConflictStrategy.FAIL_ON_CONFLICT -> {
                if (conflicts.isNotEmpty()) {
                    ConflictResolution.RequiresManualResolution(conflicts)
                } else {
                    ConflictResolution.Resolved(
                        resolvedWords = remoteWords,
                        resolvedStatistics = remoteStats
                    )
                }
            }
            ConflictStrategy.REPLACE_ALL -> {
                // Simple: use all remote data
                ConflictResolution.Resolved(
                    resolvedWords = remoteWords,
                    resolvedStatistics = remoteStats
                )
            }
            ConflictStrategy.MERGE_PREFER_LOCAL -> {
                resolveMergePreferLocal(
                    localWords = localWords,
                    remoteWords = remoteWords,
                    localStats = localStats,
                    remoteStats = remoteStats,
                    conflicts = conflicts
                )
            }
            ConflictStrategy.MERGE_PREFER_REMOTE -> {
                resolveMergePreferRemote(
                    localWords = localWords,
                    remoteWords = remoteWords,
                    localStats = localStats,
                    remoteStats = remoteStats,
                    conflicts = conflicts
                )
            }
            ConflictStrategy.MERGE_SMART -> {
                resolveMergeSmart(
                    localWords = localWords,
                    remoteWords = remoteWords,
                    localStats = localStats,
                    remoteStats = remoteStats,
                    conflicts = conflicts
                )
            }
        }
    }

    /**
     * Merge strategy: Prefer local version for conflicts
     */
    private fun resolveMergePreferLocal(
        localWords: List<Word>,
        remoteWords: List<WordBackup>,
        localStats: List<Statistic>,
        remoteStats: List<StatisticBackup>,
        conflicts: List<DataConflict>
    ): ConflictResolution {
        val conflictWords = conflicts.filterIsInstance<DataConflict.WordConflict>()
            .map { it.word }
            .toSet()

        val resolvedWords = mutableListOf<WordBackup>()
        val resolvedStats = mutableListOf<StatisticBackup>()

        // Add all local words (converted to backup format)
        localWords.forEach { localWord ->
            resolvedWords.add(WordBackup.fromWord(localWord))
        }
        localStats.forEach { localStat ->
            resolvedStats.add(StatisticBackup.fromStatistic(localStat))
        }

        // Add remote-only words (not in local)
        val localWordTexts = localWords.map { it.word }.toSet()
        remoteWords.forEach { remoteWord ->
            if (remoteWord.word !in localWordTexts) {
                resolvedWords.add(remoteWord)

                // Add corresponding statistic
                val remoteStat = remoteStats.find { it.statId == remoteWord.statId }
                if (remoteStat != null) {
                    resolvedStats.add(remoteStat)
                }
            }
        }

        return ConflictResolution.Resolved(
            resolvedWords = resolvedWords,
            resolvedStatistics = resolvedStats
        )
    }

    /**
     * Merge strategy: Prefer remote version for conflicts
     */
    private fun resolveMergePreferRemote(
        localWords: List<Word>,
        remoteWords: List<WordBackup>,
        localStats: List<Statistic>,
        remoteStats: List<StatisticBackup>,
        conflicts: List<DataConflict>
    ): ConflictResolution {
        val resolvedWords = mutableListOf<WordBackup>()
        val resolvedStats = mutableListOf<StatisticBackup>()

        // Add all remote words
        resolvedWords.addAll(remoteWords)
        resolvedStats.addAll(remoteStats)

        // Add local-only words (not in remote)
        val remoteWordTexts = remoteWords.map { it.word }.toSet()
        localWords.forEach { localWord ->
            if (localWord.word !in remoteWordTexts) {
                resolvedWords.add(WordBackup.fromWord(localWord))

                // Add corresponding statistic
                val localStat = localStats.find { it.statId == localWord.statId }
                if (localStat != null) {
                    resolvedStats.add(StatisticBackup.fromStatistic(localStat))
                }
            }
        }

        return ConflictResolution.Resolved(
            resolvedWords = resolvedWords,
            resolvedStatistics = resolvedStats
        )
    }

    /**
     * Smart merge strategy: Intelligent resolution based on data quality
     *
     * Smart merge logic:
     * - For words: Prefer version with more complete data
     * - For statistics: Prefer version with more learning progress
     * - For timestamps: Use most recent version
     */
    private fun resolveMergeSmart(
        localWords: List<Word>,
        remoteWords: List<WordBackup>,
        localStats: List<Statistic>,
        remoteStats: List<StatisticBackup>,
        conflicts: List<DataConflict>
    ): ConflictResolution {
        val resolvedWords = mutableListOf<WordBackup>()
        val resolvedStats = mutableListOf<StatisticBackup>()

        val allWordTexts = (localWords.map { it.word } + remoteWords.map { it.word }).toSet()

        allWordTexts.forEach { wordText ->
            val localWord = localWords.find { it.word == wordText }
            val remoteWord = remoteWords.find { it.word == wordText }

            when {
                localWord == null && remoteWord != null -> {
                    // Only in remote, use remote version
                    resolvedWords.add(remoteWord)
                    val remoteStat = remoteStats.find { it.statId == remoteWord.statId }
                    if (remoteStat != null) {
                        resolvedStats.add(remoteStat)
                    }
                }
                localWord != null && remoteWord == null -> {
                    // Only in local, use local version
                    resolvedWords.add(WordBackup.fromWord(localWord))
                    val localStat = localStats.find { it.statId == localWord.statId }
                    if (localStat != null) {
                        resolvedStats.add(StatisticBackup.fromStatistic(localStat))
                    }
                }
                localWord != null && remoteWord != null -> {
                    // In both, smart resolve
                    val localStat = localStats.find { it.statId == localWord.statId }
                    val remoteStat = remoteStats.find { it.statId == remoteWord.statId }

                    // Determine which version to use
                    val useRemote = determineSmartChoice(
                        localWord = localWord,
                        remoteWord = remoteWord,
                        localStat = localStat,
                        remoteStat = remoteStat
                    )

                    if (useRemote) {
                        resolvedWords.add(remoteWord)
                        if (remoteStat != null) {
                            resolvedStats.add(remoteStat)
                        }
                    } else {
                        resolvedWords.add(WordBackup.fromWord(localWord))
                        if (localStat != null) {
                            resolvedStats.add(StatisticBackup.fromStatistic(localStat))
                        }
                    }
                }
            }
        }

        return ConflictResolution.Resolved(
            resolvedWords = resolvedWords,
            resolvedStatistics = resolvedStats
        )
    }

    /**
     * Determine which version to use for smart merge
     * Returns true if remote version should be used, false for local
     */
    private fun determineSmartChoice(
        localWord: Word,
        remoteWord: WordBackup,
        localStat: Statistic?,
        remoteStat: StatisticBackup?
    ): Boolean {
        // Priority 1: Prefer version with more learning progress
        if (localStat != null && remoteStat != null) {
            val localProgress = localStat.correctCount + localStat.wrongCount
            val remoteProgress = remoteStat.correctCount + remoteStat.wrongCount

            if (remoteProgress != localProgress) {
                return remoteProgress > localProgress
            }

            // If same total attempts, prefer higher correct count
            if (remoteStat.correctCount != localStat.correctCount) {
                return remoteStat.correctCount > localStat.correctCount
            }
        }

        // Priority 2: Prefer learned words over unlearned
        if (localStat != null && remoteStat != null) {
            if (remoteStat.learned != localStat.learned) {
                return remoteStat.learned
            }
        }

        // Priority 3: Prefer version with more recent review (from Word entity)
        if (localWord.lastReviewed != null && remoteWord.lastReviewed != null) {
            return remoteWord.lastReviewed!! > localWord.lastReviewed!!
        }

        // Priority 4: Prefer version with more complete data (longer meaning)
        if (remoteWord.meaning.length != localWord.meaning.length) {
            return remoteWord.meaning.length > localWord.meaning.length
        }

        // Default: prefer remote (arbitrary but consistent choice)
        return true
    }

    /**
     * Check if two words have meaningful differences
     */
    private fun hasWordDifferences(local: Word, remote: WordBackup): Boolean {
        return local.word != remote.word ||
               local.meaning != remote.meaning ||
               local.level?.name != remote.level
    }

    /**
     * Check if two statistics have meaningful differences
     */
    private fun hasStatisticDifferences(local: Statistic, remote: StatisticBackup): Boolean {
        return local.learned != remote.learned ||
               local.correctCount != remote.correctCount ||
               local.wrongCount != remote.wrongCount ||
               local.skippedCount != remote.skippedCount
    }

    /**
     * Generate conflict summary for UI display
     */
    fun generateConflictSummary(conflicts: List<DataConflict>): ConflictSummary {
        val wordConflicts = conflicts.filterIsInstance<DataConflict.WordConflict>()
        val statConflicts = conflicts.filterIsInstance<DataConflict.StatisticConflict>()

        return ConflictSummary(
            totalConflicts = conflicts.size,
            wordConflicts = wordConflicts.size,
            statisticConflicts = statConflicts.size,
            conflicts = conflicts
        )
    }

    /**
     * Merge statistics intelligently (max values for counts)
     * Useful for combining statistics from multiple sources
     */
    fun mergeStatistics(
        stat1: StatisticBackup,
        stat2: StatisticBackup
    ): StatisticBackup {
        return StatisticBackup(
            statId = stat1.statId,
            learned = stat1.learned || stat2.learned, // If learned in either, mark as learned
            correctCount = maxOf(stat1.correctCount, stat2.correctCount),
            wrongCount = maxOf(stat1.wrongCount, stat2.wrongCount),
            skippedCount = maxOf(stat1.skippedCount, stat2.skippedCount)
        )
    }

    /**
     * Helper: Get max of nullable longs
     */
    private fun maxOfNullable(a: Long?, b: Long?): Long? {
        return when {
            a == null && b == null -> null
            a == null -> b
            b == null -> a
            else -> maxOf(a, b)
        }
    }
}

/**
 * Conflict summary for UI display
 */
data class ConflictSummary(
    val totalConflicts: Int,
    val wordConflicts: Int,
    val statisticConflicts: Int,
    val conflicts: List<DataConflict>
)

/**
 * Conflict strategy
 */
enum class ConflictStrategy {
    /**
     * Replace all local data with remote data (destructive)
     * Use when: Starting fresh, trusting remote data completely
     */
    REPLACE_ALL,

    /**
     * Merge data, preferring local version for conflicts
     * Use when: Local data is more trusted
     */
    MERGE_PREFER_LOCAL,

    /**
     * Merge data, preferring remote version for conflicts
     * Use when: Remote data is more trusted (e.g., from more active device)
     */
    MERGE_PREFER_REMOTE,

    /**
     * Smart merge based on data quality and timestamps
     * Use when: Want intelligent automatic resolution
     * Logic:
     * - Prefer version with more learning progress
     * - Prefer version with more recent timestamps
     * - Prefer version with more complete data
     */
    MERGE_SMART,

    /**
     * Don't resolve conflicts, report them for manual resolution
     * Use when: User wants full control over conflict resolution
     */
    FAIL_ON_CONFLICT
}

/**
 * Data conflict types
 */
sealed class DataConflict {
    /**
     * Word conflict: Same word with different properties
     */
    data class WordConflict(
        val word: String,
        val localVersion: WordBackup,
        val remoteVersion: WordBackup
    ) : DataConflict()

    /**
     * Statistic conflict: Same word with different learning progress
     */
    data class StatisticConflict(
        val word: String,
        val localVersion: StatisticBackup,
        val remoteVersion: StatisticBackup
    ) : DataConflict()
}

/**
 * Conflict resolution result
 */
sealed class ConflictResolution {
    /**
     * Conflicts resolved automatically
     */
    data class Resolved(
        val resolvedWords: List<WordBackup>,
        val resolvedStatistics: List<StatisticBackup>
    ) : ConflictResolution()

    /**
     * Conflicts require manual resolution
     */
    data class RequiresManualResolution(
        val conflicts: List<DataConflict>
    ) : ConflictResolution()
}
