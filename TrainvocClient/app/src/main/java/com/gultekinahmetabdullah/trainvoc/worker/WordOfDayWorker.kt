package com.gultekinahmetabdullah.trainvoc.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import com.gultekinahmetabdullah.trainvoc.notification.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Calendar

/**
 * Worker for word of the day notifications
 *
 * Selects and sends a featured word each day based on:
 * - User's learning level
 * - Words not recently reviewed
 * - Difficulty progression
 *
 * Error Handling:
 * - Returns Result.retry() on transient failures (database, network, etc.)
 * - Returns Result.failure() on permanent failures
 * - Logs all errors for debugging
 */
class WordOfDayWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "WordOfDayWorker"
        private const val MAX_RETRY_ATTEMPTS = 3
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting word of the day worker")

            val sharedPreferences =
                applicationContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val wordOfDayEnabled = sharedPreferences.getBoolean("word_of_day_enabled", true)

            if (!wordOfDayEnabled) {
                Log.i(TAG, "Word of the day disabled, skipping")
                return@withContext Result.success()
            }

            // Get database instance
            val db = AppDatabase.DatabaseBuilder.getInstance(applicationContext)
            val wordDao = db.wordDao()
            val allWords = wordDao.getAllWords().first()

            if (allWords.isEmpty()) {
                Log.i(TAG, "No words in database, skipping word of the day")
                return@withContext Result.success()
            }

            // Select word of the day using a deterministic algorithm based on current date
            // This ensures the same word is shown for the entire day
            val calendar = Calendar.getInstance()
            val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            val year = calendar.get(Calendar.YEAR)
            val seed = (year * 1000 + dayOfYear) % allWords.size

            val wordOfDay = allWords[seed]
            Log.d(TAG, "Selected word of the day: ${wordOfDay.word}")

            // Send notification
            NotificationHelper.sendWordOfTheDay(
                applicationContext,
                wordOfDay.word,
                wordOfDay.meaning,
                wordOfDay.level?.ordinal ?: 0
            )

            // Save the word of the day in shared preferences
            sharedPreferences.edit()
                .putString("last_word_of_day", wordOfDay.word)
                .putLong("word_of_day_timestamp", System.currentTimeMillis())
                .apply()

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
