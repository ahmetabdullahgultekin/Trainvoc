package com.gultekinahmetabdullah.trainvoc.sync

import android.content.Context
import android.os.Build
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.gultekinahmetabdullah.trainvoc.BuildConfig
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import com.gultekinahmetabdullah.trainvoc.security.EncryptionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data exporter for backing up user data
 *
 * Supports multiple export formats:
 * - JSON: Full backup with all data and metadata
 * - CSV: Simple word list for spreadsheet import
 * - SQL: Raw database file (not implemented yet)
 *
 * Usage:
 * ```kotlin
 * val exporter = DataExporter(context, database)
 * val result = exporter.exportToJson()
 * when (result) {
 *     is BackupResult.Success -> {
 *         println("Backup saved to: ${result.filePath}")
 *     }
 *     is BackupResult.Failure -> {
 *         println("Backup failed: ${result.error}")
 *     }
 * }
 * ```
 */
class DataExporter(
    private val context: Context,
    private val database: AppDatabase
) {
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    /**
     * Export data to JSON format
     * Creates a comprehensive backup with all data and metadata
     *
     * @param includeStatistics Include word statistics in backup
     * @param includePreferences Include user preferences in backup
     * @param encrypt Encrypt the backup file using AES-256-GCM (default: true for security)
     * @return BackupResult with file path or error
     */
    suspend fun exportToJson(
        includeStatistics: Boolean = true,
        includePreferences: Boolean = true,
        encrypt: Boolean = true
    ): BackupResult = withContext(Dispatchers.IO) {
        try {
            // Fetch all data
            val words = database.wordDao().getAllWords().first()
            val statistics = if (includeStatistics) {
                database.statisticDao().getAllStatistics().first()
            } else {
                emptyList()
            }

            // Convert to backup format
            val wordBackups = words.map { WordBackup.fromWord(it) }
            val statisticBackups = statistics.map { StatisticBackup.fromStatistic(it) }

            // Get user preferences
            val preferences = if (includePreferences) {
                getUserPreferences()
            } else {
                null
            }

            // Create backup metadata
            val metadata = BackupMetadata(
                appVersion = BuildConfig.VERSION_NAME,
                backupDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()),
                totalWords = words.size,
                learnedWords = words.count { word ->
                    statistics.find { it.statId == word.statId }?.learned == true
                },
                totalStatistics = statistics.size,
                checksum = calculateChecksum(wordBackups, statisticBackups)
            )

            // Create backup data object
            val backupData = BackupData(
                version = BackupData.CURRENT_BACKUP_VERSION,
                timestamp = System.currentTimeMillis(),
                deviceId = getDeviceId(),
                words = wordBackups,
                statistics = statisticBackups,
                userPreferences = preferences,
                metadata = metadata
            )

            // Convert to JSON
            val json = gson.toJson(backupData)

            // Save to file
            val tempFile = createBackupFile("trainvoc_backup", "json")
            tempFile.writeText(json)

            // Encrypt if requested
            val finalFile = if (encrypt) {
                val encryptedFile = createBackupFile("trainvoc_backup", "enc")
                val encryptionHelper = EncryptionHelper(context)

                val encryptionSuccess = encryptionHelper.encryptFile(tempFile, encryptedFile)

                if (encryptionSuccess) {
                    // Delete unencrypted temp file
                    tempFile.delete()
                    encryptedFile
                } else {
                    // Encryption failed, return unencrypted file with warning
                    // In production, you might want to fail completely instead
                    tempFile
                }
            } else {
                tempFile
            }

            BackupResult.Success(
                filePath = finalFile.absolutePath,
                wordCount = words.size,
                sizeBytes = finalFile.length(),
                encrypted = encrypt && finalFile.extension == "enc"
            )
        } catch (e: Exception) {
            BackupResult.Failure(
                error = "Failed to export JSON: ${e.message}",
                exception = e
            )
        }
    }

    /**
     * Export data to CSV format
     * Creates a simple word list suitable for spreadsheet applications
     *
     * Format: word,meaning,level,learned,correct_count,wrong_count
     *
     * @return BackupResult with file path or error
     */
    suspend fun exportToCsv(): BackupResult = withContext(Dispatchers.IO) {
        try {
            val words = database.wordDao().getAllWords().first()
            val statistics = database.statisticDao().getAllStatistics().first()

            val file = createBackupFile("trainvoc_words", "csv")
            val writer = file.bufferedWriter()

            // Write header
            writer.write(WordCsvRow.CSV_HEADER)
            writer.newLine()

            // Write word rows
            words.forEach { word ->
                val stat = statistics.find { it.statId == word.statId }
                val csvRow = WordCsvRow(
                    word = word.word,
                    meaning = word.meaning,
                    level = word.level?.name ?: "UNKNOWN",
                    learned = stat?.learned ?: false,
                    correctCount = stat?.correctCount ?: 0,
                    wrongCount = stat?.wrongCount ?: 0
                )

                writer.write(WordCsvRow.toCsvLine(csvRow))
                writer.newLine()
            }

            writer.close()

            BackupResult.Success(
                filePath = file.absolutePath,
                wordCount = words.size,
                sizeBytes = file.length()
            )
        } catch (e: Exception) {
            BackupResult.Failure(
                error = "Failed to export CSV: ${e.message}",
                exception = e
            )
        }
    }

    /**
     * Export statistics summary to CSV
     * Useful for analyzing learning progress
     */
    suspend fun exportStatisticsCsv(): BackupResult = withContext(Dispatchers.IO) {
        try {
            val words = database.wordDao().getAllWords().first()
            val statistics = database.statisticDao().getAllStatistics().first()

            val file = createBackupFile("trainvoc_statistics", "csv")
            val writer = file.bufferedWriter()

            // Write header
            writer.write("word,level,correct,wrong,skipped,learned,success_rate")
            writer.newLine()

            // Write statistics
            words.forEach { word ->
                val stat = statistics.find { it.statId == word.statId }
                if (stat != null) {
                    val total = stat.correctCount + stat.wrongCount
                    val successRate = if (total > 0) {
                        (stat.correctCount.toFloat() / total * 100).toInt()
                    } else {
                        0
                    }

                    writer.write(
                        "${word.word},${word.level?.name ?: "UNKNOWN"}," +
                                "${stat.correctCount},${stat.wrongCount},${stat.skippedCount}," +
                                "${stat.learned},$successRate%"
                    )
                    writer.newLine()
                }
            }

            writer.close()

            BackupResult.Success(
                filePath = file.absolutePath,
                wordCount = words.size,
                sizeBytes = file.length()
            )
        } catch (e: Exception) {
            BackupResult.Failure(
                error = "Failed to export statistics: ${e.message}",
                exception = e
            )
        }
    }

    /**
     * Get list of available backup files
     */
    fun getAvailableBackups(): List<BackupFileInfo> {
        val backupDir = getBackupDirectory()
        return backupDir.listFiles()
            ?.filter { it.extension == "json" || it.extension == "csv" || it.extension == "enc" }
            ?.map { file ->
                // Parse metadata from JSON files
                val (version, wordCount) = if (file.extension == "json") {
                    parseBackupMetadata(file)
                } else {
                    Pair(1, 0) // CSV and encrypted files don't have embedded metadata
                }

                BackupFileInfo(
                    fileName = file.name,
                    filePath = file.absolutePath,
                    timestamp = file.lastModified(),
                    sizeBytes = file.length(),
                    version = version,
                    wordCount = wordCount,
                    isCloudBackup = false
                )
            }
            ?.sortedByDescending { it.timestamp }
            ?: emptyList()
    }

    /**
     * Parse backup metadata from JSON file
     * @return Pair of (version, wordCount)
     */
    private fun parseBackupMetadata(file: File): Pair<Int, Int> {
        return try {
            val json = file.readText()
            val backupData = gson.fromJson(json, BackupData::class.java)
            Pair(backupData.version, backupData.metadata.totalWords)
        } catch (e: Exception) {
            // If parsing fails, return defaults
            Pair(1, 0)
        }
    }

    /**
     * Delete old backups, keeping only the specified number
     */
    fun cleanupOldBackups(keepCount: Int = 5) {
        val backups = getAvailableBackups()
        if (backups.size > keepCount) {
            backups.drop(keepCount).forEach { backup ->
                File(backup.filePath).delete()
            }
        }
    }

    /**
     * Get backup directory
     * Uses app-specific external storage (doesn't require permission on Android 10+)
     */
    private fun getBackupDirectory(): File {
        val backupDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ use app-specific external storage
            File(context.getExternalFilesDir(null), "backups")
        } else {
            // Pre-Android 10
            File(context.filesDir, "backups")
        }

        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }

        return backupDir
    }

    /**
     * Create backup file with timestamp
     */
    private fun createBackupFile(prefix: String, extension: String): File {
        val timestamp = dateFormat.format(Date())
        val fileName = "${prefix}_$timestamp.$extension"
        return File(getBackupDirectory(), fileName)
    }

    /**
     * Get user preferences from SharedPreferences
     */
    private fun getUserPreferences(): UserPreferences {
        val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return UserPreferences(
            username = sharedPrefs.getString("username", null),
            language = sharedPrefs.getString("language", null),
            theme = sharedPrefs.getString("theme", null),
            colorPalette = sharedPrefs.getString("color_palette", null),
            notificationsEnabled = sharedPrefs.getBoolean("notifications", true),
            dailyRemindersEnabled = sharedPrefs.getBoolean("daily_reminders_enabled", true),
            streakAlertsEnabled = sharedPrefs.getBoolean("streak_alerts_enabled", true),
            wordOfDayEnabled = sharedPrefs.getBoolean("word_of_day_enabled", true),
            notificationTime = sharedPrefs.getString("notification_time", null)
        )
    }

    /**
     * Calculate checksum for data integrity verification
     * Uses SHA-256 for cryptographically secure hashing
     */
    private fun calculateChecksum(
        words: List<WordBackup>,
        statistics: List<StatisticBackup>
    ): String {
        val data = words.joinToString { it.word } + statistics.joinToString { it.statId.toString() }
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(data.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Get device identifier for sync
     */
    private fun getDeviceId(): String {
        // Use a combination of device info for unique ID
        // Note: Not truly unique, but sufficient for backup purposes
        return "${Build.MODEL}_${Build.MANUFACTURER}_${Build.VERSION.SDK_INT}"
    }
}

/**
 * Extension functions for easy exporting
 */
suspend fun AppDatabase.exportToJson(context: Context): BackupResult {
    return DataExporter(context, this).exportToJson()
}

suspend fun AppDatabase.exportToCsv(context: Context): BackupResult {
    return DataExporter(context, this).exportToCsv()
}
