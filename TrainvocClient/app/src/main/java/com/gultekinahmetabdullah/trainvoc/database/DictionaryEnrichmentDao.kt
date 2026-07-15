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

    // NOTE: synonym reads live in WordDao.getSynonymsForWord (single owner
    // of the both-directions UNION over id pairs); map .word at call sites.

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
