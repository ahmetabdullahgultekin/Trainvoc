package com.gultekinahmetabdullah.trainvoc.sync

import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word

/**
 * Data models for backup and restore functionality
 *
 * These models are used for:
 * - Exporting user data to JSON/CSV
 * - Importing user data from external sources
 * - Cloud backup and sync
 * - Cross-device data transfer
 */

/**
 * Complete backup of all user data
 * Includes all words, statistics, progress, and metadata
 */
data class BackupData(
    val version: Int = CURRENT_BACKUP_VERSION,
    val timestamp: Long = System.currentTimeMillis(),
    val deviceId: String = "",
    val words: List<WordBackup>,
    val statistics: List<StatisticBackup>,
    val userPreferences: UserPreferences?,
    val metadata: BackupMetadata
) {
    companion object {
        const val CURRENT_BACKUP_VERSION = 1
    }
}

/**
 * Word data for backup
 * Simplified model for serialization
 */
data class WordBackup(
    val word: String,
    val meaning: String,
    val level: String?, // Stored as string for compatibility
    val lastReviewed: Long?,
    val statId: Int,
    val secondsSpent: Int
) {
    companion object {
        fun fromWord(word: Word): WordBackup {
            return WordBackup(
                word = word.word,
                meaning = word.meaning,
                level = word.level?.name,
                lastReviewed = word.lastReviewed,
                statId = word.statId,
                secondsSpent = word.secondsSpent
            )
        }
    }

    fun toWord(): Word {
        return Word(
            word = word,
            meaning = meaning,
            level = level?.let { WordLevel.valueOf(it) },
            lastReviewed = lastReviewed,
            statId = statId,
            secondsSpent = secondsSpent
        )
    }
}

/**
 * Statistic data for backup
 */
data class StatisticBackup(
    val statId: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val skippedCount: Int,
    val learned: Boolean
) {
    companion object {
        fun fromStatistic(stat: Statistic): StatisticBackup {
            return StatisticBackup(
                statId = stat.statId,
                correctCount = stat.correctCount,
                wrongCount = stat.wrongCount,
                skippedCount = stat.skippedCount,
                learned = stat.learned
            )
        }
    }

    fun toStatistic(): Statistic {
        return Statistic(
            statId = statId,
            correctCount = correctCount,
            wrongCount = wrongCount,
            skippedCount = skippedCount,
            learned = learned
        )
    }
}

/**
 * User preferences for backup
 * Includes theme, language, notification settings, etc.
 */
data class UserPreferences(
    val username: String?,
    val language: String?,
    val theme: String?,
    val colorPalette: String?,
    val notificationsEnabled: Boolean = true,
    val dailyRemindersEnabled: Boolean = true,
    val streakAlertsEnabled: Boolean = true,
    val wordOfDayEnabled: Boolean = true,
    val notificationTime: String? = null
)

/**
 * Metadata about the backup
 */
data class BackupMetadata(
    val appVersion: String,
    val backupDate: String,
    val totalWords: Int,
    val learnedWords: Int,
    val totalStatistics: Int,
    val checksum: String? = null // For data integrity verification
)

/**
 * Backup file information
 * Used for listing available backups
 */
data class BackupFileInfo(
    val fileName: String,
    val filePath: String,
    val timestamp: Long,
    val sizeBytes: Long,
    val version: Int,
    val wordCount: Int,
    val isCloudBackup: Boolean = false
)

/**
 * Sync status for cloud backup
 */
data class SyncStatus(
    val lastSyncTime: Long?,
    val isSyncing: Boolean = false,
    val syncError: String? = null,
    val pendingChanges: Int = 0,
    val cloudBackupEnabled: Boolean = false
)

/**
 * Import/Export format
 */
enum class BackupFormat {
    JSON,      // Full backup with all metadata
    CSV,       // Simple word list (word, meaning, level)
    SQL        // SQLite database file
}

/**
 * Backup operation result
 */
sealed class BackupResult {
    data class Success(
        val filePath: String,
        val wordCount: Int,
        val sizeBytes: Long
    ) : BackupResult()

    data class Failure(
        val error: String,
        val exception: Throwable? = null
    ) : BackupResult()
}

/**
 * Restore operation result
 */
sealed class RestoreResult {
    data class Success(
        val wordsRestored: Int,
        val statisticsRestored: Int,
        val conflictsResolved: Int = 0
    ) : RestoreResult()

    data class Failure(
        val error: String,
        val exception: Throwable? = null
    ) : RestoreResult()

    data class PartialSuccess(
        val wordsRestored: Int,
        val wordsFailed: Int,
        val errors: List<String>
    ) : RestoreResult()

    data class Conflict(
        val conflicts: List<DataConflict>,
        val backupData: BackupData
    ) : RestoreResult()
}

/**
 * CSV export format
 * Simple format for word list export
 */
data class WordCsvRow(
    val word: String,
    val meaning: String,
    val level: String,
    val learned: Boolean,
    val correctCount: Int,
    val wrongCount: Int
) {
    companion object {
        const val CSV_HEADER = "word,meaning,level,learned,correct_count,wrong_count"

        fun toCsvLine(row: WordCsvRow): String {
            return "${row.word},${row.meaning},${row.level},${row.learned},${row.correctCount},${row.wrongCount}"
        }

        fun fromCsvLine(line: String): WordCsvRow? {
            val parts = line.split(",")
            if (parts.size != 6) return null

            return try {
                WordCsvRow(
                    word = parts[0].trim(),
                    meaning = parts[1].trim(),
                    level = parts[2].trim(),
                    learned = parts[3].trim().toBoolean(),
                    correctCount = parts[4].trim().toInt(),
                    wrongCount = parts[5].trim().toInt()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

/**
 * Backup configuration
 */
data class BackupConfig(
    val autoBackupEnabled: Boolean = false,
    val autoBackupInterval: BackupInterval = BackupInterval.WEEKLY,
    val cloudBackupEnabled: Boolean = false,
    val backupLocation: BackupLocation = BackupLocation.LOCAL,
    val maxBackupCount: Int = 5, // Keep last 5 backups
    val includeStatistics: Boolean = true,
    val includePreferences: Boolean = true
)

enum class BackupInterval {
    DAILY,
    WEEKLY,
    MONTHLY
}

enum class BackupLocation {
    LOCAL,           // App internal storage
    EXTERNAL,        // SD card / Downloads
    GOOGLE_DRIVE,    // Google Drive
    CLOUD_STORAGE    // Generic cloud storage
}
