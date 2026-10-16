package com.gultekinahmetabdullah.trainvoc.viewmodel

import com.gultekinahmetabdullah.trainvoc.classes.quiz.Question
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Quiz
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word

/**
 * Immutable UI State for QuizScreen adhering to Unidirectional Data Flow (UDF).
 * Consolidates all quiz session state into a single source of truth.
 */
data class QuizUiState(
    val quiz: Quiz? = null,
    val quizParameter: QuizParameter? = null,
    val currentQuestion: Question? = null,
    val currentQuestionNumber: Int = 1,
    val currentWordStats: Statistic? = null,
    val timeLeft: Int = 60,
    val isTimeOver: Boolean = false,
    val progress: Float = 1f,
    val score: Int = 0,
    val currentStreak: Int = 0,
    val selectedAnswer: Word? = null,
    val isCorrect: Boolean? = null,
    val isTimeRunning: Boolean = false,
    val isAnswered: Boolean = false,
    val isQuizFinished: Boolean = false,
    val isUserReady: Boolean = true,
    val totalWords: Int? = null,
    val learnedWords: Int? = null,
    val progressPercent: Int? = null
)
