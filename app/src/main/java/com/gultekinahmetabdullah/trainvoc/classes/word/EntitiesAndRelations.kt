package com.gultekinahmetabdullah.trainvoc.classes.word

import androidx.compose.runtime.Immutable
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
 *
 * PERFORMANCE: Indices added for frequently queried columns to improve query speed.
 * - level: Used in filtering by word level (A1, A2, B1, etc.)
 * - stat_id: Used in JOIN operations with statistics table
 * - last_reviewed: Used in sorting by review date (LEAST_RECENT, MOST_RECENT)
 * - next_review_date: Used for spaced repetition scheduling
 *
 * SPACED REPETITION (SM-2 Algorithm):
 * - next_review_date: Timestamp for when word should be reviewed next
 * - easiness_factor: Learning difficulty (1.3-3.5, default 2.5)
 * - interval_days: Current interval between reviews
 * - repetitions: Consecutive successful reviews
 */

@Entity(
    tableName = "words",
    indices = [
        Index(value = ["level"]),
        Index(value = ["stat_id"]),
        Index(value = ["last_reviewed"]),
        Index(value = ["next_review_date"]) // For efficient due date queries
    ]
)
@Immutable
data class Word(
    @PrimaryKey
    @ColumnInfo(name = "word") val word: String, // One-to-one relationship with the word
    @ColumnInfo(name = "meaning") val meaning: String, // One-to-one relationship with the meaning
    @ColumnInfo(name = "level") val level: WordLevel? = null, // One-to-many relationship with the level
    @ColumnInfo(name = "last_reviewed") val lastReviewed: Long? = null, // One-to-one relationship with the last reviewed
    @ColumnInfo(name = "stat_id") val statId: Int = 0, // One-to-many relationship with the statistic
    @ColumnInfo(name = "seconds_spent") val secondsSpent: Int = 0, // One-to-one relationship with the seconds spent

    // Spaced repetition fields (SM-2 algorithm)
    @ColumnInfo(name = "next_review_date") val nextReviewDate: Long? = null, // Next review timestamp
    @ColumnInfo(name = "easiness_factor") val easinessFactor: Float = 2.5f, // Learning difficulty (1.3-3.5)
    @ColumnInfo(name = "interval_days") val intervalDays: Int = 0, // Days between reviews
    @ColumnInfo(name = "repetitions") val repetitions: Int = 0 // Consecutive successful reviews
)

/**
 * Statistic entity
 *
 * PERFORMANCE: Indices added for query optimization:
 * - Composite index for uniqueness and lookups by stat values
 * - learned: Used in NOT_LEARNED quiz queries
 * - correct_count: Used in LEAST_CORRECT/MOST_CORRECT queries
 * - wrong_count: Used in LEAST_WRONG/MOST_WRONG queries
 */

@Entity(
    tableName = "statistics",
    indices = [
        Index(
            value = ["correct_count", "wrong_count", "skipped_count", "learned"],
            unique = true
        ),
        Index(value = ["learned"]),
        Index(value = ["correct_count"]),
        Index(value = ["wrong_count"])
    ]
)
@Immutable
data class Statistic(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "stat_id")
    val statId: Int = 0,
    @ColumnInfo(name = "correct_count") val correctCount: Int = 0,
    @ColumnInfo(name = "wrong_count") val wrongCount: Int = 0,
    @ColumnInfo(name = "skipped_count") val skippedCount: Int = 0,
    @ColumnInfo(name = "learned") val learned: Boolean = false
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
            // This must be the last one
            Exam("Mixed")
        )

        val examColors = mapOf(
            "TOEFL" to Color(0xFF4CAF50),
            "IELTS" to Color(0xFF2196F3),
            "YDS" to Color(0xFF9C27B0),
            "YÖKDİL" to Color(0xFFE91E63),
            "KPDS" to Color(0xFFFFC107),
            // "Mixed" should be the last one to ensure it is not overridden
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