package com.gultekinahmetabdullah.trainvoc.viewmodel

import android.icu.text.DateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.repository.IAnalyticsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val analyticsService: IAnalyticsService
) : ViewModel() {

    private val _correctAnswers = MutableStateFlow(0)
    val correctAnswers: StateFlow<Int> = _correctAnswers

    private val _incorrectAnswers = MutableStateFlow(0)
    val incorrectAnswers: StateFlow<Int> = _incorrectAnswers

    private val _skippedQuestions = MutableStateFlow(0)
    val skippedQuestions: StateFlow<Int> = _skippedQuestions

    private val _totalQuestions = MutableStateFlow(0)
    val totalQuestions: StateFlow<Int> = _totalQuestions

    private val _successRatio = MutableStateFlow(0f)
    val successRatio: StateFlow<Float> = _successRatio

    private val _failureRatio = MutableStateFlow(0f)
    val failureRatio: StateFlow<Float> = _failureRatio

    private val _skippedRatio = MutableStateFlow(0f)
    val skippedRatio: StateFlow<Float> = _skippedRatio

    private val _totalTimeSpent = MutableStateFlow(0)
    val totalTimeSpent: StateFlow<Int> = _totalTimeSpent

    private val _lastAnswered = MutableStateFlow("")
    val lastAnswered: StateFlow<String> = _lastAnswered

    private val _totalQuizCount = MutableStateFlow(0)
    val totalQuizCount: StateFlow<Int> = _totalQuizCount

    private val _dailyCorrect = MutableStateFlow(0)
    val dailyCorrect: StateFlow<Int> = _dailyCorrect

    private val _weeklyCorrect = MutableStateFlow(0)
    val weeklyCorrect: StateFlow<Int> = _weeklyCorrect

    private val _mostWrongWord = MutableStateFlow("")
    val mostWrongWord: StateFlow<String> = _mostWrongWord

    private val _bestCategory = MutableStateFlow("")
    val bestCategory: StateFlow<String> = _bestCategory

    init {
        fillStats()
    }

    fun fillStats() {
        viewModelScope.launch(Dispatchers.IO) {
            _correctAnswers.value = analyticsService.getCorrectAnswers()
            _incorrectAnswers.value = analyticsService.getWrongAnswers()
            _skippedQuestions.value = analyticsService.getSkippedAnswers()
            _totalQuestions.value =
                _correctAnswers.value + _incorrectAnswers.value + _skippedQuestions.value
            calculateRatios()
            _totalTimeSpent.value = analyticsService.getTotalTimeSpent()
            // Convert the last answered time to a readable format
            _lastAnswered.value = if (analyticsService.getLastAnswered() == 0L) "N/A"
            else DateFormat.getDateTimeInstance().format(analyticsService.getLastAnswered())
            // Yeni istatistikler
            _totalQuizCount.value = analyticsService.getTotalQuizCount()
            _dailyCorrect.value = analyticsService.getDailyCorrectAnswers()
            _weeklyCorrect.value = analyticsService.getWeeklyCorrectAnswers()
            _mostWrongWord.value = analyticsService.getMostWrongWord() ?: "-"
            _bestCategory.value = analyticsService.getBestCategory() ?: "-"
        }
    }

    private fun calculateRatios() {
        _successRatio.value = if (_totalQuestions.value == 0) 0f
        else (_correctAnswers.value.toFloat() / _totalQuestions.value)

        _failureRatio.value = if (_totalQuestions.value == 0) 0f
        else (_incorrectAnswers.value.toFloat() / _totalQuestions.value)

        _skippedRatio.value = if (_totalQuestions.value == 0) 0f
        else (_skippedQuestions.value.toFloat() / _totalQuestions.value)
    }
}
