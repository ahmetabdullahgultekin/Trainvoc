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
        val quizTypes = listOf(
            Quiz(
                1,
                "Least Correct",
                "Test your knowledge with least correct answers",
                0xFF4CAF50,
                QuizType.LEAST_CORRECT
            ),
            Quiz(
                2,
                "Least Wrong",
                "Test your knowledge with least wrong answers",
                0xFF2196F3,
                QuizType.LEAST_WRONG
            ),
            Quiz(
                3,
                "Least Recent",
                "Test your knowledge with least recent words",
                0xFF9C27B0,
                QuizType.LEAST_RECENT
            ),
            Quiz(
                4,
                "Least Reviewed",
                "Test your knowledge with less reviewed words",
                0xFFE91E63,
                QuizType.LEAST_REVIEWED
            ),
            Quiz(
                5,
                "Random",
                "Test your knowledge with random questions",
                0xFFFF9800,
                QuizType.RANDOM
            ),
            Quiz(
                6,
                "Most Correct",
                "Test your knowledge with most correct answers",
                0xFF795548,
                QuizType.MOST_CORRECT
            ),
            Quiz(
                7,
                "Most Wrong",
                "Test your knowledge with most wrong answers",
                0xFF607D8B,
                QuizType.MOST_WRONG
            ),
            Quiz(
                8,
                "Most Recent",
                "Test your knowledge with most recent words",
                0xFF9E9E9E,
                QuizType.MOST_RECENT
            ),
            Quiz(
                9,
                "Most Reviewed",
                "Test your knowledge with most reviewed words",
                0xFFCDDC39,
                QuizType.MOST_REVIEWED
            )
        )
    }
}
