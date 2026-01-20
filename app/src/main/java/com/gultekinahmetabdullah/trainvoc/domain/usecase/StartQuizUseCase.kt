package com.gultekinahmetabdullah.trainvoc.domain.usecase

import com.gultekinahmetabdullah.trainvoc.classes.quiz.Question
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Quiz
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.core.common.AppError
import com.gultekinahmetabdullah.trainvoc.core.common.AppResult
import com.gultekinahmetabdullah.trainvoc.core.common.DispatcherProvider
import com.gultekinahmetabdullah.trainvoc.repository.IQuizService
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for starting a quiz session
 *
 * This demonstrates the UseCase pattern for extracting business logic
 * from ViewModels. Benefits:
 * - Single Responsibility: Each UseCase does one thing
 * - Testable: Can test business logic without ViewModel complexity
 * - Reusable: Can be used by different ViewModels or UI layers
 * - Type-safe: Uses AppResult for explicit error handling
 *
 * Example usage in ViewModel:
 * ```kotlin
 * viewModelScope.launch {
 *     when (val result = startQuizUseCase(quiz, parameter)) {
 *         is AppResult.Success -> {
 *             _quizQuestions.value = result.data
 *             startTimer()
 *         }
 *         is AppResult.Error -> _errorState.value = result.error
 *         is AppResult.Loading -> _isLoading.value = true
 *     }
 * }
 * ```
 */
class StartQuizUseCase @Inject constructor(
    private val quizService: IQuizService,
    private val dispatchers: DispatcherProvider
) {
    /**
     * Initialize a new quiz session
     *
     * @param quiz The quiz type to start
     * @param parameter The quiz parameters (level or exam type)
     * @return AppResult containing initial questions or error
     */
    suspend operator fun invoke(
        quiz: Quiz,
        parameter: QuizParameter
    ): AppResult<QuizSession> = withContext(dispatchers.io) {
        try {
            // Validate inputs
            if (!isValidQuizConfiguration(quiz, parameter)) {
                return@withContext AppResult.Error(
                    AppError.Validation("Invalid quiz configuration")
                )
            }

            // Load initial questions
            val questions = quizService.generateTenQuestions(
                quizType = quiz.type,
                quizParameter = parameter
            )

            // Validate questions were generated
            if (questions.isEmpty()) {
                return@withContext AppResult.Error(
                    AppError.NotFound("No questions available for this quiz type")
                )
            }

            // Return successful session
            AppResult.Success(
                QuizSession(
                    quiz = quiz,
                    parameter = parameter,
                    questions = questions,
                    startTime = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            AppResult.Error(
                AppError.Database(
                    message = "Failed to start quiz: ${e.message}",
                    cause = e
                )
            )
        }
    }

    /**
     * Validate quiz configuration before starting
     */
    private fun isValidQuizConfiguration(quiz: Quiz, parameter: QuizParameter): Boolean {
        // Add validation logic as needed
        return true
    }
}

/**
 * Represents an initialized quiz session
 *
 * This encapsulates all data needed to start a quiz,
 * making it easy to pass between layers and test.
 */
data class QuizSession(
    val quiz: Quiz,
    val parameter: QuizParameter,
    val questions: MutableList<Question>,
    val startTime: Long
)
