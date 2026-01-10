package com.gultekinahmetabdullah.trainvoc.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Question
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Quiz
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * QuizViewModel with SavedStateHandle support for process death recovery
 *
 * State Persistence:
 * - Saves quiz state to SavedStateHandle on changes
 * - Restores state automatically after process death
 * - Handles complex state serialization
 */
@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: IWordRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAG = "QuizViewModel"

        // SavedState keys
        private const val KEY_CURRENT_INDEX = "current_index"
        private const val KEY_SCORE = "score"
        private const val KEY_TIME_LEFT = "time_left"
        private const val KEY_IS_QUIZ_FINISHED = "is_quiz_finished"
        private const val KEY_IS_TIME_RUNNING = "is_time_running"
        private const val KEY_IS_ANSWERED = "is_answered"
        private const val KEY_QUIZ_TYPE = "quiz_type"
        private const val KEY_QUIZ_PARAMETER_TYPE = "quiz_parameter_type"
    }

    private val durationConst = 60
    private val progressConst = 1f
    private val scoreConst = 0

    private val _quiz = MutableStateFlow<Quiz?>(null)
    val quiz: StateFlow<Quiz?> = _quiz

    private val _quizParameter = MutableStateFlow<QuizParameter?>(null)
    val quizParameter: StateFlow<QuizParameter?> = _quizParameter

    private val _quizQuestions = MutableStateFlow<MutableList<Question>>(mutableListOf())
    val quizQuestions: StateFlow<List<Question>> = _quizQuestions

    private var currentIndex = 0
        set(value) {
            field = value
            savedStateHandle[KEY_CURRENT_INDEX] = value
        }

    private val _currentQuestion = MutableStateFlow<Question?>(null)
    val currentQuestion: StateFlow<Question?> = _currentQuestion

    private val _currentWordStats = MutableStateFlow<Statistic?>(null)
    val currentWordStats: StateFlow<Statistic?> = _currentWordStats

    // Add time variable to show the time left for each question
    private val _timeLeft = MutableStateFlow(durationConst)
    val timeLeft: StateFlow<Int> = _timeLeft

    private val _isTimeOver = MutableStateFlow(false)
    val isTimeOver: StateFlow<Boolean> = _isTimeOver

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

    // Current question number (1-indexed for display)
    private val _currentQuestionNumber = MutableStateFlow(1)
    val currentQuestionNumber: StateFlow<Int> = _currentQuestionNumber

    // Total and learned word counts for Story and Custom modes
    private val _totalWords = MutableStateFlow<Int?>(null)
    val totalWords: StateFlow<Int?> = _totalWords

    private val _learnedWords = MutableStateFlow<Int?>(null)
    val learnedWords: StateFlow<Int?> = _learnedWords

    private val _progressPercent = MutableStateFlow<Int?>(null)
    val progressPercent: StateFlow<Int?> = _progressPercent

    private var quizJob: Job? = null

    init {
        // Restore state from SavedStateHandle after process death
        restoreState()

        // Observe state changes and save to SavedStateHandle
        viewModelScope.launch {
            _score.collect { score ->
                savedStateHandle[KEY_SCORE] = score
            }
        }

        viewModelScope.launch {
            _timeLeft.collect { timeLeft ->
                savedStateHandle[KEY_TIME_LEFT] = timeLeft
            }
        }

        viewModelScope.launch {
            _isQuizFinished.collect { isFinished ->
                savedStateHandle[KEY_IS_QUIZ_FINISHED] = isFinished
            }
        }

        viewModelScope.launch {
            _isTimeRunning.collect { isRunning ->
                savedStateHandle[KEY_IS_TIME_RUNNING] = isRunning
            }
        }

        viewModelScope.launch {
            _isAnswered.collect { isAnswered ->
                savedStateHandle[KEY_IS_ANSWERED] = isAnswered
            }
        }
    }

    /**
     * Restores quiz state from SavedStateHandle after process death
     */
    private fun restoreState() {
        try {
            currentIndex = savedStateHandle[KEY_CURRENT_INDEX] ?: 0
            _score.value = savedStateHandle[KEY_SCORE] ?: scoreConst
            _timeLeft.value = savedStateHandle[KEY_TIME_LEFT] ?: durationConst
            _isQuizFinished.value = savedStateHandle[KEY_IS_QUIZ_FINISHED] ?: false
            _isTimeRunning.value = savedStateHandle[KEY_IS_TIME_RUNNING] ?: false
            _isAnswered.value = savedStateHandle[KEY_IS_ANSWERED] ?: false

            // Update progress based on restored timeLeft
            _progress.value = _timeLeft.value / durationConst.toFloat()

            Log.d(TAG, "State restored: index=$currentIndex, score=${_score.value}, timeLeft=${_timeLeft.value}")
        } catch (e: Exception) {
            Log.e(TAG, "Error restoring state: ${e.message}", e)
        }
    }

    /**
     * Saves current quiz state to SavedStateHandle
     */
    private fun saveState() {
        try {
            savedStateHandle[KEY_CURRENT_INDEX] = currentIndex
            savedStateHandle[KEY_SCORE] = _score.value
            savedStateHandle[KEY_TIME_LEFT] = _timeLeft.value
            savedStateHandle[KEY_IS_QUIZ_FINISHED] = _isQuizFinished.value
            savedStateHandle[KEY_IS_TIME_RUNNING] = _isTimeRunning.value
            savedStateHandle[KEY_IS_ANSWERED] = _isAnswered.value

            Log.d(TAG, "State saved")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving state: ${e.message}", e)
        }
    }

    fun startQuiz(newQuizParameter: QuizParameter, quiz: Quiz) {

        try {
            // If quiz has already started, then do not allow new start
            resetQuiz()
            // Fetch 10 questions from the database until user reaches the end of the list
            // Start the timer for each question
            // Timer will decrease the timeLeft variable every second
            quizJob = viewModelScope.launch(Dispatchers.IO) {
                // Initialize the variables
                // Declare the quiz type
                _quiz.value = quiz
                _quizParameter.value = newQuizParameter
                // Load the first set of questions
                loadQuizQuestions()
                loadNextQuestion()
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
                        _isTimeOver.value = true
                        checkAnswer(null)
                        resetQuestionVariables()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("QuizViewModel", "Error starting quiz: ${e.message}")
        }
    }

    private suspend fun addNewQuestions() {
        if (currentIndex == _quizQuestions.value.size - 3) {
            loadQuizQuestions()
        }
    }

    private suspend fun loadQuizQuestions() {
        // Add new questions to the dynamic list
        val quizType = _quiz.value?.type ?: return
        val parameter = _quizParameter.value ?: return
        _quizQuestions.value.addAll(
            repository.generateTenQuestions(quizType, parameter)
        )
    }

    fun loadNextQuestion() {
        _isTimeOver.value = false
        resetQuestionVariables()
        if (currentIndex < _quizQuestions.value.size) {
            _currentQuestion.value = _quizQuestions.value[currentIndex]
            currentIndex++
            _currentQuestionNumber.value = currentIndex
            viewModelScope.launch(Dispatchers.IO) {
                _currentQuestion.value?.correctWord?.let { correctWord ->
                    _currentWordStats.value = repository.getWordStats(correctWord)
                }
                addNewQuestions()
            }
            _isTimeRunning.value = true
        } else {
            _currentQuestion.value = null // Quiz finished
        }
    }

    fun checkAnswer(choice: Word?): Boolean? {
        val currentQuestion = _currentQuestion.value ?: return null
        val currentStats = _currentWordStats.value ?: return null
        pauseQuiz()
        val secondsSpent = durationConst - _timeLeft.value
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLastAnswered(currentQuestion.correctWord.word)
            repository.updateSecondsSpent(secondsSpent, currentQuestion.correctWord)
        }
        if (choice == null) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateWordStats(
                    currentStats.copy(
                        skippedCount = currentStats.skippedCount + 1
                    ),
                    currentQuestion.correctWord
                )
            }
            return null
        }
        if (currentQuestion.correctWord == choice) {
            // Correct answer
            _score.value++
            // Update the entity stats in the database
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateWordStats(
                    currentStats.copy(
                        correctCount = currentStats.correctCount + 1
                    ),
                    currentQuestion.correctWord
                )
            }
            return true
        } else {
            // Wrong answer
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateWordStats(
                    currentStats.copy(
                        wrongCount = currentStats.wrongCount + 1
                    ),
                    currentQuestion.correctWord
                )
            }
            return false
        }
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
        _currentWordStats.value = null
        _totalWords.value = null
        _learnedWords.value = null
        _progressPercent.value = null
        _isTimeOver.value = false
        _currentQuestionNumber.value = 1
        // Cancel the quiz job if it is running
        quizJob?.cancel()
    }

    fun finalizeQuiz() {
        // Check answer and send it as null if the user didn't answer
        checkAnswer(null)
        // Reset the quiz variables
        resetQuiz()
    }

    fun collectQuizStats(parameter: QuizParameter) {
        viewModelScope.launch(Dispatchers.IO) {
            when (parameter) {
                is QuizParameter.Level -> {
                    val total = repository.getWordCountByLevel(parameter.wordLevel.name)
                    val learned = repository.getLearnedWordCount(parameter.wordLevel.name)
                    _totalWords.value = total
                    _learnedWords.value = learned
                    _progressPercent.value = if (total > 0) (learned * 100 / total) else 0
                }

                is QuizParameter.ExamType -> {
                    val total = repository.getWordCountByExam(parameter.exam.exam)
                    val learned = repository.getLearnedWordCountByExam(parameter.exam.exam)
                    _totalWords.value = total
                    _learnedWords.value = learned
                    _progressPercent.value = if (total > 0) (learned * 100 / total) else 0
                }
            }
        }
    }
}
