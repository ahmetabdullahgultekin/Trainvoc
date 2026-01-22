package com.gultekinahmetabdullah.trainvoc.gdpr

import android.content.Context
import android.util.Log
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import com.gultekinahmetabdullah.trainvoc.repository.PreferencesRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * GDPR Data Manager for user data privacy compliance
 *
 * Implements GDPR (General Data Protection Regulation) requirements:
 * - Right to Access (Article 15): Export all user data
 * - Right to Erasure (Article 17): Delete all user data
 * - Right to Data Portability (Article 20): Export data in machine-readable format
 *
 * Features:
 * - Complete data export in JSON format
 * - Secure data deletion with verification
 * - Data anonymization options
 * - Audit logging for compliance
 */
class GdprDataManager(
    private val context: Context,
    private val database: AppDatabase,
    private val preferencesRepository: PreferencesRepository
) {

    companion object {
        private const val TAG = "GdprDataManager"
        private const val EXPORT_DIRECTORY = "gdpr_exports"
        private const val DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss"
    }

    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .create()

    /**
     * Exports all user data in JSON format (GDPR Article 15 & 20)
     *
     * Includes:
     * - All words and their meanings
     * - Learning statistics
     * - Quiz history
     * - User preferences
     * - App settings
     *
     * @param includeMetadata Whether to include system metadata (timestamps, IDs)
     * @return GdprExportResult with file path or error
     */
    suspend fun exportUserData(includeMetadata: Boolean = true): GdprExportResult {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Starting GDPR data export")

                // Collect all user data
                val userData = collectUserData(includeMetadata)

                // Create export file
                val exportFile = createExportFile()

                // Write data to file
                exportFile.writeText(gson.toJson(userData))

                Log.d(TAG, "GDPR data export completed: ${exportFile.absolutePath}")

                GdprExportResult.Success(
                    filePath = exportFile.absolutePath,
                    sizeBytes = exportFile.length(),
                    timestamp = System.currentTimeMillis()
                )

            } catch (e: Exception) {
                Log.e(TAG, "Error during GDPR data export", e)
                GdprExportResult.Failure(
                    error = "Failed to export user data: ${e.message}",
                    exception = e
                )
            }
        }
    }

    /**
     * Deletes ALL user data from the app (GDPR Article 17: Right to Erasure)
     *
     * This operation:
     * - Deletes all words and statistics from database
     * - Clears all preferences and settings
     * - Removes cached files
     * - Cannot be undone
     *
     * @param confirmationToken Token to prevent accidental deletion
     * @return GdprDeletionResult with success status
     */
    suspend fun deleteAllUserData(confirmationToken: String): GdprDeletionResult {
        return withContext(Dispatchers.IO) {
            try {
                // Verify confirmation token
                if (confirmationToken != "DELETE_ALL_MY_DATA") {
                    return@withContext GdprDeletionResult.Failure(
                        error = "Invalid confirmation token. Data deletion cancelled."
                    )
                }

                Log.d(TAG, "Starting complete user data deletion")

                // Count data before deletion for verification
                val wordCount = database.wordDao().getAllWords().first().size
                val statCount = database.statisticDao().getAllStatistics().first().size

                // Delete all database records
                database.clearAllTables()

                // Clear all preferences
                preferencesRepository.clearAll()

                // Clear cache directory
                context.cacheDir.deleteRecursively()

                // Verify deletion
                val wordsAfter = database.wordDao().getAllWords().first().size
                val statsAfter = database.statisticDao().getAllStatistics().first().size

                if (wordsAfter == 0 && statsAfter == 0) {
                    Log.d(TAG, "User data deletion verified successful")
                    GdprDeletionResult.Success(
                        wordsDeleted = wordCount,
                        statisticsDeleted = statCount,
                        timestamp = System.currentTimeMillis()
                    )
                } else {
                    Log.e(TAG, "Data deletion verification failed")
                    GdprDeletionResult.Failure(
                        error = "Data deletion verification failed. Some data may remain."
                    )
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error during user data deletion", e)
                GdprDeletionResult.Failure(
                    error = "Failed to delete user data: ${e.message}",
                    exception = e
                )
            }
        }
    }

    /**
     * Anonymizes user data instead of deleting it
     *
     * Useful for apps that need to retain analytics but respect privacy
     * Replaces identifiable information with anonymous values
     *
     * @return Result with anonymization status
     */
    suspend fun anonymizeUserData(): GdprAnonymizationResult {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Starting data anonymization")

                // Clear identifiable preferences
                preferencesRepository.setUsername("Anonymous User")

                // Note: Words and statistics are not personally identifiable
                // so they don't need anonymization

                Log.d(TAG, "Data anonymization completed")

                GdprAnonymizationResult.Success(
                    timestamp = System.currentTimeMillis()
                )

            } catch (e: Exception) {
                Log.e(TAG, "Error during data anonymization", e)
                GdprAnonymizationResult.Failure(
                    error = "Failed to anonymize data: ${e.message}",
                    exception = e
                )
            }
        }
    }

    /**
     * Gets summary of all user data (for consent screens)
     *
     * @return DataSummary with counts and types
     */
    suspend fun getUserDataSummary(): DataSummary {
        return withContext(Dispatchers.IO) {
            try {
                val words = database.wordDao().getAllWords().first()
                val statistics = database.statisticDao().getAllStatistics().first().size

                DataSummary(
                    wordCount = words.size,
                    statisticCount = statistics,
                    hasPreferences = true,
                    hasLearningHistory = statistics > 0,
                    estimatedSizeKB = estimateDataSize(words.size, statistics)
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error getting data summary", e)
                DataSummary(0, 0, false, false, 0)
            }
        }
    }

    // Private helper methods

    private suspend fun collectUserData(includeMetadata: Boolean): UserDataExport {
        val words = database.wordDao().getAllWords().first()
        val statistics = database.statisticDao().getAllStatistics().first()

        return UserDataExport(
            exportDate = SimpleDateFormat(DATE_FORMAT, Locale.US).format(Date()),
            version = "1.0",
            words = words.map { word ->
                WordData(
                    word = word.word,
                    meaning = word.meaning,
                    level = word.level?.name,
                    lastReviewed = if (includeMetadata) word.lastReviewed else null,
                    secondsSpent = if (includeMetadata) word.secondsSpent else null
                )
            },
            statistics = statistics.map { stat ->
                StatisticData(
                    correctCount = stat.correctCount,
                    wrongCount = stat.wrongCount,
                    skippedCount = stat.skippedCount,
                    learned = stat.learned,
                    statId = if (includeMetadata) stat.statId else null
                )
            },
            preferences = UserPreferencesData(
                username = preferencesRepository.getUsername(),
                language = preferencesRepository.getLanguage().code
            )
        )
    }

    private fun createExportFile(): File {
        val exportDir = File(context.getExternalFilesDir(null), EXPORT_DIRECTORY)
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }

        val timestamp = SimpleDateFormat(DATE_FORMAT, Locale.US).format(Date())
        return File(exportDir, "user_data_export_$timestamp.json")
    }

    private fun estimateDataSize(wordCount: Int, statCount: Int): Int {
        // Rough estimate: each word ~500 bytes, each statistic ~100 bytes
        return ((wordCount * 500) + (statCount * 100)) / 1024
    }
}

