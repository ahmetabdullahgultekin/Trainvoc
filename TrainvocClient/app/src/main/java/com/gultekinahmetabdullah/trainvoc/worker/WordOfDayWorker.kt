package com.gultekinahmetabdullah.trainvoc.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import com.gultekinahmetabdullah.trainvoc.notification.NotificationHelper
import com.gultekinahmetabdullah.trainvoc.notification.NotificationPreferences
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

/**
 * Worker for word of the day notifications
 *
 * Selects and sends a featured word each day based on:
 * - Deterministic date algorithm
 * - Single-row offset query (O(1) memory footprint)
 *
 * Error Handling:
 * - Returns Result.retry() on transient failures (database, network, etc.)
 * - Returns Result.failure() on permanent failures
 * - Logs all errors for debugging
 */
@HiltWorker
class WordOfDayWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val wordDao: WordDao,
    private val wordOfDayDao: com.gultekinahmetabdullah.trainvoc.features.WordOfDayDao,
    private val notificationPrefs: NotificationPreferences
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "WordOfDayWorker"
        private const val MAX_RETRY_ATTEMPTS = 3
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting word of the day worker")

            if (!notificationPrefs.wordOfDayEnabled) {
                Log.i(TAG, "Word of the day disabled, skipping")
                return@withContext Result.success()
            }

            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                .format(java.util.Date())

            // Check if WordOfDayDao already has an entry for today (shared with WordOfDayViewModel)
            val existingEntry = wordOfDayDao.getWordOfDay(today)
            val wordOfDay = if (existingEntry != null) {
                wordDao.getWordById(existingEntry.wordId)
            } else {
                val totalWords = wordDao.getWordCount()
                if (totalWords == 0) {
                    Log.i(TAG, "No words in database, skipping word of the day")
                    return@withContext Result.success()
                }
                val calendar = Calendar.getInstance()
                val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
                val year = calendar.get(Calendar.YEAR)
                val seed = (year * 1000 + dayOfYear) % totalWords
                wordDao.getWordAtOffset(seed)?.also { selected ->
                    wordOfDayDao.insertWordOfDay(
                        com.gultekinahmetabdullah.trainvoc.features.WordOfDay(
                            wordId = selected.id,
                            date = today,
                            wasViewed = false
                        )
                    )
                }
            }

            if (wordOfDay == null) {
                Log.w(TAG, "Could not fetch word of the day for $today")
                return@withContext Result.retry()
            }
            Log.d(TAG, "Selected word of the day: ${wordOfDay.word}")

            // Send notification
            NotificationHelper.sendWordOfTheDay(
                applicationContext,
                wordOfDay.word,
                wordOfDay.meaning,
                wordOfDay.level?.ordinal ?: 0
            )

            // Save the word of the day in preferences
            notificationPrefs.lastWordOfDay = wordOfDay.word
            notificationPrefs.wordOfDayTimestamp = System.currentTimeMillis()

            Log.d(TAG, "Word of the day notification sent successfully")
            Result.success()

        } catch (e: SecurityException) {
            // Notification permission denied - permanent failure
            Log.e(TAG, "Security exception: Notification permission may be denied", e)
            Result.failure()

        } catch (e: IllegalStateException) {
            // Invalid state (database closed, etc.) - permanent failure
            Log.e(TAG, "Illegal state exception in word of the day", e)
            Result.failure()

        } catch (e: Exception) {
            // Generic error (database query, etc.) - retry if under max attempts
            Log.e(TAG, "Error in word of the day worker", e)

            if (runAttemptCount < MAX_RETRY_ATTEMPTS) {
                Log.w(TAG, "Retrying word of the day (attempt ${runAttemptCount + 1}/$MAX_RETRY_ATTEMPTS)")
                Result.retry()
            } else {
                Log.e(TAG, "Max retry attempts reached, failing")
                Result.failure()
            }
        }
    }
}
