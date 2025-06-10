package com.gultekinahmetabdullah.trainvoc.classes.quiz

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam

sealed class AvailableQuizCategory(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val descriptionArgRes: Int,
    val color: Color,
    val onClickParam: QuizParameter,
    val enabled: Boolean = true
) {
    class LevelCategory(level: WordLevel) : AvailableQuizCategory(
        titleRes = when (level.name) {
            "A1" -> R.string.level_a1
            "A2" -> R.string.level_a2
            "B1" -> R.string.level_b1
            "B2" -> R.string.level_b2
            "C1" -> R.string.level_c1
            "C2" -> R.string.level_c2
            else -> R.string.unknown_level
        },
        descriptionRes = R.string.test_your_knowledge_with,
        descriptionArgRes = when (level.name) {
            "A1" -> R.string.level_a1
            "A2" -> R.string.level_a2
            "B1" -> R.string.level_b1
            "B2" -> R.string.level_b2
            "C1" -> R.string.level_c1
            "C2" -> R.string.level_c2
            else -> R.string.unknown_level
        },
        color = Color.Companion.Unspecified, // Will be set in UI
        onClickParam = QuizParameter.Level(level)
    )

    class ExamCategory(exam: Exam, enabled: Boolean = true) : AvailableQuizCategory(
        titleRes = when (exam.exam) {
            "YDS" -> R.string.exam_yds
            "TOEFL" -> R.string.exam_toefl
            "IELTS" -> R.string.exam_ielts
            "YÖKDİL" -> R.string.exam_yokdil
            "KPDS" -> R.string.exam_kpds
            "Mixed" -> R.string.exam_mixed
            else -> R.string.exam_generic
        },
        descriptionRes = if (exam.exam == "Mixed") {
            R.string.test_your_knowledge_with_mixed
        } else {
            R.string.test_your_knowledge_with_exam
        },
        descriptionArgRes = when (exam.exam) {
            "YDS" -> R.string.exam_yds
            "TOEFL" -> R.string.exam_toefl
            "IELTS" -> R.string.exam_ielts
            "YÖKDİL" -> R.string.exam_yokdil
            "KPDS" -> R.string.exam_kpds
            "Mixed" -> R.string.exam_mixed
            else -> R.string.exam_generic
        },
        color = Exam.Companion.examColors.entries.firstOrNull { it.key == exam.exam }?.value
            ?: Color.Companion.Unspecified,
        onClickParam = QuizParameter.ExamType(exam),
        enabled = enabled
    )

    companion object {
        fun getAll(): List<AvailableQuizCategory> {
            val levelCategories = WordLevel.entries.map { LevelCategory(it) }
            val examCategories = Exam.Companion.examTypes.map {
                val disabledExams = listOf("TOEFL", "IELTS", "YÖKDİL", "KPDS")
                ExamCategory(it, enabled = it.exam !in disabledExams)
            }
            return levelCategories + examCategories
        }
    }
}