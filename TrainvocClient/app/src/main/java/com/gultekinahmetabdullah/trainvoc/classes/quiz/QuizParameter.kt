package com.gultekinahmetabdullah.trainvoc.classes.quiz

import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam

sealed class QuizParameter {
    data class Level(val wordLevel: WordLevel) : QuizParameter()
    data class ExamType(val exam: Exam) : QuizParameter()
}