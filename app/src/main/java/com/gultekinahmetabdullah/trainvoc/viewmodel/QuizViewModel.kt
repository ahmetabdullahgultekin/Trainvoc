package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.Question
import com.gultekinahmetabdullah.trainvoc.classes.Word
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class QuizViewModel(private val repository: WordRepository) : ViewModel() {

    private val _quizQuestions = MutableStateFlow<List<Question>>(emptyList())
    val quizQuestions: StateFlow<List<Question>> = _quizQuestions

    private var currentIndex = 0
    private val _currentQuestion = MutableStateFlow<Question?>(null)
    val currentQuestion: StateFlow<Question?> = _currentQuestion

    fun loadQuizQuestions() {
        viewModelScope.launch {
            val questions = repository.getQuizQuestions()
            _quizQuestions.value = questions
            currentIndex = 0
            loadNextQuestion()
        }
    }

    private fun loadNextQuestion() {
        if (currentIndex < _quizQuestions.value.size) {
            _currentQuestion.value = _quizQuestions.value[currentIndex]
            currentIndex++
        } else {
            _currentQuestion.value = null // Quiz finished
        }
    }

    fun checkAnswer(choice: Word): Boolean {
        val currentQuestion = _currentQuestion.value ?: return false
        return if (currentQuestion.correctWord == choice) {
            // Correct answer
            true
        } else {
            // Wrong answer
            false
        }
    }
}
