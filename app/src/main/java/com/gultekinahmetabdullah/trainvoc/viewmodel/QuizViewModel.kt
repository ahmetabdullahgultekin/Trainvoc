package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.Question
import com.gultekinahmetabdullah.trainvoc.classes.Word
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class QuizViewModel(private val repository: WordRepository) : ViewModel() {

    private val durationConst = 60
    private val progressConst = 1f
    private val scoreConst = 0

    private val _quizQuestions = MutableStateFlow<MutableList<Question>>(mutableListOf())
    val quizQuestions: StateFlow<List<Question>> = _quizQuestions

    private var currentIndex = 0
    private val _currentQuestion = MutableStateFlow<Question?>(null)
    val currentQuestion: StateFlow<Question?> = _currentQuestion

    // Add time variable to show the time left for each question
    private val _timeLeft = MutableStateFlow(durationConst)
    val timeLeft: StateFlow<Int> = _timeLeft

    // Add a variable to store the progress of the quiz
    private val _progress = MutableStateFlow(progressConst)
    val progress: StateFlow<Float> = _progress

    // Add a variable to store the user's score
    private val _score = MutableStateFlow(scoreConst)
    val score: StateFlow<Int> = _score

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    fun startQuiz() {
        // Fetch 10 questions from the database until user reaches the end of the list
        // Start the timer for each question
        // Timer will decrease the timeLeft variable every second
        viewModelScope.launch {
            // Initialize the variables
            // Reset the score
            _score.value = scoreConst
            _isRunning.value = true
            // Load the first set of questions
            loadQuizQuestions()
            /*
             * Start the quiz
             *
             * Loop through the questions,
             * This loop will continue until the user reaches the end of the list
             * Each iteration corresponds to a question
             *
             * Load the next question
             * Start the timer
             * Wait for the timer to finish
             */
            while (currentIndex < _quizQuestions.value.size) {
                // Reset the timer and progress bar
                _timeLeft.value = durationConst
                _progress.value = progressConst
                // Load the next question
                loadNextQuestion()
                // Check if we need to load more questions
                if (currentIndex == _quizQuestions.value.size - 3) {
                    loadQuizQuestions()
                }
                // Start the timer
                while (_timeLeft.value > 0) {
                    // Check if the user has paused the quiz
                    if (!_isRunning.value) {
                        delay(100) // Check every 100ms if the quiz is resumed
                        continue
                    }
                    // Wait for 1 second
                    delay(1000)
                    _timeLeft.value--
                    _progress.value = _timeLeft.value / durationConst.toFloat()
                }
            }
        }
    }

    private suspend fun loadQuizQuestions() {
        val questions = repository.getQuizQuestions()
        // Add new questions to the dynamic list
        _quizQuestions.value.addAll(questions)
    }

    fun loadNextQuestion() {
        if (currentIndex < _quizQuestions.value.size) {
            _currentQuestion.value = _quizQuestions.value[currentIndex]
            currentIndex++
        } else {
            _currentQuestion.value = null // Quiz finished
        }
    }

    fun checkAnswer(choice: Word): Boolean {
        val currentQuestion = _currentQuestion.value ?: return false
        _progress.value = progressConst
        _timeLeft.value = durationConst
        return if (currentQuestion.correctWord == choice) {
            // Correct answer
            _score.value++
            true
        } else {
            // Wrong answer
            _score.value--
            false
        }
    }

    fun togglePause() {
        _isRunning.value = !_isRunning.value
    }

    private fun resetQuiz() {
        _quizQuestions.value.clear()
        currentIndex = 0
        _currentQuestion.value = null
        _timeLeft.value = durationConst
        _progress.value = progressConst
        _score.value = scoreConst
        _isRunning.value = false
    }

    fun finalizeQuiz() {
        resetQuiz()
    }
}
