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
            "Hatırlıyor musun?" to "\"${word.word}\" kelimesini hatırlıyor musun?"
        } else {
            "Biliyor musun?" to "\"${word.word}\" kelimesinin anlamını biliyor musun?"
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
                "Kelime Hatırlatma",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        // Bildirime tıklanınca MainActivity'ye ve kelime id'sine yönlendiren intent
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
