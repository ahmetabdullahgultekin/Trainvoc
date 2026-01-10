package com.gultekinahmetabdullah.trainvoc.classes.quiz

import com.gultekinahmetabdullah.trainvoc.classes.enums.QuizType

data class Quiz(
    val id: Int,
    val name: String,
    val description: String,
    val color: Long,
    val type: QuizType
) {
    companion object {
        // Convenience accessors for quiz types
        val NotLearned get() = quizTypes[0]
        val Random get() = quizTypes[1]
        val LeastCorrect get() = quizTypes[2]
        val LeastWrong get() = quizTypes[3]
        val LeastRecent get() = quizTypes[4]
        val LeastReviewed get() = quizTypes[5]
        val MostCorrect get() = quizTypes[6]
        val MostWrong get() = quizTypes[7]
        val MostRecent get() = quizTypes[8]
        val MostReviewed get() = quizTypes[9]

        val quizTypes = listOf(
            // Quiz types with their IDs, names, descriptions, colors, and types
            // Warning: First quiz type must be "Not Learned" with ID 1
            Quiz(
                1,
                "Not Learned",
                "Test your knowledge with words you haven't learned yet",
                0xFFB71C1C,
                QuizType.NOT_LEARNED
            ),
            Quiz(
                2,
                "Random",
                "Test your knowledge with random questions",
                0xFFFF9800,
                QuizType.RANDOM
            ),
            Quiz(
                3,
                "Least Correct",
                "Test your knowledge with least correct answers",
                0xFF4CAF50,
                QuizType.LEAST_CORRECT
            ),
            Quiz(
                4,
                "Least Wrong",
                "Test your knowledge with least wrong answers",
                0xFF2196F3,
                QuizType.LEAST_WRONG
            ),
            Quiz(
                5,
                "Least Recent",
                "Test your knowledge with least recent words",
                0xFF9C27B0,
                QuizType.LEAST_RECENT
            ),
            Quiz(
                6,
                "Least Reviewed",
                "Test your knowledge with less reviewed words",
                0xFFE91E63,
                QuizType.LEAST_REVIEWED
            ),
            Quiz(
                7,
                "Most Correct",
                "Test your knowledge with most correct answers",
                0xFF795548,
                QuizType.MOST_CORRECT
            ),
            Quiz(
                8,
                "Most Wrong",
                "Test your knowledge with most wrong answers",
                0xFF607D8B,
                QuizType.MOST_WRONG
            ),
            Quiz(
                9,
                "Most Recent",
                "Test your knowledge with most recent words",
                0xFF9E9E9E,
                QuizType.MOST_RECENT
            ),
            Quiz(
                10,
                "Most Reviewed",
                "Test your knowledge with most reviewed words",
                0xFFCDDC39,
                QuizType.MOST_REVIEWED
            ),

            )
    }
}
