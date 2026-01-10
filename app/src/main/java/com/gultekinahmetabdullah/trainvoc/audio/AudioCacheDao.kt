package com.gultekinahmetabdullah.trainvoc.audio

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO for audio cache operations
 */
@Dao
interface AudioCacheDao {

    @Query("SELECT * FROM audio_cache WHERE word_id = :wordId")
    suspend fun getAudioCache(wordId: String): AudioCache?

    @Query("SELECT * FROM audio_cache WHERE word_id = :wordId")
    fun getAudioCacheFlow(wordId: String): Flow<AudioCache?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioCache(audioCache: AudioCache)

    @Update
    suspend fun updateAudioCache(audioCache: AudioCache)

    @Query("UPDATE audio_cache SET last_accessed = :timestamp, access_count = access_count + 1 WHERE word_id = :wordId")
    suspend fun recordAccess(wordId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM audio_cache ORDER BY last_accessed DESC LIMIT :limit")
    suspend fun getRecentlyUsed(limit: Int = 100): List<AudioCache>

    @Query("SELECT * FROM audio_cache ORDER BY access_count DESC LIMIT :limit")
    suspend fun getMostUsed(limit: Int = 100): List<AudioCache>

    @Query("SELECT SUM(file_size_bytes) FROM audio_cache")
    suspend fun getTotalCacheSize(): Long?

    @Query("SELECT COUNT(*) FROM audio_cache")
    suspend fun getCacheCount(): Int

    @Query("DELETE FROM audio_cache WHERE word_id = :wordId")
    suspend fun deleteAudioCache(wordId: String)

    @Query("DELETE FROM audio_cache WHERE last_accessed < :beforeTimestamp")
    suspend fun deleteOldCache(beforeTimestamp: Long)

    @Query("DELETE FROM audio_cache")
    suspend fun clearAllCache()

    /**
     * LRU cache cleanup - delete least recently used entries when cache is full
     */
    @Query("""
        DELETE FROM audio_cache
        WHERE word_id IN (
            SELECT word_id FROM audio_cache
            ORDER BY last_accessed ASC
            LIMIT :count
        )
    """)
    suspend fun deleteLeastRecentlyUsed(count: Int)
}
