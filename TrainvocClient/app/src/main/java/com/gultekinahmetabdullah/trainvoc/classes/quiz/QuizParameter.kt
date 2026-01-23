package com.gultekinahmetabdullah.trainvoc.classes.quiz

import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam

sealed class QuizParameter {
    data class Level(val wordLevel: WordLevel) : QuizParameter()
    data class ExamType(val exam: Exam) : QuizParameter()

    /**
     * Review mode - quiz with specific words (e.g., missed words from previous quiz)
     * @param wordIds List of word IDs to include in the quiz
     */
    data class Review(val wordIds: List<String>) : QuizParameter()
}