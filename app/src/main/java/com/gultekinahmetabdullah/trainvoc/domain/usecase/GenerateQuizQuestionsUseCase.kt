package com.gultekinahmetabdullah.trainvoc.domain.usecase

import com.gultekinahmetabdullah.trainvoc.classes.quiz.Question
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Quiz
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.repository.IQuizService
import javax.inject.Inject

/**
 * Use Case for generating quiz questions.
 * Extracts complex quiz generation logic from ViewModel.
 * Follows Dependency Inversion Principle by depending on IQuizService interface.
 */
class GenerateQuizQuestionsUseCase @Inject constructor(
    private val quizService: IQuizService
) {
    /**
     * Generates a list of quiz questions based on parameters and quiz type.
     *
     * @param parameter Quiz parameters (level or exam type)
     * @param quiz Quiz configuration (question count, options, etc.)
     * @return List of generated questions
     */
    suspend operator fun invoke(
        parameter: QuizParameter,
        quiz: Quiz
    ): Result<List<Question>> {
        return try {
            val questions = quizService.generateTenQuestions(quiz.type, parameter)
            if (questions.isEmpty()) {
                Result.failure(Exception("No questions could be generated. Please check your quiz parameters."))
            } else {
                Result.success(questions)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
