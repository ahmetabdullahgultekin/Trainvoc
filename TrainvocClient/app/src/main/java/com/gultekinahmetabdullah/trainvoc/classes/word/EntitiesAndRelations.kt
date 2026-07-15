package com.gultekinahmetabdullah.trainvoc.classes.word

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel

/**
 * Language entity (schema v18).
 *
 * Vocabulary is multilingual: every word row belongs to a language, and
 * translations link word ids across languages. Seeded ids: en=1, tr=2.
 * Adding a language (e.g. Arabic) is data-only — insert a row here plus
 * word/translation rows; no schema change needed.
 */
@Entity(
    tableName = "languages",
    indices = [Index(value = ["code"], unique = true)]
)
@Immutable
data class Language(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "code") val code: String, // ISO 639-1 ("en", "tr")
    @ColumnInfo(name = "name") val name: String
) {
    companion object {
        const val ENGLISH_ID = 1L
        const val TURKISH_ID = 2L
    }
}

/**
 * Word entity (schema v18: relational, multilingual).
 *
 * Every lemma in every language is one row with a permanent numeric id
 * (seed ids come from tools/dictgen/ids.lock.json; user-added words are
 * allocated ids >= 1_000_000 so seed updates never collide).
 *
 * `meaning` is a denormalized display cache of the primary translations;
 * the source of truth is the word_translations table. It is kept so the
 * existing query surface (quiz generation, lists) works unchanged and
 * will be dropped once the UI reads senses natively.
 *
 * SPACED REPETITION (SM-2, legacy): next_review_date, easiness_factor,
 * interval_days, repetitions persist per word row.
 */
@Entity(
    tableName = "words",
    indices = [
        Index(value = ["word", "language_id"], unique = true),
        Index(value = ["language_id"]),
        Index(value = ["level"]),
        Index(value = ["stat_id"]),
        Index(value = ["last_reviewed"]),
        Index(value = ["next_review_date"]), // For efficient due date queries
        Index(value = ["isFavorite"]) // For efficient favorites queries
    ]
)
@Immutable
data class Word(
    @ColumnInfo(name = "word") val word: String, // the lemma text
    @ColumnInfo(name = "meaning") val meaning: String, // display cache (see class doc)
    @ColumnInfo(name = "level") val level: WordLevel? = null, // CEFR level (EN rows)
    @ColumnInfo(name = "last_reviewed") val lastReviewed: Long? = null,
    @ColumnInfo(name = "stat_id") val statId: Int = 0,
    @ColumnInfo(name = "seconds_spent") val secondsSpent: Int = 0,

    // Spaced repetition fields (SM-2 algorithm)
    @ColumnInfo(name = "next_review_date") val nextReviewDate: Long? = null,
    @ColumnInfo(name = "easiness_factor") val easinessFactor: Float = 2.5f,
    @ColumnInfo(name = "interval_days") val intervalDays: Int = 0,
    @ColumnInfo(name = "repetitions") val repetitions: Int = 0,

    // Favorites fields (Migration 11→12)
    @ColumnInfo(name = "isFavorite") val isFavorite: Boolean = false,
    @ColumnInfo(name = "favoritedAt") val favoritedAt: Long? = null,

    // Part of speech field (Migration 14→15)
    @ColumnInfo(name = "part_of_speech") val partOfSpeech: String? = null,

    // Relational id + language (Migration 17→18)
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "language_id") val languageId: Long = Language.ENGLISH_ID,
    @ColumnInfo(name = "note") val note: String? = null // usage note from the dictionary source
)

/**
 * Cross-lingual translation edge (schema v18).
 *
 * word_id -> translated_word_id, grouped into senses: "take" has
 * sense 0 -> almak, sense 1 -> götürmek, ... The pair direction is as
 * emitted by the seed (EN -> TR); queries follow edges both ways.
 */
@Entity(
    tableName = "word_translations",
    primaryKeys = ["word_id", "translated_word_id", "sense_index"],
    foreignKeys = [
        ForeignKey(
            entity = Word::class,
            parentColumns = ["id"],
            childColumns = ["word_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Word::class,
            parentColumns = ["id"],
            childColumns = ["translated_word_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["word_id"]),
        Index(value = ["translated_word_id"])
    ]
)
@Immutable
data class WordTranslation(
    @ColumnInfo(name = "word_id") val wordId: Long,
    @ColumnInfo(name = "translated_word_id") val translatedWordId: Long,
    @ColumnInfo(name = "sense_index") val senseIndex: Int = 0,
    @ColumnInfo(name = "note") val note: String? = null, // usage constraint ("birini", ...)
    @ColumnInfo(name = "is_primary") val isPrimary: Boolean = false
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
        parentColumn = "id",
        entityColumn = "exam",
        associateBy = Junction(
            WordExamCrossRef::class,
            parentColumn = "word_id",
            entityColumn = "exam"
        )
    )
    val exams: List<Exam>
)

data class ExamWithWords(
    @Embedded val exam: Exam,
    @Relation(
        parentColumn = "exam",
        entityColumn = "id",
        associateBy = Junction(
            WordExamCrossRef::class,
            parentColumn = "exam",
            entityColumn = "word_id"
        )
    )
    val words: List<Word>
)

/**
 * WordExamCrossRef entity (schema v18: keyed by word id)
 */

@Entity(
    tableName = "word_exam_cross_ref",
    primaryKeys = ["word_id", "exam"],
    indices = [
        Index(value = ["word_id"]),
        Index(value = ["exam"])
    ]
)
data class WordExamCrossRef(
    @ColumnInfo(name = "word_id") val wordId: Long,
    @ColumnInfo(name = "exam") val exam: String
)

/**
 * API Cache entity (Phase 7: Dictionary Enrichment)
 *
 * Stores API responses for offline support and to reduce API calls.
 * Cached data expires after 30 days.
 */
@Entity(
    tableName = "api_cache",
    indices = [
        Index(value = ["word"], unique = true),
        Index(value = ["cached_at"])
    ]
)
data class ApiCache(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "word")
    val word: String,

    @ColumnInfo(name = "ipa")
    val ipa: String? = null,

    @ColumnInfo(name = "audio_url")
    val audioUrl: String? = null,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val CACHE_EXPIRY_DAYS = 30
        const val CACHE_EXPIRY_MILLIS = CACHE_EXPIRY_DAYS * 24 * 60 * 60 * 1000L

        fun isExpired(cachedAt: Long): Boolean {
            return System.currentTimeMillis() - cachedAt > CACHE_EXPIRY_MILLIS
        }
    }
}

/**
 * Synonym entity (schema v18: id-based, same-language pairs)
 *
 * Stores pairs of word ids in the SAME language that share a meaning.
 * Pairs are stored once with wordId < synonymWordId; queries look up
 * both directions.
 */
@Entity(
    tableName = "synonyms",
    primaryKeys = ["word_id", "synonym_word_id"],
    foreignKeys = [
        ForeignKey(
            entity = Word::class,
            parentColumns = ["id"],
            childColumns = ["word_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Word::class,
            parentColumns = ["id"],
            childColumns = ["synonym_word_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["word_id"]),
        Index(value = ["synonym_word_id"])
    ]
)
data class Synonym(
    @ColumnInfo(name = "word_id")
    val wordId: Long,

    @ColumnInfo(name = "synonym_word_id")
    val synonymWordId: Long
)

// Note: ExampleSentence entity already exists in com.gultekinahmetabdullah.trainvoc.examples package
// We'll reuse that comprehensive table for Phase 7 dictionary enrichment