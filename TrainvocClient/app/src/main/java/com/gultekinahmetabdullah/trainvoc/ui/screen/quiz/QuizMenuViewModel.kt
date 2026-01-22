package com.gultekinahmetabdullah.trainvoc.ui.screen.quiz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Quiz
import com.gultekinahmetabdullah.trainvoc.quiz.QuizHistoryDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for QuizMenuScreen
 * Manages quiz statistics per quiz type
 */
@HiltViewModel
class QuizMenuViewModel @Inject constructor(
    private val quizHistoryDao: QuizHistoryDao
) : ViewModel() {

    companion object {
        private const val TAG = "QuizMenuViewModel"
    }

    private val _quizStats = MutableStateFlow<Map<Int, QuizTypeStats>>(emptyMap())
    val quizStats: StateFlow<Map<Int, QuizTypeStats>> = _quizStats.asStateFlow()

    private val _hasAnyQuizHistory = MutableStateFlow(false)
    val hasAnyQuizHistory: StateFlow<Boolean> = _hasAnyQuizHistory.asStateFlow()

    init {
        loadQuizStats()
    }

    /**
     * Load statistics for all quiz types
     */
    private fun loadQuizStats() {
        viewModelScope.launch {
            try {
                // Check if user has any quiz history
                val totalCount = quizHistoryDao.getTotalQuizCount()
                _hasAnyQuizHistory.value = totalCount > 0

                // Load stats for each quiz type
                val statsMap = mutableMapOf<Int, QuizTypeStats>()

                Quiz.quizTypes.forEach { quiz ->
                    val stats = calculateQuizTypeStats(quiz.name)
                    if (stats.timesPlayed > 0 || stats.bestScore != null) {
                        statsMap[quiz.id] = stats
                    }
                }

                _quizStats.value = statsMap
            } catch (e: Exception) {
                Log.e(TAG, "Error loading quiz stats", e)
            }
        }
    }

    /**
     * Calculate statistics for a specific quiz type
     */
    private suspend fun calculateQuizTypeStats(quizType: String): QuizTypeStats {
        return try {
            val quizHistory = quizHistoryDao.getQuizHistoryByType(quizType)

            if (quizHistory.isEmpty()) {
                QuizTypeStats(
                    bestScore = null,
                    timesPlayed = 0
                )
            } else {
                val bestScore = quizHistory.maxOfOrNull { it.accuracy }
                QuizTypeStats(
                    bestScore = bestScore,
                    timesPlayed = quizHistory.size
                )
            }
        } catch (e: Exception) {
            QuizTypeStats(bestScore = null, timesPlayed = 0)
        }
    }
}

/**
 * Statistics for a quiz type
 */
data class QuizTypeStats(
    val bestScore: Float?,      // Best accuracy (0.0 to 1.0)
    val timesPlayed: Int         // Number of times played
)
