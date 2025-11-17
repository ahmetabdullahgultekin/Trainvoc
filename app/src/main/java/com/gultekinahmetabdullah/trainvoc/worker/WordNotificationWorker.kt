package com.gultekinahmetabdullah.trainvoc.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gultekinahmetabdullah.trainvoc.MainActivity
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlin.random.Random

class WordNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val db = AppDatabase.DatabaseBuilder.getInstance(applicationContext)
        val wordDao = db.wordDao()
        val statsDao = db.statisticDao()
        val words = wordDao.getAllWords().first()
        if (words.isEmpty()) return@withContext Result.success()

        val word = words[Random.nextInt(words.size)]
        val stat = word.statId.let { statsDao.getStatisticById(it.toLong()) }

        val (title, message) = if (stat != null && stat.learned) {
            applicationContext.getString(R.string.notification_learned_title) to
                applicationContext.getString(R.string.notification_learned_message, word.word)
        } else {
            applicationContext.getString(R.string.notification_new_title) to
                applicationContext.getString(R.string.notification_new_message, word.word)
        }

        sendNotification(title, message, word.word)
        Result.success()
    }

    private fun sendNotification(title: String, message: String, wordId: String) {
        val channelId = "word_reminder_channel"
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                applicationContext.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        // Intent to redirect to MainActivity and word detail when notification is clicked
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("wordId", wordId)
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, wordId.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
