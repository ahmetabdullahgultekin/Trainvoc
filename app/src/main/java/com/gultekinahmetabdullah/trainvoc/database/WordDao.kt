package com.gultekinahmetabdullah.trainvoc.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gultekinahmetabdullah.trainvoc.classes.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY word ASC")
    fun getAllWords(): Flow<List<Word>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word)

    @Query("SELECT SUM(numberOfCorrectAnswers + numberOfWrongAnswers) FROM words")
    fun getTotalAnswers(): LiveData<Int>

    @Query("SELECT AVG((numberOfCorrectAnswers * 1.0) / (numberOfCorrectAnswers + numberOfWrongAnswers) * 100) FROM words WHERE (numberOfCorrectAnswers + numberOfWrongAnswers) > 0")
    fun getCorrectPercentage(): LiveData<Double>

    @Query("SELECT AVG((numberOfWrongAnswers * 1.0) / (numberOfCorrectAnswers + numberOfWrongAnswers) * 100) FROM words WHERE (numberOfCorrectAnswers + numberOfWrongAnswers) > 0")
    fun getWrongPercentage(): LiveData<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: Word)

    @Insert
    suspend fun insertWords(words: List<Word>)

    @Query("SELECT * FROM words ORDER BY numberOfCorrectAnswers ASC LIMIT 4")
    fun getLeastKnownWords(): LiveData<List<Word>>

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(): Word

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT 5")
    suspend fun getRandomFiveWords(): List<Word>
}