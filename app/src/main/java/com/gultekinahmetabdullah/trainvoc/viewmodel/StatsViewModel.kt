package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StatsViewModel : ViewModel() {

    private val _totalQuestions = MutableStateFlow(0)
    val totalQuestions: StateFlow<Int> = _totalQuestions

    private val _correctAnswers = MutableStateFlow(0)
    val correctAnswers: StateFlow<Int> = _correctAnswers

    private val _incorrectAnswers = MutableStateFlow(0)
    val incorrectAnswers: StateFlow<Int> = _incorrectAnswers

    private val _successPercentage = MutableStateFlow(0f)
    val successPercentage: StateFlow<Float> = MutableStateFlow(0f)

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
}
