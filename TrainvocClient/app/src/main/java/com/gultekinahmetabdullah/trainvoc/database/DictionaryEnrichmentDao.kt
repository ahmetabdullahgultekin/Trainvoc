package com.gultekinahmetabdullah.trainvoc.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gultekinahmetabdullah.trainvoc.classes.word.ApiCache
import com.gultekinahmetabdullah.trainvoc.classes.word.Synonym

/**
 * DAO for dictionary enrichment data (Phase 7)
 *
 * Handles API cache and synonyms.
 * Note: Example sentences use the existing ExampleSentenceDao
 */
@Dao
interface DictionaryEnrichmentDao {

    // ================== API CACHE ==================

    /**
     * Get cached API data for a word
     */
    @Query("SELECT * FROM api_cache WHERE word = :word LIMIT 1")
    suspend fun getCachedData(word: String): ApiCache?

    /**
     * Insert or update cached API data
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedData(cache: ApiCache)

    /**
     * Delete expired cache entries
     */
    @Query("DELETE FROM api_cache WHERE cached_at < :expiryTime")
    suspend fun deleteExpiredCache(expiryTime: Long)

    /**
     * Clear all cache
     */
    @Query("DELETE FROM api_cache")
    suspend fun clearAllCache()

    // ================== SYNONYMS ==================

    /**
     * Get all synonym lemmas for a word id (schema v18: id-based pairs,
     * stored once with word_id < synonym_word_id; follow both directions).
     */
    @Query(
        """
        SELECT w.word FROM synonyms s JOIN words w ON w.id = s.synonym_word_id
        WHERE s.word_id = :wordId
        UNION
        SELECT w.word FROM synonyms s JOIN words w ON w.id = s.word_id
        WHERE s.synonym_word_id = :wordId
        ORDER BY 1 ASC
        """
    )
    suspend fun getSynonymsForWord(wordId: Long): List<String>

    /**
     * Insert synonyms for a word (bulk insert)
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSynonyms(synonyms: List<Synonym>)

    /**
     * Delete all synonym pairs touching a word id
     */
    @Query("DELETE FROM synonyms WHERE word_id = :wordId OR synonym_word_id = :wordId")
    suspend fun deleteSynonymsForWord(wordId: Long)

    /**
     * Clear all synonyms
     */
    @Query("DELETE FROM synonyms")
    suspend fun clearAllSynonyms()
}
