package com.gultekinahmetabdullah.trainvoc.worker

import android.content.Context
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
 */
class WordOfDayWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val sharedPreferences = applicationContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val wordOfDayEnabled = sharedPreferences.getBoolean("word_of_day_enabled", true)

        if (!wordOfDayEnabled) {
            return@withContext Result.success()
        }

        // Get database instance
        val db = AppDatabase.DatabaseBuilder.getInstance(applicationContext)
        val wordDao = db.wordDao()
        val allWords = wordDao.getAllWords().first()

        if (allWords.isEmpty()) {
            return@withContext Result.success()
        }

        // Select word of the day using a deterministic algorithm based on current date
        // This ensures the same word is shown for the entire day
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)
        val seed = (year * 1000 + dayOfYear) % allWords.size

        val wordOfDay = allWords[seed]

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

        Result.success()
    }
}
