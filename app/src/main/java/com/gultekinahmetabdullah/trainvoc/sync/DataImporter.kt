package com.gultekinahmetabdullah.trainvoc.sync

import android.content.Context
import androidx.room.withTransaction
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest

/**
 * Data importer for restoring user data from backups
 *
 * Supports multiple import formats:
 * - JSON: Full restore with all data and metadata
 * - CSV: Simple word list import
 *
 * Features:
 * - Data validation before import
 * - Conflict detection and resolution
 * - Progress callbacks for UI
 * - Rollback capability on failure
 * - Checksum verification for data integrity
 *
 * Usage:
 * ```kotlin
 * val importer = DataImporter(context, database)
 * val result = importer.importFromJson(
 *     filePath = "/path/to/backup.json",
 *     conflictStrategy = ConflictStrategy.MERGE_PREFER_REMOTE
 * )
 * when (result) {
 *     is RestoreResult.Success -> {
 *         println("Restored ${result.wordsRestored} words")
 *     }
 *     is RestoreResult.Failure -> {
 *         println("Restore failed: ${result.error}")
 *     }
 *     is RestoreResult.Conflict -> {
 *         // Handle conflicts
 *         println("Conflicts detected: ${result.conflicts.size}")
 *     }
 * }
 * ```
 */
