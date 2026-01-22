package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import com.gultekinahmetabdullah.trainvoc.quiz.QuizHistory
import com.gultekinahmetabdullah.trainvoc.quiz.QuizHistoryDao
import com.gultekinahmetabdullah.trainvoc.quiz.QuizQuestionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for quiz history management
 *
 * Handles:
 * - Saving quiz results after completion
 * - Loading last quiz results for display
 * - Tracking individual question results for missed words review
 */
@HiltViewModel
class QuizHistoryViewModel @Inject constructor(
    private val quizHistoryDao: QuizHistoryDao,
    private val wordDao: WordDao
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _lastQuizResult = MutableStateFlow<QuizHistory?>(null)
    val lastQuizResult: StateFlow<QuizHistory?> = _lastQuizResult.asStateFlow()

    private val _missedWords = MutableStateFlow<List<Word>>(emptyList())
    val missedWords: StateFlow<List<Word>> = _missedWords.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Statistics
    private val _totalQuizCount = MutableStateFlow(0)
    val totalQuizCount: StateFlow<Int> = _totalQuizCount.asStateFlow()

    private val _averageAccuracy = MutableStateFlow(0f)
    val averageAccuracy: StateFlow<Float> = _averageAccuracy.asStateFlow()

    init {
        loadLastQuizResult()
        loadStatistics()
    }

    /**
     * Load the most recent quiz result.
     */
    private fun loadLastQuizResult() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = quizHistoryDao.getLastQuizResult()
                _lastQuizResult.value = result

                // Load missed words if quiz exists
                if (result != null) {
                    loadMissedWords(result.id)
                }
            } catch (e: Exception) {
                _error.value = "Failed to load quiz results: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load words that were answered incorrectly in a quiz.
     */
    private suspend fun loadMissedWords(quizId: Int) {
        try {
            val incorrectWordIds = quizHistoryDao.getIncorrectWordIds(quizId)
            val words = incorrectWordIds.mapNotNull { wordId ->
                try {
                    wordDao.getWord(wordId)
                } catch (e: Exception) {
                    null
                }
            }
            _missedWords.value = words
        } catch (e: Exception) {
            _error.value = "Failed to load missed words: ${e.message}"
        }
    }

    /**
     * Load overall quiz statistics.
     */
    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                _totalQuizCount.value = quizHistoryDao.getTotalQuizCount()
                _averageAccuracy.value = quizHistoryDao.getAverageAccuracy() ?: 0f
            } catch (e: Exception) {
                // Statistics are optional, don't show error
            }
        }
    }

    /**
     * Save a completed quiz to history.
     *
     * @param totalQuestions Total number of questions in the quiz
     * @param correctAnswers Number of correct answers
     * @param wrongAnswers Number of wrong answers
     * @param skippedQuestions Number of skipped questions
     * @param timeTaken Time taken to complete (formatted as "MM:SS")
     * @param quizType Type of quiz (e.g., "LEVEL_A1", "EXAM_TOEFL")
     * @param questionResults List of pairs (wordId, isCorrect) for each question
     */
    suspend fun saveQuizResult(
        totalQuestions: Int,
        correctAnswers: Int,
        wrongAnswers: Int,
        skippedQuestions: Int,
        timeTaken: String,
        quizType: String,
        questionResults: List<Pair<String, Boolean>>
    ): Long {
        val accuracy = if (totalQuestions > 0) {
            (correctAnswers.toFloat() / totalQuestions) * 100
        } else {
            0f
        }

        val quizHistory = QuizHistory(
            timestamp = System.currentTimeMillis(),
            totalQuestions = totalQuestions,
            correctAnswers = correctAnswers,
            wrongAnswers = wrongAnswers,
            skippedQuestions = skippedQuestions,
            timeTaken = timeTaken,
            quizType = quizType,
            accuracy = accuracy
        )

        val quizId = quizHistoryDao.insertQuizHistory(quizHistory)

        // Save individual question results
        val results = questionResults.map { (wordId, isCorrect) ->
            QuizQuestionResult(
                quizId = quizId.toInt(),
                wordId = wordId,
                isCorrect = isCorrect
            )
        }
        quizHistoryDao.insertQuestionResults(results)

        // Refresh the last quiz result
        loadLastQuizResult()
        loadStatistics()

        return quizId
    }

    /**
     * Refresh the quiz history data.
     */
    fun refresh() {
        loadLastQuizResult()
        loadStatistics()
    }

    /**
     * Clear error state.
     */
    fun clearError() {
        _error.value = null
    }
}
