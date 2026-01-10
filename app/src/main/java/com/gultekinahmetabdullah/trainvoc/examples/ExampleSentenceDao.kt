package com.gultekinahmetabdullah.trainvoc.examples

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO for example sentence operations
 */
@Dao
interface ExampleSentenceDao {

    @Query("SELECT * FROM example_sentences WHERE word_id = :wordId")
    suspend fun getExamplesForWord(wordId: String): List<ExampleSentence>

    @Query("SELECT * FROM example_sentences WHERE word_id = :wordId")
    fun getExamplesForWordFlow(wordId: String): Flow<List<ExampleSentence>>

    @Query("SELECT * FROM example_sentences WHERE word_id = :wordId AND difficulty = :difficulty")
    suspend fun getExamplesByDifficulty(
        wordId: String,
        difficulty: ExampleDifficulty
    ): List<ExampleSentence>

    @Query("SELECT * FROM example_sentences WHERE word_id = :wordId AND context = :context")
    suspend fun getExamplesByContext(
        wordId: String,
        context: UsageContext
    ): List<ExampleSentence>

    @Query("SELECT * FROM example_sentences WHERE word_id = :wordId AND is_favorite = 1")
    suspend fun getFavoriteExamples(wordId: String): List<ExampleSentence>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExample(example: ExampleSentence): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamples(examples: List<ExampleSentence>)

    @Update
    suspend fun updateExample(example: ExampleSentence)

    @Query("UPDATE example_sentences SET is_favorite = :isFavorite WHERE id = :exampleId")
    suspend fun setFavorite(exampleId: Long, isFavorite: Boolean)

    @Query("DELETE FROM example_sentences WHERE id = :exampleId")
    suspend fun deleteExample(exampleId: Long)

    @Query("DELETE FROM example_sentences WHERE word_id = :wordId")
    suspend fun deleteExamplesForWord(wordId: String)

    @Query("SELECT COUNT(*) FROM example_sentences WHERE word_id = :wordId")
    suspend fun getExampleCount(wordId: String): Int

    @Query("SELECT * FROM example_sentences WHERE source = :source")
    suspend fun getExamplesBySource(source: ExampleSource): List<ExampleSentence>

    @Query("DELETE FROM example_sentences")
    suspend fun clearAllExamples()
}