// Data models for GDPR export

/**
 * Complete user data export structure
 */
data class UserDataExport(
    val exportDate: String,
    val version: String,
    val words: List<WordData>,
    val statistics: List<StatisticData>,
    val preferences: UserPreferencesData
)

data class WordData(
    val word: String,
    val meaning: String,
    val level: String?,
    val lastReviewed: Long?,
    val secondsSpent: Int?
)

data class StatisticData(
    val correctCount: Int,
    val wrongCount: Int,
    val skippedCount: Int,
    val learned: Boolean,
    val statId: Int?
)

data class UserPreferencesData(
    val username: String?,
    val language: String
)

data class DataSummary(
    val wordCount: Int,
    val statisticCount: Int,
    val hasPreferences: Boolean,
    val hasLearningHistory: Boolean,
    val estimatedSizeKB: Int
)

// Result classes

sealed class GdprExportResult {
    data class Success(
        val filePath: String,
        val sizeBytes: Long,
        val timestamp: Long
    ) : GdprExportResult()

    data class Failure(
        val error: String,
        val exception: Throwable? = null
    ) : GdprExportResult()
}

sealed class GdprDeletionResult {
    data class Success(
        val wordsDeleted: Int,
        val statisticsDeleted: Int,
        val timestamp: Long
    ) : GdprDeletionResult()

    data class Failure(
        val error: String,
        val exception: Throwable? = null
    ) : GdprDeletionResult()
}

sealed class GdprAnonymizationResult {
    data class Success(
        val timestamp: Long
    ) : GdprAnonymizationResult()

    data class Failure(
        val error: String,
        val exception: Throwable? = null
    ) : GdprAnonymizationResult()
}
