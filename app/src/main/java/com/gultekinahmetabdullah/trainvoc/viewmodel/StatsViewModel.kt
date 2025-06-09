package com.gultekinahmetabdullah.trainvoc.viewmodel

import android.icu.text.DateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatsViewModel(private val repository: WordRepository) : ViewModel() {

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
        viewModelScope.launch {
            _correctAnswers.value = repository.getCorrectAnswers()
            _incorrectAnswers.value = repository.getWrongAnswers()
            _skippedQuestions.value = repository.getSkippedAnswers()
            _totalQuestions.value =
                _correctAnswers.value + _incorrectAnswers.value + _skippedQuestions.value
            calculateRatios()
            _totalTimeSpent.value = repository.getTotalTimeSpent()
            // Convert the last answered time to a readable format
            _lastAnswered.value = if (repository.getLastAnswered() == 0L) "N/A"
            else DateFormat.getDateTimeInstance().format(repository.getLastAnswered())
            // Yeni istatistikler
            _totalQuizCount.value = repository.getTotalQuizCount()
            _dailyCorrect.value = repository.getDailyCorrectAnswers()
            _weeklyCorrect.value = repository.getWeeklyCorrectAnswers()
            _mostWrongWord.value = repository.getMostWrongWord() ?: "-"
            _bestCategory.value = repository.getBestCategory() ?: "-"
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

    /*
    fun updateStats(isCorrect: Boolean) {
        _totalQuestions.value++

        if (isCorrect) {
            _correctAnswers.value++
        } else {
            _incorrectAnswers.value++
        }

        calculateSuccessPercentage()
    }


    private fun calculateSuccessPercentage() {
        _successPercentage.value = if (_totalQuestions.value == 0) 0f
        else (_correctAnswers.value.toFloat() / _totalQuestions.value) * 100
    }

    fun resetStats() {
        _totalQuestions.value = 0
        _correctAnswers.value = 0
        _incorrectAnswers.value = 0
        _successPercentage.value = 0f
    }

     */
}
