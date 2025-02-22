package com.gultekinahmetabdullah.trainvoc.classes.word

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel

/**
 * Word entity
 */

@Entity(tableName = "words")
data class Word(
    @PrimaryKey @ColumnInfo(name = "word") val word: String, // One-to-one relationship with the word
    @ColumnInfo(name = "meaning") val meaning: String, // One-to-one relationship with the meaning
    @ColumnInfo(name = "level") val level: WordLevel? = null, // One-to-many relationship with the level
    @ColumnInfo(name = "last_reviewed") val lastReviewed: Long? = null, // One-to-one relationship with the last reviewed
    @ColumnInfo(name = "stat_id") val statId: Int = 1, // One-to-many relationship with the statistic
    @ColumnInfo(name = "seconds_spent") val secondsSpent: Int = 0 // One-to-one relationship with the seconds spent
)

/**
 * Statistic entity
 */

@Entity(
    tableName = "statistics",
    indices = [
        Index(
            value = ["correct_count", "wrong_count", "skipped_count"],
            unique = true
        ),
    ]
)
data class Statistic(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "stat_id") val statId: Int = 0,
    @ColumnInfo(name = "correct_count") val correctCount: Int = 0,
    @ColumnInfo(name = "wrong_count") val wrongCount: Int = 0,
    @ColumnInfo(name = "skipped_count") val skippedCount: Int = 0,
)

/**
 * Exam entity
 */

@Entity(tableName = "exams")
data class Exam(
    @PrimaryKey @ColumnInfo(name = "exam") val exam: String
) {
    companion object {
        val examTypes = listOf(
            Exam("TOEFL"),
            Exam("IELTS"),
            Exam("YDS"),
            Exam("YÖKDİL"),
            Exam("KPDS"),
            Exam("Mixed")
        )

        val examColors = mapOf(
            "TOEFL" to Color(0xFF4CAF50),
            "IELTS" to Color(0xFF2196F3),
            "YDS" to Color(0xFF9C27B0),
            "YÖKDİL" to Color(0xFFE91E63),
            "KPDS" to Color(0xFFE91E63),
            "Mixed" to Color(0xFFFF9800)
        )
    }
}

/**
 * Relations
 *
 * WordWithStats: One-to-many relationship between the word and the statistic
 * WordAskedInExam: One-to-one relationship between the word and the exam
 * ExamWithWords: One-to-one relationship between the exam and the word
 * WordExamCrossRef: Many-to-many relationship between the word and the exam
 */

data class WordWithStats(
    @Embedded val statistic: Statistic,
    @Relation(
        parentColumn = "stat_id",
        entityColumn = "stat_id"
    )
    val words: List<Word>
)

data class WordAskedInExams(
    @Embedded val word: Word,
    @Relation(
        parentColumn = "word",
        entityColumn = "exam"
    )
    val exams: List<Exam>
)

data class ExamWithWords(
    @Embedded val exam: Exam,
    @Relation(
        parentColumn = "exam",
        entityColumn = "word"
    )
    val words: List<Word>
)

/**
 * WordExamCrossRef entity
 */

@Entity(tableName = "word_exam_cross_ref", primaryKeys = ["word", "exam"])
data class WordExamCrossRef(
    val word: String,
    val exam: String
)