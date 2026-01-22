package com.gultekinahmetabdullah.trainvoc.features

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import kotlinx.coroutines.flow.Flow

/**
 * Word of the Day entity
 *
 * Stores the daily featured word that rotates at midnight.
 * Each day has exactly one word of the day.
 */
@Entity(
    tableName = "word_of_day",
    foreignKeys = [
        ForeignKey(
            entity = Word::class,
            parentColumns = ["word"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["date"], unique = true),
        Index(value = ["wordId"])
    ]
)
data class WordOfDay(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "wordId")
    val wordId: String,

    @ColumnInfo(name = "date")
    val date: String, // Format: "YYYY-MM-DD"

    @ColumnInfo(name = "wasViewed")
    val wasViewed: Boolean = false
)

/**
 * DAO for Word of the Day operations
 */
@Dao
interface WordOfDayDao {

    /**
     * Get the word of the day for a specific date
     */
    @Query("SELECT * FROM word_of_day WHERE date = :date LIMIT 1")
    suspend fun getWordOfDay(date: String): WordOfDay?

    /**
     * Insert a new word of the day
     * On conflict (same date), replace the existing entry
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordOfDay(wordOfDay: WordOfDay)

    /**
     * Mark word of the day as viewed
     */
    @Query("UPDATE word_of_day SET wasViewed = 1 WHERE date = :date")
    suspend fun markAsViewed(date: String)

    /**
     * Get all words of the day (for history view)
     */
    @Query("SELECT * FROM word_of_day ORDER BY date DESC LIMIT 30")
    fun getWordOfDayHistory(): Flow<List<WordOfDay>>

    /**
     * Delete old word of the day entries (older than 30 days)
     */
    @Query("DELETE FROM word_of_day WHERE date < :cutoffDate")
    suspend fun deleteOldEntries(cutoffDate: String)
}
