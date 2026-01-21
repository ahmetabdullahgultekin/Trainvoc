package com.gultekinahmetabdullah.trainvoc.utils

/**
 * Input validation utilities for user inputs
 *
 * Provides validation for text inputs to prevent:
 * - DoS attacks through excessively long inputs
 * - Resource exhaustion
 * - Injection attacks
 */
object InputValidation {

    /**
     * Validation configuration constants
     */
    object Limits {
        const val MAX_TTS_TEXT_LENGTH = 1000
        const val MAX_SEARCH_QUERY_LENGTH = 100
        const val MAX_USERNAME_LENGTH = 50
        const val MAX_WORD_LENGTH = 100
        const val MAX_SENTENCE_LENGTH = 500
        const val MIN_USERNAME_LENGTH = 2
        const val MIN_SEARCH_QUERY_LENGTH = 1
    }

    /**
     * Validation error types
     */
    sealed class ValidationError : Exception() {
        data class Empty(val fieldName: String) : ValidationError() {
            override val message: String = "$fieldName cannot be empty"
        }

        data class TooShort(val fieldName: String, val minLength: Int) : ValidationError() {
            override val message: String = "$fieldName must be at least $minLength characters"
        }

        data class TooLong(val fieldName: String, val maxLength: Int) : ValidationError() {
            override val message: String = "$fieldName cannot exceed $maxLength characters"
        }

        data class InvalidCharacters(val fieldName: String) : ValidationError() {
            override val message: String = "$fieldName contains invalid characters"
        }

        data class InvalidFormat(val fieldName: String, val reason: String) : ValidationError() {
            override val message: String = "$fieldName is invalid: $reason"
        }
    }

    /**
     * Validate text to speech input
     *
     * @param text Text to validate
     * @return Result with validated text or validation error
     */
    fun validateTTSText(text: String): Result<String> {
        return when {
            text.isBlank() -> Result.failure(ValidationError.Empty("TTS text"))
            text.length > Limits.MAX_TTS_TEXT_LENGTH -> Result.failure(
                ValidationError.TooLong("TTS text", Limits.MAX_TTS_TEXT_LENGTH)
            )
            else -> Result.success(text.trim())
        }
    }

    /**
     * Validate search query
     *
     * @param query Search query to validate
     * @return Result with validated query or validation error
     */
    fun validateSearchQuery(query: String): Result<String> {
        val trimmed = query.trim()
        return when {
            trimmed.isEmpty() -> Result.success("") // Empty query is valid (shows all)
            trimmed.length > Limits.MAX_SEARCH_QUERY_LENGTH -> Result.failure(
                ValidationError.TooLong("Search query", Limits.MAX_SEARCH_QUERY_LENGTH)
            )
            else -> Result.success(trimmed)
        }
    }

    /**
     * Validate username
     *
     * @param username Username to validate
     * @return Result with validated username or validation error
     */
    fun validateUsername(username: String): Result<String> {
        val trimmed = username.trim()
        return when {
            trimmed.isBlank() -> Result.failure(ValidationError.Empty("Username"))
            trimmed.length < Limits.MIN_USERNAME_LENGTH -> Result.failure(
                ValidationError.TooShort("Username", Limits.MIN_USERNAME_LENGTH)
            )
            trimmed.length > Limits.MAX_USERNAME_LENGTH -> Result.failure(
                ValidationError.TooLong("Username", Limits.MAX_USERNAME_LENGTH)
            )
            !trimmed.matches(Regex("^[a-zA-Z0-9_\\- ]+\$")) -> Result.failure(
                ValidationError.InvalidCharacters("Username")
            )
            else -> Result.success(trimmed)
        }
    }

    /**
     * Validate word (vocabulary word)
     *
     * @param word Word to validate
     * @return Result with validated word or validation error
     */
    fun validateWord(word: String): Result<String> {
        val trimmed = word.trim()
        return when {
            trimmed.isBlank() -> Result.failure(ValidationError.Empty("Word"))
            trimmed.length > Limits.MAX_WORD_LENGTH -> Result.failure(
                ValidationError.TooLong("Word", Limits.MAX_WORD_LENGTH)
            )
            else -> Result.success(trimmed)
        }
    }

    /**
     * Validate sentence or example
     *
     * @param sentence Sentence to validate
     * @return Result with validated sentence or validation error
     */
    fun validateSentence(sentence: String): Result<String> {
        val trimmed = sentence.trim()
        return when {
            trimmed.isBlank() -> Result.failure(ValidationError.Empty("Sentence"))
            trimmed.length > Limits.MAX_SENTENCE_LENGTH -> Result.failure(
                ValidationError.TooLong("Sentence", Limits.MAX_SENTENCE_LENGTH)
            )
            else -> Result.success(trimmed)
        }
    }

    /**
     * Sanitize text for safe display (prevents XSS if used in webviews)
     *
     * @param text Text to sanitize
     * @return Sanitized text
     */
    fun sanitizeText(text: String): String {
        return text.trim()
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
            .replace("&", "&amp;")
    }

    /**
     * Validate and sanitize combined
     *
     * @param text Text to validate and sanitize
     * @param maxLength Maximum allowed length
     * @param fieldName Name of the field for error messages
     * @return Result with validated and sanitized text
     */
    fun validateAndSanitize(
        text: String,
        maxLength: Int,
        fieldName: String = "Input"
    ): Result<String> {
        val trimmed = text.trim()
        return when {
            trimmed.isBlank() -> Result.failure(ValidationError.Empty(fieldName))
            trimmed.length > maxLength -> Result.failure(
                ValidationError.TooLong(fieldName, maxLength)
            )
            else -> Result.success(sanitizeText(trimmed))
        }
    }
}
