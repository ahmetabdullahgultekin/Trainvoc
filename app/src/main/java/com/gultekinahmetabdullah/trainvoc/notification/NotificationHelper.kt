package com.gultekinahmetabdullah.trainvoc.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.gultekinahmetabdullah.trainvoc.MainActivity
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Centralized notification management system
 *
 * Handles all notification operations including:
 * - Channel creation and management
 * - Daily reminder notifications
 * - Streak alert notifications
 * - Word of the day notifications
 * - Custom notification styling
 */
object NotificationHelper {

    // Notification Channel IDs
    const val CHANNEL_DAILY_REMINDER = "daily_reminder_channel"
    const val CHANNEL_STREAK_ALERT = "streak_alert_channel"
    const val CHANNEL_WORD_OF_DAY = "word_of_day_channel"
    const val CHANNEL_WORD_QUIZ = "word_quiz_channel"
    const val CHANNEL_GENERAL = "general_channel"

    // Notification IDs
    const val NOTIFICATION_ID_DAILY_REMINDER = 1001
    const val NOTIFICATION_ID_STREAK_ALERT = 1002
    const val NOTIFICATION_ID_WORD_OF_DAY = 1003

    private const val FLAGS = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

    /**
     * Initialize all notification channels
     * Should be called when the app starts
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Daily Reminder Channel
            val dailyReminderChannel = NotificationChannel(
                CHANNEL_DAILY_REMINDER,
                context.getString(R.string.channel_daily_reminder),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_daily_reminder_desc)
                enableVibration(true)
            }

            // Streak Alert Channel
            val streakAlertChannel = NotificationChannel(
                CHANNEL_STREAK_ALERT,
                context.getString(R.string.channel_streak_alert),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_streak_alert_desc)
                enableVibration(true)
                enableLights(true)
            }

            // Word of the Day Channel
            val wordOfDayChannel = NotificationChannel(
                CHANNEL_WORD_OF_DAY,
                context.getString(R.string.channel_word_of_day),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_word_of_day_desc)
            }

            // General Channel
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                context.getString(R.string.channel_general),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_general_desc)
            }

            // Word Quiz Channel
            val wordQuizChannel = NotificationChannel(
                CHANNEL_WORD_QUIZ,
                context.getString(R.string.channel_word_quiz),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_word_quiz_desc)
                enableVibration(true)
            }

            manager.createNotificationChannels(
                listOf(
                    dailyReminderChannel,
                    streakAlertChannel,
                    wordOfDayChannel,
                    generalChannel,
                    wordQuizChannel
                )
            )
        }
    }

    /**
     * Send a daily reminder notification
     */
    fun sendDailyReminder(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_DAILY_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_DAILY_REMINDER)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.notification_daily_title))
            .setContentText(context.getString(R.string.notification_daily_message))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(context.getString(R.string.notification_daily_message_expanded))
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID_DAILY_REMINDER, notification)
    }

    /**
     * Send a streak alert notification
     *
     * @param context Application context
     * @param streakDays Number of consecutive days
     * @param isEndangered True if streak is about to break
     */
    fun sendStreakAlert(context: Context, streakDays: Int, isEndangered: Boolean = false) {
        val (title, message) = if (isEndangered) {
            context.getString(R.string.notification_streak_danger_title) to
                    context.getString(R.string.notification_streak_danger_message, streakDays)
        } else {
            context.getString(R.string.notification_streak_milestone_title) to
                    context.getString(R.string.notification_streak_milestone_message, streakDays)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_STREAK_ALERT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_STREAK_ALERT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID_STREAK_ALERT, notification)
    }

    /**
     * Send word of the day notification
     *
     * @param context Application context
     * @param word The word to feature
     * @param meaning The meaning/translation
     * @param level Word difficulty level
     */
    fun sendWordOfTheDay(
        context: Context,
        word: String,
        meaning: String,
        level: Int
    ) {
        val levelLabel = getLevelLabel(context, level)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("wordId", word)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_WORD_OF_DAY,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_WORD_OF_DAY)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.notification_wotd_title))
            .setContentText("$word - $meaning")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        context.getString(
                            R.string.notification_wotd_message,
                            word,
                            meaning,
                            levelLabel
                        )
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID_WORD_OF_DAY, notification)
    }

    /**
     * Get human-readable level label
     */
    private fun getLevelLabel(context: Context, level: Int): String {
        return when (level) {
            0 -> context.getString(R.string.level_a1)
            1 -> context.getString(R.string.level_a2)
            2 -> context.getString(R.string.level_b1)
            3 -> context.getString(R.string.level_b2)
            4 -> context.getString(R.string.level_c1)
            5 -> context.getString(R.string.level_c2)
            else -> context.getString(R.string.unknown_level)
        }
    }

    /**
     * Cancel a specific notification
     */
    fun cancelNotification(context: Context, notificationId: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)
    }

    /**
     * Cancel all notifications
     */
    fun cancelAllNotifications(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancelAll()
    }

    /**
     * Send an interactive word quiz notification with action buttons
     *
     * This notification allows users to:
     * - Claim they know the word
     * - Request to see the answer
     * - Skip to the next word
     */
    fun sendWordQuizNotification(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.DatabaseBuilder.getInstance(context)
            val prefs = NotificationPreferences.getInstance(context)

            // Get enabled levels from preferences
            val enabledLevels = prefs.enabledLevels.toList()
            val includeLearned = prefs.includeLearnedWords

            // Get a random word based on filters
            val word = if (enabledLevels.isNotEmpty()) {
                db.wordDao().getRandomWordFromLevels(enabledLevels, includeLearned)
            } else {
                db.wordDao().getRandomWordForNotification(includeLearned)
            }

            if (word == null) return@launch

            val notificationId = System.currentTimeMillis().toInt()

            // Create action intents
            val iKnowIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = NotificationActionReceiver.ACTION_I_KNOW
                putExtra(NotificationActionReceiver.EXTRA_WORD, word.word)
                putExtra(NotificationActionReceiver.EXTRA_STAT_ID, word.statId)
                putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
            }

            val showAnswerIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = NotificationActionReceiver.ACTION_SHOW_ANSWER
                putExtra(NotificationActionReceiver.EXTRA_WORD, word.word)
                putExtra(NotificationActionReceiver.EXTRA_MEANING, word.meaning)
                putExtra(NotificationActionReceiver.EXTRA_STAT_ID, word.statId)
                putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
            }

            val skipIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = NotificationActionReceiver.ACTION_SKIP
                putExtra(NotificationActionReceiver.EXTRA_STAT_ID, word.statId)
                putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, notificationId)
            }

            // Build notification with action buttons
            val notification = NotificationCompat.Builder(context, CHANNEL_WORD_QUIZ)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getString(R.string.word_quiz_title))
                .setContentText(context.getString(R.string.word_quiz_question))
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.word_quiz_question) + "\n\n\"${word.word.uppercase()}\"")
                )
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    context.getString(R.string.i_know_it),
                    PendingIntent.getBroadcast(context, 0, iKnowIntent, FLAGS)
                )
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    context.getString(R.string.show_answer),
                    PendingIntent.getBroadcast(context, 1, showAnswerIntent, FLAGS)
                )
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    context.getString(R.string.skip),
                    PendingIntent.getBroadcast(context, 2, skipIntent, FLAGS)
                )
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(notificationId, notification)
        }
    }
}
