package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.classes.enums.QuizType
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Question
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter

/**
 * Interface for quiz generation operations.
 * Follows Interface Segregation Principle - clients that only need
 * quiz generation don't depend on word management or analytics.
 */
interface IQuizService {
    /**
     * Generate quiz questions based on type and parameters.
     */
    suspend fun generateTenQuestions(
        quizType: QuizType,
        quizParameter: QuizParameter
    ): MutableList<Question>
}
