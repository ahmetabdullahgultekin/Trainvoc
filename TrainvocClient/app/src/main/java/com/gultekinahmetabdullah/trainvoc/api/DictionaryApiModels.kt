package com.gultekinahmetabdullah.trainvoc.api

import com.google.gson.annotations.SerializedName

/**
 * Data models for Free Dictionary API
 * API: https://dictionaryapi.dev/
 *
 * Example response for "hello":
 * https://api.dictionaryapi.dev/api/v2/entries/en/hello
 */

/**
 * Root response from the Free Dictionary API
 * The API returns an array of word entries
 */
data class DictionaryApiResponse(
    @SerializedName("word")
    val word: String,

    @SerializedName("phonetic")
    val phonetic: String? = null,

    @SerializedName("phonetics")
    val phonetics: List<Phonetic> = emptyList(),

    @SerializedName("meanings")
    val meanings: List<Meaning> = emptyList(),

    @SerializedName("sourceUrls")
    val sourceUrls: List<String> = emptyList()
)

/**
 * Phonetic information for a word
 */
data class Phonetic(
    @SerializedName("text")
    val text: String? = null,

    @SerializedName("audio")
    val audio: String? = null,

    @SerializedName("sourceUrl")
    val sourceUrl: String? = null
)

/**
 * Meaning of a word (contains definitions for a specific part of speech)
 */
data class Meaning(
    @SerializedName("partOfSpeech")
    val partOfSpeech: String,

    @SerializedName("definitions")
    val definitions: List<Definition> = emptyList(),

    @SerializedName("synonyms")
    val synonyms: List<String> = emptyList(),

    @SerializedName("antonyms")
    val antonyms: List<String> = emptyList()
)

/**
 * Definition of a word
 */
data class Definition(
    @SerializedName("definition")
    val definition: String,

    @SerializedName("example")
    val example: String? = null,

    @SerializedName("synonyms")
    val synonyms: List<String> = emptyList(),

    @SerializedName("antonyms")
    val antonyms: List<String> = emptyList()
)

/**
 * Enriched dictionary data (processed from API response)
 * This is what we'll store in our database
 */
data class EnrichedDictionaryData(
    val word: String,
    val ipa: String?,
    val audioUrl: String?,
    val partOfSpeech: String?,
    val definitions: List<String>,
    val examples: List<String>,
    val synonyms: List<String>,
    val antonyms: List<String>,
    val sourceUrl: String?
)

/**
 * Extension function to convert API response to our enriched data model
 */
fun DictionaryApiResponse.toEnrichedData(): EnrichedDictionaryData {
    // Get the best IPA pronunciation (prefer one with audio)
    val bestPhonetic = phonetics.firstOrNull { it.audio?.isNotEmpty() == true }
        ?: phonetics.firstOrNull()

    val ipa = bestPhonetic?.text ?: phonetic
    val audioUrl = bestPhonetic?.audio

    // Get the first meaning's part of speech (most common)
    val partOfSpeech = meanings.firstOrNull()?.partOfSpeech

    // Collect all definitions
    val definitions = meanings.flatMap { meaning ->
        meaning.definitions.map { it.definition }
    }.take(5) // Limit to 5 definitions

    // Collect all examples
    val examples = meanings.flatMap { meaning ->
        meaning.definitions.mapNotNull { it.example }
    }.take(5) // Limit to 5 examples

    // Collect all synonyms (unique)
    val synonyms = meanings.flatMap { it.synonyms }.distinct().take(10)

    // Collect all antonyms (unique)
    val antonyms = meanings.flatMap { it.antonyms }.distinct().take(10)

    // Get first source URL
    val sourceUrl = sourceUrls.firstOrNull()

    return EnrichedDictionaryData(
        word = word,
        ipa = ipa,
        audioUrl = audioUrl,
        partOfSpeech = partOfSpeech,
        definitions = definitions,
        examples = examples,
        synonyms = synonyms,
        antonyms = antonyms,
        sourceUrl = sourceUrl
    )
}
