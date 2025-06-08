package com.gultekinahmetabdullah.trainvoc.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import kotlinx.coroutines.flow.Flow

@Dao
interface StatisticDao {

    // Default insert method
    @Query(
        """
        INSERT INTO statistics (stat_id, correct_count, wrong_count, skipped_count, learned)
        VALUES (0, 0, 0, 0, 0)
           """
    )
    suspend fun insertDefaultStatistic(): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStatistic(statistic: Statistic): Long

    @Update
    suspend fun updateStatistic(stat: Statistic)

    @Query("SELECT * FROM statistics WHERE stat_id = :statId")
    suspend fun getStatisticById(statId: Long): Statistic?

    @Query("SELECT * FROM statistics")
    fun getAllStatistics(): Flow<List<Statistic>>

    @Query("UPDATE statistics SET correct_count = correct_count + 1 WHERE stat_id = :statId")
    suspend fun incrementCorrect(statId: Long)

    @Query("UPDATE statistics SET wrong_count = wrong_count + 1 WHERE stat_id = :statId")
    suspend fun incrementWrong(statId: Long)

    @Query("UPDATE statistics SET skipped_count = skipped_count + 1 WHERE stat_id = :statId")
    suspend fun incrementSkipped(statId: Long)

    @Query("UPDATE statistics SET learned = 1 WHERE stat_id = :statId")
    suspend fun markLearned(statId: Long)

    @Query("DELETE FROM statistics WHERE stat_id > 1")
    suspend fun resetProgress()

    @Query("DELETE FROM statistics WHERE stat_id = :statId")
    suspend fun deleteStatistic(statId: Int)

    @Query(
        """
        SELECT SUM(CASE WHEN s.correct_count > (s.wrong_count + s.skipped_count) THEN 1 ELSE 0 END)
        AS words_with_higher_correct_count
        FROM words w JOIN statistics s ON w.stat_id = s.stat_id
        WHERE w.level = :level
        """
    )
    suspend fun getLevelUnlockerWordCount(level: String): Int

    @Query("SELECT COUNT(*) FROM words WHERE stat_id = :statId")
    suspend fun getWordCountByStatId(statId: Int): Int
}