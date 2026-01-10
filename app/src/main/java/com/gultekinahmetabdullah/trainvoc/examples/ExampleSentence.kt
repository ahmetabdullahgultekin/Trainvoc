package com.gultekinahmetabdullah.trainvoc.examples

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Example sentence entity for showing words in context
 */
@Entity(
    tableName = "example_sentences",
    indices = [
        Index(value = ["word_id"]),
        Index(value = ["difficulty"]),
        Index(value = ["context"])
    ]
)
data class ExampleSentence(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "word_id")
    val wordId: String,

    @ColumnInfo(name = "word_text")
    val wordText: String,

    @ColumnInfo(name = "sentence")
    val sentence: String,  // Example sentence in target language

    @ColumnInfo(name = "translation")
    val translation: String,  // Translation to native language

    @ColumnInfo(name = "difficulty")
    val difficulty: ExampleDifficulty,

    @ColumnInfo(name = "context")
    val context: UsageContext,

    @ColumnInfo(name = "source")
    val source: ExampleSource,

    @ColumnInfo(name = "audio_url")
    val audioUrl: String? = null,  // TTS audio for the sentence

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false
)

/**
 * Example difficulty levels
 */
enum class ExampleDifficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

/**
 * Usage context for examples
 */
enum class UsageContext {
    FORMAL,      // Formal speech/writing
    INFORMAL,    // Casual conversation
    SLANG,       // Slang/colloquial
    TECHNICAL,   // Technical/professional
    LITERARY,    // Literary/poetic
    NEUTRAL      // Neutral/general use
}

/**
 * Source of example sentences
 */
enum class ExampleSource {
    TATOEBA,         // Tatoeba Project (free)
    MANUAL,          // Manually curated
    AI_GENERATED,    // AI-generated (GPT-4)
    USER_SUBMITTED   // User contributions
}
