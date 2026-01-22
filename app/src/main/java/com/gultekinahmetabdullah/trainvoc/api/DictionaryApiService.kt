package com.gultekinahmetabdullah.trainvoc.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Free Dictionary API Service
 *
 * Base URL: https://api.dictionaryapi.dev/api/v2/
 * Documentation: https://dictionaryapi.dev/
 *
 * Features:
 * - No API key required
 * - Free, unlimited requests
 * - Returns IPA pronunciation, definitions, examples, synonyms, antonyms
 * - Audio pronunciations included
 */
interface DictionaryApiService {

    /**
     * Get dictionary entry for a word
     *
     * @param word The word to look up (lowercase recommended)
     * @return List of dictionary entries (usually 1, but can be multiple for homonyms)
     *
     * Example: https://api.dictionaryapi.dev/api/v2/entries/en/hello
     *
     * Response codes:
     * - 200: Success
     * - 404: Word not found
     */
    @GET("entries/en/{word}")
    suspend fun getWordDefinition(
        @Path("word") word: String
    ): Response<List<DictionaryApiResponse>>
}
