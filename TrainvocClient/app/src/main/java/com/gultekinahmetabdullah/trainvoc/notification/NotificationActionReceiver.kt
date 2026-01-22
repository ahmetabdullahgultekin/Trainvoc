package com.gultekinahmetabdullah.trainvoc.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver that handles notification action button clicks.
 *
 * Actions:
 * - ACTION_I_KNOW: User claims to know the word (increment correct)
 * - ACTION_SHOW_ANSWER: Reveal the word meaning
 * - ACTION_SKIP: Skip this word and send another
 * - ACTION_GOT_IT: User confirmed they knew it after reveal
 * - ACTION_NEED_PRACTICE: User didn't know it after reveal
 * - ACTION_NEXT: Send next word notification
 */
class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_I_KNOW = "com.gultekinahmetabdullah.trainvoc.ACTION_I_KNOW"
        const val ACTION_SHOW_ANSWER = "com.gultekinahmetabdullah.trainvoc.ACTION_SHOW_ANSWER"
        const val ACTION_SKIP = "com.gultekinahmetabdullah.trainvoc.ACTION_SKIP"
        const val ACTION_GOT_IT = "com.gultekinahmetabdullah.trainvoc.ACTION_GOT_IT"
        const val ACTION_NEED_PRACTICE = "com.gultekinahmetabdullah.trainvoc.ACTION_NEED_PRACTICE"
        const val ACTION_NEXT = "com.gultekinahmetabdullah.trainvoc.ACTION_NEXT"

        const val EXTRA_WORD = "extra_word"
        const val EXTRA_MEANING = "extra_meaning"
        const val EXTRA_STAT_ID = "extra_stat_id"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"

        const val CHANNEL_ID = "word_quiz_channel"

        private const val FLAGS = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    }

    override fun onReceive(context: Context, intent: Intent) {
        val word = intent.getStringExtra(EXTRA_WORD) ?: ""
        val meaning = intent.getStringExtra(EXTRA_MEANING) ?: ""
        val statId = intent.getIntExtra(EXTRA_STAT_ID, -1)
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)

        when (intent.action) {
            ACTION_I_KNOW -> handleIKnow(context, word, statId, notificationId)
            ACTION_SHOW_ANSWER -> handleShowAnswer(context, word, meaning, statId, notificationId)
            ACTION_SKIP -> handleSkip(context, statId, notificationId)
            ACTION_GOT_IT -> handleGotIt(context, statId, notificationId)
            ACTION_NEED_PRACTICE -> handleNeedPractice(context, statId, notificationId)
            ACTION_NEXT -> handleNext(context, notificationId)
        }
    }

    /**
     * User clicked "I Know It" - they claim to know the word without seeing answer
     */
    private fun handleIKnow(context: Context, word: String, statId: Int, notificationId: Int) {
        if (statId > 0) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.DatabaseBuilder.getInstance(context)
                db.statisticDao().incrementCorrect(statId.toLong())

                // Check if word should be marked as learned (threshold: 5 correct)
                val stat = db.statisticDao().getStatisticById(statId.toLong())
                if (stat != null && stat.correctCount >= 5 && !stat.learned) {
                    db.statisticDao().markLearned(statId.toLong())
                }

                // Update last reviewed timestamp
                db.wordDao().updateLastReviewed(word, System.currentTimeMillis())
            }
        }

        // Show success feedback notification
        showFeedbackNotification(
            context,
            notificationId,
            context.getString(R.string.great_job),
            context.getString(R.string.word_marked_correct, word)
        )
    }

    /**
     * User clicked "Show Answer" - reveal the meaning with follow-up actions
     */
    private fun handleShowAnswer(
        context: Context,
        word: String,
        meaning: String,
        statId: Int,
        notificationId: Int
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create intents for follow-up actions
        val gotItIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_GOT_IT
            putExtra(EXTRA_WORD, word)
            putExtra(EXTRA_STAT_ID, statId)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }

        val needPracticeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_NEED_PRACTICE
            putExtra(EXTRA_WORD, word)
            putExtra(EXTRA_STAT_ID, statId)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }

        val nextIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_NEXT
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }

        // Build expanded notification with answer
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(word.uppercase())
            .setContentText(meaning)
            .setStyle(NotificationCompat.BigTextStyle().bigText(meaning))
            .addAction(
                R.drawable.ic_launcher_foreground,
                context.getString(R.string.got_it),
                PendingIntent.getBroadcast(context, 0, gotItIntent, FLAGS)
            )
            .addAction(
                R.drawable.ic_launcher_foreground,
                context.getString(R.string.need_practice),
                PendingIntent.getBroadcast(context, 1, needPracticeIntent, FLAGS)
            )
            .addAction(
                R.drawable.ic_launcher_foreground,
                context.getString(R.string.next_word),
                PendingIntent.getBroadcast(context, 2, nextIntent, FLAGS)
            )
            .setAutoCancel(false)
            .setOngoing(false)
            .build()

        manager.notify(notificationId, notification)
    }

    /**
     * User clicked "Skip" - skip this word and send another
     */
    private fun handleSkip(context: Context, statId: Int, notificationId: Int) {
        if (statId > 0) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.DatabaseBuilder.getInstance(context)
                db.statisticDao().incrementSkipped(statId.toLong())
            }
        }

        // Cancel current notification and trigger new one
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)

        // Trigger a new word notification
        NotificationHelper.sendWordQuizNotification(context)
    }

    /**
     * User clicked "Got It" after seeing answer - they knew it
     */
    private fun handleGotIt(context: Context, statId: Int, notificationId: Int) {
        if (statId > 0) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.DatabaseBuilder.getInstance(context)
                db.statisticDao().incrementCorrect(statId.toLong())

                // Check if word should be marked as learned
                val stat = db.statisticDao().getStatisticById(statId.toLong())
                if (stat != null && stat.correctCount >= 5 && !stat.learned) {
                    db.statisticDao().markLearned(statId.toLong())
                }
            }
        }

        // Dismiss notification
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)
    }

    /**
     * User clicked "Need Practice" after seeing answer - they didn't know it
     */
    private fun handleNeedPractice(context: Context, statId: Int, notificationId: Int) {
        if (statId > 0) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.DatabaseBuilder.getInstance(context)
                db.statisticDao().incrementWrong(statId.toLong())
            }
        }

        // Dismiss notification
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)
    }

    /**
     * User clicked "Next" - send another word notification
     */
    private fun handleNext(context: Context, notificationId: Int) {
        // Cancel current notification
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)

        // Trigger a new word notification
        NotificationHelper.sendWordQuizNotification(context)
    }

    /**
     * Show a brief feedback notification
     */
    private fun showFeedbackNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setTimeoutAfter(3000) // Auto-dismiss after 3 seconds
            .build()

        manager.notify(notificationId, notification)
    }
}
