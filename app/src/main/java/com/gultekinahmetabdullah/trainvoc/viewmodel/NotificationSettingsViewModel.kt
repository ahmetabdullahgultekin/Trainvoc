package com.gultekinahmetabdullah.trainvoc.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.gultekinahmetabdullah.trainvoc.notification.NotificationHelper
import com.gultekinahmetabdullah.trainvoc.notification.NotificationPreferences
import com.gultekinahmetabdullah.trainvoc.notification.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for managing notification settings.
 * Provides reactive state for the NotificationSettingsScreen UI.
 */
@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = NotificationPreferences.getInstance(context)

    // Word Quiz Settings
    private val _wordQuizEnabled = MutableStateFlow(prefs.wordQuizEnabled)
    val wordQuizEnabled: StateFlow<Boolean> = _wordQuizEnabled.asStateFlow()

    private val _wordQuizInterval = MutableStateFlow(prefs.wordQuizIntervalMinutes)
    val wordQuizInterval: StateFlow<Int> = _wordQuizInterval.asStateFlow()

    // Filter Settings
    private val _enabledLevels = MutableStateFlow(prefs.enabledLevels)
    val enabledLevels: StateFlow<Set<String>> = _enabledLevels.asStateFlow()

    private val _enabledExams = MutableStateFlow(prefs.enabledExams)
    val enabledExams: StateFlow<Set<String>> = _enabledExams.asStateFlow()

    private val _includeLearnedWords = MutableStateFlow(prefs.includeLearnedWords)
    val includeLearnedWords: StateFlow<Boolean> = _includeLearnedWords.asStateFlow()

    private val _includeLowAccuracyWords = MutableStateFlow(prefs.includeLowAccuracyWords)
    val includeLowAccuracyWords: StateFlow<Boolean> = _includeLowAccuracyWords.asStateFlow()

    // Quiet Hours Settings
    private val _quietHoursEnabled = MutableStateFlow(prefs.quietHoursEnabled)
    val quietHoursEnabled: StateFlow<Boolean> = _quietHoursEnabled.asStateFlow()

    private val _quietHoursStart = MutableStateFlow(prefs.quietHoursStart)
    val quietHoursStart: StateFlow<Int> = _quietHoursStart.asStateFlow()

    private val _quietHoursEnd = MutableStateFlow(prefs.quietHoursEnd)
    val quietHoursEnd: StateFlow<Int> = _quietHoursEnd.asStateFlow()

    // Other Notification Settings
    private val _dailyRemindersEnabled = MutableStateFlow(prefs.dailyRemindersEnabled)
    val dailyRemindersEnabled: StateFlow<Boolean> = _dailyRemindersEnabled.asStateFlow()

    private val _streakAlertsEnabled = MutableStateFlow(prefs.streakAlertsEnabled)
    val streakAlertsEnabled: StateFlow<Boolean> = _streakAlertsEnabled.asStateFlow()

    private val _wordOfDayEnabled = MutableStateFlow(prefs.wordOfDayEnabled)
    val wordOfDayEnabled: StateFlow<Boolean> = _wordOfDayEnabled.asStateFlow()

    // Word Quiz Settings
    fun setWordQuizEnabled(enabled: Boolean) {
        prefs.wordQuizEnabled = enabled
        _wordQuizEnabled.value = enabled
        if (enabled) {
            NotificationScheduler.scheduleWordQuiz(context)
        } else {
            NotificationScheduler.cancelWordQuiz(context)
        }
    }

    fun setWordQuizInterval(minutes: Int) {
        prefs.wordQuizIntervalMinutes = minutes
        _wordQuizInterval.value = minutes
        if (prefs.wordQuizEnabled) {
            NotificationScheduler.scheduleWordQuiz(context)
        }
    }

    // Filter Settings
    fun toggleLevel(level: String, enabled: Boolean) {
        val currentLevels = _enabledLevels.value.toMutableSet()
        if (enabled) {
            currentLevels.add(level)
        } else {
            currentLevels.remove(level)
        }
        prefs.enabledLevels = currentLevels
        _enabledLevels.value = currentLevels
    }

    fun toggleExam(exam: String, enabled: Boolean) {
        val currentExams = _enabledExams.value.toMutableSet()
        if (enabled) {
            currentExams.add(exam)
        } else {
            currentExams.remove(exam)
        }
        prefs.enabledExams = currentExams
        _enabledExams.value = currentExams
    }

    fun setIncludeLearnedWords(include: Boolean) {
        prefs.includeLearnedWords = include
        _includeLearnedWords.value = include
    }

    fun setIncludeLowAccuracyWords(include: Boolean) {
        prefs.includeLowAccuracyWords = include
        _includeLowAccuracyWords.value = include
    }

    // Quiet Hours Settings
    fun setQuietHoursEnabled(enabled: Boolean) {
        prefs.quietHoursEnabled = enabled
        _quietHoursEnabled.value = enabled
    }

    fun setQuietHoursStart(hour: Int) {
        prefs.quietHoursStart = hour
        _quietHoursStart.value = hour
    }

    fun setQuietHoursEnd(hour: Int) {
        prefs.quietHoursEnd = hour
        _quietHoursEnd.value = hour
    }

    // Other Notification Settings
    fun setDailyRemindersEnabled(enabled: Boolean) {
        prefs.dailyRemindersEnabled = enabled
        _dailyRemindersEnabled.value = enabled
    }

    fun setStreakAlertsEnabled(enabled: Boolean) {
        prefs.streakAlertsEnabled = enabled
        _streakAlertsEnabled.value = enabled
    }

    fun setWordOfDayEnabled(enabled: Boolean) {
        prefs.wordOfDayEnabled = enabled
        _wordOfDayEnabled.value = enabled
    }

    // Test notification
    fun sendTestNotification() {
        NotificationHelper.sendWordQuizNotification(context)
    }

    // Frequency presets for the UI
    companion object {
        val FREQUENCY_PRESETS = listOf(
            15 to "15 min",
            30 to "30 min",
            60 to "1 hour",
            120 to "2 hours",
            240 to "4 hours",
            480 to "8 hours",
            1440 to "Daily"
        )

        val LEVELS = listOf("A1", "A2", "B1", "B2", "C1", "C2")
        val EXAMS = listOf("YDS", "YÖKDİL", "TOEFL", "IELTS")
    }
}
