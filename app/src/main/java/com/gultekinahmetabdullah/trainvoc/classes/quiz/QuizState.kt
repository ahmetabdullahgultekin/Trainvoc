package com.gultekinahmetabdullah.trainvoc.classes.quiz

import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic

/**
 * Sealed class representing all possible states of a quiz session.
 * This replaces 19+ scattered state variables in QuizViewModel.
 */
sealed class QuizState {
    /**
     * Initial state before quiz starts
     */
    object Idle : QuizState()

    /**
     * Loading state while questions are being generated
     */
    object Loading : QuizState()

    /**
     * Active quiz state with current question
     */
    data class Active(
        val currentQuestion: Question,
        val questionIndex: Int,
        val totalQuestions: Int,
        val score: Int,
        val timeLeft: Int,
        val currentWordStats: Statistic?,
        val isTimerRunning: Boolean = true
    ) : QuizState() {
        val progress: Float
            get() = if (totalQuestions > 0) questionIndex.toFloat() / totalQuestions else 0f
    }

    /**
     * State when user has answered the current question
     */
    data class Answered(
        val currentQuestion: Question,
        val questionIndex: Int,
        val totalQuestions: Int,
        val score: Int,
        val timeLeft: Int,
        val currentWordStats: Statistic?,
        val isCorrect: Boolean,
        val selectedAnswer: com.gultekinahmetabdullah.trainvoc.classes.word.Word
    ) : QuizState() {
        val progress: Float
            get() = if (totalQuestions > 0) questionIndex.toFloat() / totalQuestions else 0f
    }

    /**
     * State when time runs out on current question
     */
    data class TimeUp(
        val currentQuestion: Question,
        val questionIndex: Int,
        val totalQuestions: Int,
        val score: Int,
        val currentWordStats: Statistic?
    ) : QuizState() {
        val progress: Float
            get() = if (totalQuestions > 0) questionIndex.toFloat() / totalQuestions else 0f
    }

    /**
     * Quiz finished state
     */
    data class Finished(
        val finalScore: Int,
        val totalQuestions: Int,
        val correctAnswers: Int,
        val wrongAnswers: Int,
        val skippedQuestions: Int
    ) : QuizState()

    /**
     * Error state
     */
    data class Error(val message: String) : QuizState()
}