class DataImporter(
    private val context: Context,
    private val database: AppDatabase
) {
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    /**
     * Import data from JSON backup file
     *
     * @param filePath Path to the JSON backup file
     * @param conflictStrategy Strategy for handling conflicts
     * @param validateChecksum Verify data integrity before import
     * @param onProgress Progress callback (0.0 to 1.0)
     * @return RestoreResult with success/failure/conflict status
     */
    suspend fun importFromJson(
        filePath: String,
        conflictStrategy: ConflictStrategy = ConflictStrategy.MERGE_PREFER_REMOTE,
        validateChecksum: Boolean = true,
        onProgress: ((Float) -> Unit)? = null
    ): RestoreResult = withContext(Dispatchers.IO) {
        try {
            onProgress?.invoke(0.1f)

            // Read and parse JSON file
            val file = File(filePath)
            if (!file.exists()) {
                return@withContext RestoreResult.Failure("Backup file not found")
            }

            val json = file.readText()
            val backupData = try {
                gson.fromJson(json, BackupData::class.java)
            } catch (e: JsonSyntaxException) {
                return@withContext RestoreResult.Failure("Invalid backup file format: ${e.message}")
            }

            onProgress?.invoke(0.2f)

            // Validate backup version
            if (backupData.version > BackupData.CURRENT_BACKUP_VERSION) {
                return@withContext RestoreResult.Failure(
                    "Backup version ${backupData.version} is newer than app version. " +
                            "Please update the app to restore this backup."
                )
            }

            // Verify checksum if requested
            if (validateChecksum) {
                val calculatedChecksum = calculateChecksum(backupData.words, backupData.statistics)
                if (calculatedChecksum != backupData.metadata.checksum) {
                    return@withContext RestoreResult.Failure(
                        "Backup file is corrupted. Checksum mismatch."
                    )
                }
            }

            onProgress?.invoke(0.3f)

            // Get existing data for conflict detection
            val existingWords = database.wordDao().getAllWords().first()
            val existingStatistics = database.statisticDao().getAllStatistics().first()

            onProgress?.invoke(0.4f)

            // Detect conflicts
            val conflicts = detectConflicts(
                existingWords = existingWords,
                existingStatistics = existingStatistics,
                backupWords = backupData.words,
                backupStatistics = backupData.statistics
            )

            onProgress?.invoke(0.5f)

            // Handle conflicts based on strategy
            when {
                conflicts.isNotEmpty() && conflictStrategy == ConflictStrategy.FAIL_ON_CONFLICT -> {
                    return@withContext RestoreResult.Conflict(
                        conflicts = conflicts,
                        backupData = backupData
                    )
                }

                else -> {
                    // Resolve conflicts and import
                    val resolvedData = resolveConflicts(
                        existingWords = existingWords,
                        existingStatistics = existingStatistics,
                        backupWords = backupData.words,
                        backupStatistics = backupData.statistics,
                        conflicts = conflicts,
                        strategy = conflictStrategy
                    )

                    onProgress?.invoke(0.7f)

                    // Import data (wrapped in transaction for rollback capability)
                    val (wordsImported, statsImported) = importData(
                        words = resolvedData.words,
                        statistics = resolvedData.statistics,
                        onProgress = { progress ->
                            onProgress?.invoke(0.7f + progress * 0.2f)
                        }
                    )

                    onProgress?.invoke(0.9f)

                    // Restore user preferences if included
                    if (backupData.userPreferences != null) {
                        restoreUserPreferences(backupData.userPreferences)
                    }

                    onProgress?.invoke(1.0f)

                    RestoreResult.Success(
                        wordsRestored = wordsImported,
                        statisticsRestored = statsImported,
                        conflictsResolved = conflicts.size
                    )
                }
            }
        } catch (e: Exception) {
            RestoreResult.Failure(
                error = "Failed to import JSON: ${e.message}",
                exception = e
            )
        }
    }

    /**
     * Import words from CSV file
     * Format: word,meaning,level,learned,correct_count,wrong_count
     *
     * @param filePath Path to the CSV file
     * @param conflictStrategy Strategy for handling existing words
     * @param onProgress Progress callback (0.0 to 1.0)
     * @return RestoreResult with import status
     */
    suspend fun importFromCsv(
        filePath: String,
        conflictStrategy: ConflictStrategy = ConflictStrategy.MERGE_PREFER_REMOTE,
        onProgress: ((Float) -> Unit)? = null
    ): RestoreResult = withContext(Dispatchers.IO) {
        try {
            onProgress?.invoke(0.1f)

            val file = File(filePath)
            if (!file.exists()) {
                return@withContext RestoreResult.Failure("CSV file not found")
            }

            val lines = file.readLines()
            if (lines.isEmpty()) {
                return@withContext RestoreResult.Failure("CSV file is empty")
            }

            onProgress?.invoke(0.2f)

            // Skip header
            val dataLines = lines.drop(1)
            val totalLines = dataLines.size

            val wordsToImport = mutableListOf<WordBackup>()
            val statisticsToImport = mutableListOf<StatisticBackup>()

            var lineNumber = 2 // Start from 2 (1 is header)
            for ((index, line) in dataLines.withIndex()) {
                try {
                    val csvRow = WordCsvRow.fromCsvLine(line)

                    // Create word backup
                    val wordBackup = WordBackup(
                        word = csvRow?.word ?: "",
                        meaning = csvRow?.meaning ?: "",
                        level = csvRow?.level,
                        lastReviewed = null,
                        statId = 0, // Will be generated
                        secondsSpent = 0
                    )
                    wordsToImport.add(wordBackup)

                    // Create statistic backup
                    val statisticBackup = StatisticBackup(
                        statId = 0, // Will be generated
                        learned = csvRow?.learned ?: false,
                        correctCount = csvRow?.correctCount ?: 0,
                        wrongCount = csvRow?.wrongCount ?: 0,
                        skippedCount = 0
                    )
                    statisticsToImport.add(statisticBackup)

                    onProgress?.invoke(0.2f + (index.toFloat() / totalLines) * 0.3f)
                } catch (e: Exception) {
                    return@withContext RestoreResult.Failure(
                        "Error parsing CSV line $lineNumber: ${e.message}"
                    )
                }
                lineNumber++
            }

            onProgress?.invoke(0.5f)

            // Get existing data for conflict detection
            val existingWords = database.wordDao().getAllWords().first()
            val existingStatistics = database.statisticDao().getAllStatistics().first()

            // Detect conflicts
            val conflicts = detectConflicts(
                existingWords = existingWords,
                existingStatistics = existingStatistics,
                backupWords = wordsToImport,
                backupStatistics = statisticsToImport
            )

            onProgress?.invoke(0.6f)

            // Resolve conflicts
            val resolvedData = resolveConflicts(
                existingWords = existingWords,
                existingStatistics = existingStatistics,
                backupWords = wordsToImport,
                backupStatistics = statisticsToImport,
                conflicts = conflicts,
                strategy = conflictStrategy
            )

            onProgress?.invoke(0.7f)

            // Import data
            val (wordsImported, statsImported) = importData(
                words = resolvedData.words,
                statistics = resolvedData.statistics,
                onProgress = { progress ->
                    onProgress?.invoke(0.7f + progress * 0.3f)
                }
            )

            onProgress?.invoke(1.0f)

            RestoreResult.Success(
                wordsRestored = wordsImported,
                statisticsRestored = statsImported,
                conflictsResolved = conflicts.size
            )
        } catch (e: Exception) {
            RestoreResult.Failure(
                error = "Failed to import CSV: ${e.message}",
                exception = e
            )
        }
    }

    /**
     * Validate backup file before import
     * Checks file format, version, and integrity
     *
     * @return ValidationResult with status and error details
     */
    suspend fun validateBackup(filePath: String): ValidationResult = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            if (!file.exists()) {
                return@withContext ValidationResult.Invalid("File not found")
            }

            // Check file extension
            when (file.extension.lowercase()) {
                "json" -> {
                    // Try to parse JSON
                    val json = file.readText()
                    val backupData = try {
                        gson.fromJson(json, BackupData::class.java)
                    } catch (e: JsonSyntaxException) {
                        return@withContext ValidationResult.Invalid("Invalid JSON format: ${e.message}")
                    }

                    // Check version compatibility
                    if (backupData.version > BackupData.CURRENT_BACKUP_VERSION) {
                        return@withContext ValidationResult.Invalid(
                            "Backup version ${backupData.version} is newer than app version"
                        )
                    }

                    // Verify checksum
                    val calculatedChecksum =
                        calculateChecksum(backupData.words, backupData.statistics)
                    if (calculatedChecksum != backupData.metadata.checksum) {
                        return@withContext ValidationResult.Invalid("Checksum verification failed")
                    }

                    ValidationResult.Valid(
                        wordCount = backupData.words.size,
                        statisticCount = backupData.statistics.size,
                        version = backupData.version
                    )
                }

                "csv" -> {
                    // Basic CSV validation
                    val lines = file.readLines()
                    if (lines.isEmpty()) {
                        return@withContext ValidationResult.Invalid("CSV file is empty")
                    }

                    if (!lines.first().startsWith("word,meaning,level")) {
                        return@withContext ValidationResult.Invalid("Invalid CSV header format")
                    }

                    ValidationResult.Valid(
                        wordCount = lines.size - 1, // Exclude header
                        statisticCount = 0,
                        version = 1
                    )
                }

                else -> ValidationResult.Invalid("Unsupported file format: ${file.extension}")
            }
        } catch (e: Exception) {
            ValidationResult.Invalid("Validation failed: ${e.message}")
        }
    }

    /**
     * Detect conflicts between existing data and backup data
     */
    private fun detectConflicts(
        existingWords: List<Word>,
        existingStatistics: List<Statistic>,
        backupWords: List<WordBackup>,
        backupStatistics: List<StatisticBackup>
    ): List<DataConflict> {
        val conflicts = mutableListOf<DataConflict>()

        // Check for word conflicts (same word, different meaning or level)
        for (backupWord in backupWords) {
            val existingWord = existingWords.find { it.word == backupWord.word }
            if (existingWord != null) {
                if (existingWord.meaning != backupWord.meaning ||
                    existingWord.level?.name != backupWord.level
                ) {
                    conflicts.add(
                        DataConflict.WordConflict(
                            word = backupWord.word,
                            localVersion = WordBackup.fromWord(existingWord),
                            remoteVersion = backupWord
                        )
                    )
                }
            }
        }

        return conflicts
    }

    /**
     * Resolve conflicts based on strategy
     */
    private fun resolveConflicts(
        existingWords: List<Word>,
        existingStatistics: List<Statistic>,
        backupWords: List<WordBackup>,
        backupStatistics: List<StatisticBackup>,
        conflicts: List<DataConflict>,
        strategy: ConflictStrategy
    ): ResolvedData {
        val resolvedWords = mutableListOf<WordBackup>()
        val resolvedStatistics = mutableListOf<StatisticBackup>()

        when (strategy) {
            ConflictStrategy.REPLACE_ALL -> {
                // Replace all existing data with backup data
                resolvedWords.addAll(backupWords)
                resolvedStatistics.addAll(backupStatistics)
            }

            ConflictStrategy.MERGE_PREFER_LOCAL -> {
                // Keep local version for conflicts, add new items
                val existingWordTexts = existingWords.map { it.word }.toSet()

                // Add all existing words (converted to backup format)
                resolvedWords.addAll(existingWords.map { WordBackup.fromWord(it) })
                resolvedStatistics.addAll(existingStatistics.map { StatisticBackup.fromStatistic(it) })

                // Add new words from backup (not in existing)
                backupWords.forEach { backupWord ->
                    if (backupWord.word !in existingWordTexts) {
                        resolvedWords.add(backupWord)
                    }
                }

                // Add corresponding statistics
                backupStatistics.forEach { backupStat ->
                    val correspondingWord = backupWords.find { it.statId == backupStat.statId }
                    if (correspondingWord != null && correspondingWord.word !in existingWordTexts) {
                        resolvedStatistics.add(backupStat)
                    }
                }
            }

            ConflictStrategy.MERGE_PREFER_REMOTE -> {
                // Use remote version for conflicts, keep local-only items
                val backupWordTexts = backupWords.map { it.word }.toSet()

                // Add all backup words
                resolvedWords.addAll(backupWords)
                resolvedStatistics.addAll(backupStatistics)

                // Add local-only words (not in backup)
                existingWords.forEach { existingWord ->
                    if (existingWord.word !in backupWordTexts) {
                        resolvedWords.add(WordBackup.fromWord(existingWord))
                    }
                }

                // Add corresponding statistics
                existingStatistics.forEach { existingStat ->
                    val correspondingWord = existingWords.find { it.statId == existingStat.statId }
                    if (correspondingWord != null && correspondingWord.word !in backupWordTexts) {
                        resolvedStatistics.add(StatisticBackup.fromStatistic(existingStat))
                    }
                }
            }

            ConflictStrategy.MERGE_SMART -> {
                // Smart merge: prefer most recent or most complete data
                val allWordTexts =
                    (existingWords.map { it.word } + backupWords.map { it.word }).toSet()

                allWordTexts.forEach { wordText ->
                    val localWord = existingWords.find { it.word == wordText }
                    val remoteWord = backupWords.find { it.word == wordText }

                    when {
                        localWord == null && remoteWord != null -> {
                            // Only in backup, use backup version
                            resolvedWords.add(remoteWord)
                            val remoteStat =
                                backupStatistics.find { it.statId == remoteWord.statId }
                            if (remoteStat != null) {
                                resolvedStatistics.add(remoteStat)
                            }
                        }

                        localWord != null && remoteWord == null -> {
                            // Only in local, use local version
                            resolvedWords.add(WordBackup.fromWord(localWord))
                            val localStat =
                                existingStatistics.find { it.statId == localWord.statId }
                            if (localStat != null) {
                                resolvedStatistics.add(StatisticBackup.fromStatistic(localStat))
                            }
                        }

                        localWord != null && remoteWord != null -> {
                            // In both, smart merge
                            val localStat =
                                existingStatistics.find { it.statId == localWord.statId }
                            val remoteStat =
                                backupStatistics.find { it.statId == remoteWord.statId }

                            // Prefer version with more learning progress
                            val useRemote = if (localStat != null && remoteStat != null) {
                                remoteStat.correctCount + remoteStat.wrongCount >
                                        localStat.correctCount + localStat.wrongCount
                            } else {
                                remoteStat != null
                            }

                            if (useRemote) {
                                resolvedWords.add(remoteWord)
                                if (remoteStat != null) {
                                    resolvedStatistics.add(remoteStat)
                                }
                            } else {
                                resolvedWords.add(WordBackup.fromWord(localWord))
                                if (localStat != null) {
                                    resolvedStatistics.add(StatisticBackup.fromStatistic(localStat))
                                }
                            }
                        }
                    }
                }
            }

            ConflictStrategy.FAIL_ON_CONFLICT -> {
                // Should not reach here, handled earlier
                throw IllegalStateException("FAIL_ON_CONFLICT should be handled before resolution")
            }
        }

        return ResolvedData(resolvedWords, resolvedStatistics)
    }

    /**
     * Import resolved data into database
     * Uses transaction for atomicity and rollback capability
     */
    private suspend fun importData(
        words: List<WordBackup>,
        statistics: List<StatisticBackup>,
        onProgress: ((Float) -> Unit)? = null
    ): Pair<Int, Int> {
        return database.withTransaction {
            // First, create all statistics to get their IDs
            val statisticIdMap = mutableMapOf<Int, Int>()
            statistics.forEachIndexed { index, statBackup ->
                val statistic = statBackup.toStatistic()
                val statId = database.statisticDao().insertStatistic(statistic).toInt()
                statisticIdMap[statBackup.statId] = statId

                onProgress?.invoke((index.toFloat() / (words.size + statistics.size)) * 0.5f)
            }

            // Then import words with correct statId references
            var wordsImported = 0
            words.forEachIndexed { index, wordBackup ->
                val newStatId = statisticIdMap[wordBackup.statId] ?: wordBackup.statId
                val word = wordBackup.toWord().copy(statId = newStatId)
                database.wordDao().insertWord(word)
                wordsImported++

                onProgress?.invoke(0.5f + ((index.toFloat() / words.size) * 0.5f))
            }

            Pair(wordsImported, statistics.size)
        }
    }

    /**
     * Restore user preferences from backup
     */
    private fun restoreUserPreferences(preferences: UserPreferences) {
        val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().apply {
            preferences.username?.let { putString("username", it) }
            preferences.language?.let { putString("language", it) }
            preferences.theme?.let { putString("theme", it) }
            preferences.colorPalette?.let { putString("color_palette", it) }
            putBoolean("notifications", preferences.notificationsEnabled)
            putBoolean("daily_reminders_enabled", preferences.dailyRemindersEnabled)
            putBoolean("streak_alerts_enabled", preferences.streakAlertsEnabled)
            putBoolean("word_of_day_enabled", preferences.wordOfDayEnabled)
            preferences.notificationTime?.let { putString("notification_time", it) }
        }.apply()
    }

    /**
     * Calculate checksum for data integrity verification
     */
    private fun calculateChecksum(
        words: List<WordBackup>,
        statistics: List<StatisticBackup>
    ): String {
        val data = words.joinToString { it.word } + statistics.joinToString { it.statId.toString() }
        val digest = MessageDigest.getInstance("MD5")
        val hashBytes = digest.digest(data.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}

/**
 * Resolved data after conflict resolution
 */
private data class ResolvedData(
    val words: List<WordBackup>,
    val statistics: List<StatisticBackup>
)

/**
 * Validation result for backup files
 */
sealed class ValidationResult {
    data class Valid(
        val wordCount: Int,
        val statisticCount: Int,
        val version: Int
    ) : ValidationResult()

    data class Invalid(
        val reason: String
    ) : ValidationResult()
}

/**
 * Extension functions for easy importing
 */
suspend fun AppDatabase.importFromJson(
    context: Context,
    filePath: String,
    conflictStrategy: ConflictStrategy = ConflictStrategy.MERGE_PREFER_REMOTE
): RestoreResult {
    return DataImporter(context, this).importFromJson(filePath, conflictStrategy)
}

suspend fun AppDatabase.importFromCsv(
    context: Context,
    filePath: String,
    conflictStrategy: ConflictStrategy = ConflictStrategy.MERGE_PREFER_REMOTE
): RestoreResult {
    return DataImporter(context, this).importFromCsv(filePath, conflictStrategy)
}
