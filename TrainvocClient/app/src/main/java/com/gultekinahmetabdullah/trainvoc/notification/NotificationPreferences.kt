package com.gultekinahmetabdullah.trainvoc.notification

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages notification preferences
 *
 * Provides a centralized way to access and update notification settings.
 * All preferences are stored in SharedPreferences.
 */
class NotificationPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Daily Reminder Settings
    var dailyRemindersEnabled: Boolean
        get() = prefs.getBoolean("daily_reminders_enabled", true)
        set(value) {
            prefs.edit().putBoolean("daily_reminders_enabled", value).apply()
            if (value) {
                NotificationScheduler.scheduleDailyReminder(context)
            } else {
                NotificationScheduler.cancelDailyReminder(context)
            }
        }

    var reminderHour: Int
        get() = prefs.getInt("reminder_hour", 9) // Default 9 AM
        set(value) {
            prefs.edit().putInt("reminder_hour", value).apply()
            if (dailyRemindersEnabled) {
                NotificationScheduler.scheduleDailyReminder(context)
            }
        }

    // Streak Alert Settings
    var streakAlertsEnabled: Boolean
        get() = prefs.getBoolean("streak_alerts_enabled", true)
        set(value) {
            prefs.edit().putBoolean("streak_alerts_enabled", value).apply()
            if (value) {
                NotificationScheduler.scheduleStreakAlert(context)
            } else {
                NotificationScheduler.cancelStreakAlert(context)
            }
        }

    // Word of the Day Settings
    var wordOfDayEnabled: Boolean
        get() = prefs.getBoolean("word_of_day_enabled", true)
        set(value) {
            prefs.edit().putBoolean("word_of_day_enabled", value).apply()
            if (value) {
                NotificationScheduler.scheduleWordOfDay(context)
            } else {
                NotificationScheduler.cancelWordOfDay(context)
            }
        }

    var wordOfDayHour: Int
        get() = prefs.getInt("word_of_day_hour", 8) // Default 8 AM
        set(value) {
            prefs.edit().putInt("word_of_day_hour", value).apply()
            if (wordOfDayEnabled) {
                NotificationScheduler.scheduleWordOfDay(context)
            }
        }

    // Streak Data
    var currentStreak: Int
        get() = prefs.getInt("current_streak", 0)
        set(value) = prefs.edit().putInt("current_streak", value).apply()

    var lastPracticeDate: Long
        get() = prefs.getLong("last_practice_date", 0)
        set(value) = prefs.edit().putLong("last_practice_date", value).apply()

    var longestStreak: Int
        get() = prefs.getInt("longest_streak", 0)
        set(value) = prefs.edit().putInt("longest_streak", value).apply()

    // Word of the Day Data
    var lastWordOfDay: String?
        get() = prefs.getString("last_word_of_day", null)
        set(value) = prefs.edit().putString("last_word_of_day", value).apply()

    var wordOfDayTimestamp: Long
        get() = prefs.getLong("word_of_day_timestamp", 0)
        set(value) = prefs.edit().putLong("word_of_day_timestamp", value).apply()

    // Word Quiz Notification Settings
    var wordQuizEnabled: Boolean
        get() = prefs.getBoolean("word_quiz_enabled", true)
        set(value) = prefs.edit().putBoolean("word_quiz_enabled", value).apply()

    var wordQuizIntervalMinutes: Int
        get() = prefs.getInt("word_quiz_interval", 60) // Default 1 hour
        set(value) = prefs.edit().putInt("word_quiz_interval", value).apply()

    // Word Filter Settings for Notifications
    var enabledLevels: Set<String>
        get() = prefs.getStringSet(
            "notification_levels",
            setOf("A1", "A2", "B1", "B2", "C1", "C2")
        ) ?: emptySet()
        set(value) = prefs.edit().putStringSet("notification_levels", value).apply()

    var enabledExams: Set<String>
        get() = prefs.getStringSet("notification_exams", setOf("YDS")) ?: emptySet()
        set(value) = prefs.edit().putStringSet("notification_exams", value).apply()

    var includeLearnedWords: Boolean
        get() = prefs.getBoolean("notification_include_learned", false)
        set(value) = prefs.edit().putBoolean("notification_include_learned", value).apply()

    var includeLowAccuracyWords: Boolean
        get() = prefs.getBoolean("notification_low_accuracy", true)
        set(value) = prefs.edit().putBoolean("notification_low_accuracy", value).apply()

    // Quiet Hours Settings
    var quietHoursEnabled: Boolean
        get() = prefs.getBoolean("quiet_hours_enabled", false)
        set(value) = prefs.edit().putBoolean("quiet_hours_enabled", value).apply()

    var quietHoursStart: Int
        get() = prefs.getInt("quiet_hours_start", 22) // 10 PM
        set(value) = prefs.edit().putInt("quiet_hours_start", value).apply()

    var quietHoursEnd: Int
        get() = prefs.getInt("quiet_hours_end", 8) // 8 AM
        set(value) = prefs.edit().putInt("quiet_hours_end", value).apply()

    private val context = context.applicationContext

    companion object {
        @Volatile
        private var instance: NotificationPreferences? = null

        fun getInstance(context: Context): NotificationPreferences {
            return instance ?: synchronized(this) {
                instance ?: NotificationPreferences(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}
