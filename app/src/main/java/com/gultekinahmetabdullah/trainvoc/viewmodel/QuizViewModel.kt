package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.Question
import com.gultekinahmetabdullah.trainvoc.classes.Quiz
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

    private val _quiz = MutableStateFlow<Quiz?>(null)
    val quiz: StateFlow<Quiz?> = _quiz

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

    private val _isTimeRunning = MutableStateFlow(false)
    val isTimeRunning: StateFlow<Boolean> = _isTimeRunning

    private val _isAnswered = MutableStateFlow(false)
    val isAnswered: StateFlow<Boolean> = _isAnswered

    private val _isQuizFinished = MutableStateFlow(false)
    val isQuizFinished: StateFlow<Boolean> = _isQuizFinished

    private val _isUserReady = MutableStateFlow(true)
    val isUserReady: StateFlow<Boolean> = _isUserReady

    /*
    init {
        viewModelScope.launch {
            loadQuizQuestions()
        }
    }
     */

    fun startQuiz(quiz: Quiz) {
        // If quiz has already started, then do not allow new start
        resetQuiz()
        // Fetch 10 questions from the database until user reaches the end of the list
        // Start the timer for each question
        // Timer will decrease the timeLeft variable every second
        viewModelScope.launch {
            // Initialize the variables
            // Declare the quiz type
            _quiz.value = quiz
            // Load the first set of questions
            loadQuizQuestions()
            loadNextQuestion()
            // Start the quiz
            _isTimeRunning.value = true
            _isQuizFinished.value = false
            // Start the timer
            while (_timeLeft.value > 0 && !_isQuizFinished.value) {
                // Check if the user has paused the quiz
                while (!_isTimeRunning.value) {
                    delay(100)
                    continue
                }
                // Wait for 1 second
                delay(1000)
                _timeLeft.value--
                _progress.value = _timeLeft.value / durationConst.toFloat()
                if (_timeLeft.value == 0) {
                    checkAnswer(null)
                    resetQuestionVariables()
                }
            }

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
            /*
            viewModelScope.launch {
                while (currentIndex < _quizQuestions.value.size) {
                    while (_isTimeRunning.value) {
                        delay(100)
                    }
                    // Check if we need to load more questions
                    addNewQuestions()
                    // Reset the timer and progress bar
                    resetQuestionVariables()
                    // Load the next question
                    loadNextQuestion()
                }
            }

             */
        }
    }

    private suspend fun addNewQuestions() {
        if (currentIndex == _quizQuestions.value.size - 3) {
            loadQuizQuestions()
        }
    }

    private suspend fun loadQuizQuestions() {
        // Add new questions to the dynamic list
        _quizQuestions.value.addAll(repository.generateTenQuestions(_quiz.value!!.type))
    }

    fun loadNextQuestion() {
        resetQuestionVariables()
        if (currentIndex < _quizQuestions.value.size) {
            _currentQuestion.value = _quizQuestions.value[currentIndex]
            currentIndex++
            viewModelScope.launch {
                addNewQuestions()
            }
            _isTimeRunning.value = true
        } else {
            _currentQuestion.value = null // Quiz finished
        }
    }

    fun checkAnswer(choice: Word?): Boolean? {
        val currentQuestion = _currentQuestion.value ?: return null
        pauseQuiz()
        viewModelScope.launch {
            repository.addTimeSpent(
                _currentQuestion.value!!.correctWord.word,
                durationConst - _timeLeft.value
            )
            repository.updateLastAnswered(_currentQuestion.value!!.correctWord.word)
        }
        if (choice == null) {
            viewModelScope.launch {
                repository.increaseSkippedAnswers(_currentQuestion.value!!.correctWord.word)
            }
            return null
        }
        if (currentQuestion.correctWord == choice) {
            // Correct answer
            _score.value++
            // Update the entity stats in the database
            viewModelScope.launch {
                repository.increaseCorrectAnswers(_currentQuestion.value!!.correctWord.word)
            }
            return true
        } else {
            // Wrong answer
            _score.value--
            // Update the entity stats in the database
            viewModelScope.launch {
                repository.increaseWrongAnswers(_currentQuestion.value!!.correctWord.word)
            }
            return false
        }
    }

    fun resumeQuiz() {
        _isAnswered.value = false
        _isTimeRunning.value = true
        _isUserReady.value = true
    }

    private fun pauseQuiz() {
        _isTimeRunning.value = false
        _isAnswered.value = true
        _isUserReady.value = false
    }

    private fun resetQuestionVariables() {
        _timeLeft.value = durationConst
        _progress.value = progressConst
        _isAnswered.value = false
        _isUserReady.value = true
    }

    private fun resetQuiz() {
        _quizQuestions.value.clear()
        currentIndex = 0
        _currentQuestion.value = null
        _timeLeft.value = durationConst
        _progress.value = progressConst
        _score.value = scoreConst
        _isTimeRunning.value = false
        _isAnswered.value = false
        _isQuizFinished.value = true
        _isUserReady.value = false
        _quiz.value = null
        _isQuizFinished.value = true
    }

    fun finalizeQuiz() {
        resetQuiz()
    }
}
