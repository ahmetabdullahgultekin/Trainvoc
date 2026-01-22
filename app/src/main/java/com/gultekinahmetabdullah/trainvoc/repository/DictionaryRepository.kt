package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.api.DictionaryApiService
import com.gultekinahmetabdullah.trainvoc.api.EnrichedDictionaryData
import com.gultekinahmetabdullah.trainvoc.api.toEnrichedData
import com.gultekinahmetabdullah.trainvoc.classes.word.ApiCache
import com.gultekinahmetabdullah.trainvoc.classes.word.Synonym
import com.gultekinahmetabdullah.trainvoc.database.DictionaryEnrichmentDao
import com.gultekinahmetabdullah.trainvoc.examples.ExampleSentence
import com.gultekinahmetabdullah.trainvoc.examples.ExampleSentenceDao
import com.gultekinahmetabdullah.trainvoc.examples.ExampleDifficulty
import com.gultekinahmetabdullah.trainvoc.examples.ExampleSource
import com.gultekinahmetabdullah.trainvoc.examples.UsageContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for dictionary enrichment (Phase 7)
 *
 * Implements offline-first strategy:
 * 1. Check cache first (30-day expiry)
 * 2. If cache miss or expired, call API
 * 3. Store in cache for future use
 * 4. Return data with graceful fallbacks
 */
@Singleton
class DictionaryRepository @Inject constructor(
    private val dictionaryApiService: DictionaryApiService,
    private val dictionaryEnrichmentDao: DictionaryEnrichmentDao,
    private val exampleSentenceDao: ExampleSentenceDao
) {

    /**
     * Get enriched dictionary data for a word (offline-first)
     *
     * @param word The word to look up
     * @return EnrichedDictionaryData with IPA, examples, synonyms, etc. (nullable fields for graceful degradation)
     */
    suspend fun getEnrichedData(word: String): EnrichedDictionaryData? = withContext(Dispatchers.IO) {
        try {
            val normalizedWord = word.lowercase().trim()

            // 1. Check cache first
            val cached = dictionaryEnrichmentDao.getCachedData(normalizedWord)
            if (cached != null && !ApiCache.isExpired(cached.cachedAt)) {
                // Cache hit and not expired - use cached data
                return@withContext buildEnrichedDataFromCache(normalizedWord, cached)
            }

            // 2. Cache miss or expired - call API
            val apiResponse = try {
                val response = dictionaryApiService.getWordDefinition(normalizedWord)
                if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                    response.body()?.first()
                } else {
                    null
                }
            } catch (e: Exception) {
                // Network error - use stale cache if available
                if (cached != null) {
                    return@withContext buildEnrichedDataFromCache(normalizedWord, cached)
                }
                null
            }

            if (apiResponse == null) {
                // API returned nothing - return cached data if available, else null
                return@withContext if (cached != null) {
                    buildEnrichedDataFromCache(normalizedWord, cached)
                } else {
                    null
                }
            }

            // 3. Store in cache
            val enrichedData = apiResponse.toEnrichedData()
            cacheEnrichedData(normalizedWord, enrichedData)

            enrichedData
        } catch (e: Exception) {
            // Unexpected error - return null (graceful degradation)
            null
        }
    }

    /**
     * Get IPA pronunciation for a word
     */
    suspend fun getIPA(word: String): String? = withContext(Dispatchers.IO) {
        getEnrichedData(word)?.ipa
    }

    /**
     * Get synonyms for a word
     */
    suspend fun getSynonyms(word: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val normalizedWord = word.lowercase().trim()

            // Check database first
            val cachedSynonyms = dictionaryEnrichmentDao.getSynonymsForWord(normalizedWord)
            if (cachedSynonyms.isNotEmpty()) {
                return@withContext cachedSynonyms
            }

            // Fetch from API if not cached
            val enrichedData = getEnrichedData(normalizedWord)
            enrichedData?.synonyms ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Get example sentences for a word
     */
    suspend fun getExamples(word: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val normalizedWord = word.lowercase().trim()

            // Check database first
            val cachedExamples = exampleSentenceDao.getExamplesForWord(normalizedWord)
            if (cachedExamples.isNotEmpty()) {
                return@withContext cachedExamples.map { it.sentence }
            }

            // Fetch from API if not cached
            val enrichedData = getEnrichedData(normalizedWord)
            enrichedData?.examples ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Get part of speech for a word
     */
    suspend fun getPartOfSpeech(word: String): String? = withContext(Dispatchers.IO) {
        getEnrichedData(word)?.partOfSpeech
    }

    /**
     * Clear expired cache entries (should be called periodically, e.g., on app start)
     */
    suspend fun clearExpiredCache() = withContext(Dispatchers.IO) {
        val expiryTime = System.currentTimeMillis() - ApiCache.CACHE_EXPIRY_MILLIS
        dictionaryEnrichmentDao.deleteExpiredCache(expiryTime)
    }

    /**
     * Build enriched data from cached API data and database
     */
    private suspend fun buildEnrichedDataFromCache(
        word: String,
        cached: ApiCache
    ): EnrichedDictionaryData {
        val synonyms = dictionaryEnrichmentDao.getSynonymsForWord(word)
        val exampleSentences = exampleSentenceDao.getExamplesForWord(word)
        val examples = exampleSentences.map { it.sentence }

        return EnrichedDictionaryData(
            word = word,
            ipa = cached.ipa,
            audioUrl = cached.audioUrl,
            partOfSpeech = null, // Not stored in cache table
            definitions = emptyList(), // Not stored in cache table
            examples = examples,
            synonyms = synonyms,
            antonyms = emptyList(), // Not stored separately
            sourceUrl = null
        )
    }

    /**
     * Cache enriched data to database
     */
    private suspend fun cacheEnrichedData(word: String, data: EnrichedDictionaryData) {
        // Cache API response (IPA and audio URL)
        val apiCache = ApiCache(
            word = word,
            ipa = data.ipa,
            audioUrl = data.audioUrl,
            cachedAt = System.currentTimeMillis()
        )
        dictionaryEnrichmentDao.insertCachedData(apiCache)

        // Cache synonyms (bulk insert)
        if (data.synonyms.isNotEmpty()) {
            val synonymEntities = data.synonyms.map { synonym ->
                Synonym(
                    word = word,
                    synonym = synonym,
                    addedAt = System.currentTimeMillis()
                )
            }
            dictionaryEnrichmentDao.insertSynonyms(synonymEntities)
        }

        // Cache examples (using ExampleSentence entity)
        if (data.examples.isNotEmpty()) {
            val exampleEntities = data.examples.map { example ->
                ExampleSentence(
                    wordId = word,
                    wordText = word,
                    sentence = example,
                    translation = "", // Not provided by API
                    difficulty = ExampleDifficulty.INTERMEDIATE,
                    context = UsageContext.NEUTRAL,
                    source = ExampleSource.MANUAL, // From Free Dictionary API
                    audioUrl = null
                )
            }
            exampleSentenceDao.insertExamples(exampleEntities)
        }
    }
}
