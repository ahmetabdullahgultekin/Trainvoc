package com.gultekinahmetabdullah.trainvoc.utils

/**
 * Validation utilities for input sanitization and validation
 *
 * Features:
 * - Username validation
 * - Word content validation
 * - CSV injection prevention
 * - File name sanitization
 */
object ValidationUtils {

    // Username validation
    private const val USERNAME_MIN_LENGTH = 2
    private const val USERNAME_MAX_LENGTH = 30
    private val USERNAME_REGEX = Regex("^[a-zA-Z0-9_-]+$")

    // Word validation
    private const val WORD_MAX_LENGTH = 100
    private const val MEANING_MAX_LENGTH = 500

    // CSV injection patterns
    private val CSV_INJECTION_PATTERNS = listOf("=", "+", "-", "@", "\t", "\r")

    /**
     * Validates username format and length
     *
     * Rules:
     * - Length: 2-30 characters
     * - Allowed: letters, numbers, underscore, hyphen
     * - No spaces or special characters
     *
     * @param username Username to validate
     * @return ValidationResult with success or error message
     */
    fun validateUsername(username: String?): ValidationResult {
        return when {
            username.isNullOrBlank() -> {
                ValidationResult.Invalid("Username cannot be empty")
            }
            username.length < USERNAME_MIN_LENGTH -> {
                ValidationResult.Invalid("Username must be at least $USERNAME_MIN_LENGTH characters")
            }
            username.length > USERNAME_MAX_LENGTH -> {
                ValidationResult.Invalid("Username cannot exceed $USERNAME_MAX_LENGTH characters")
            }
            !username.matches(USERNAME_REGEX) -> {
                ValidationResult.Invalid("Username can only contain letters, numbers, underscore, and hyphen")
            }
            else -> {
                ValidationResult.Valid(username.trim())
            }
        }
    }

    /**
     * Validates word content
     *
     * @param word Word to validate
     * @return ValidationResult with success or error message
     */
    fun validateWord(word: String?): ValidationResult {
        return when {
            word.isNullOrBlank() -> {
                ValidationResult.Invalid("Word cannot be empty")
            }
            word.length > WORD_MAX_LENGTH -> {
                ValidationResult.Invalid("Word cannot exceed $WORD_MAX_LENGTH characters")
            }
            containsControlCharacters(word) -> {
                ValidationResult.Invalid("Word contains invalid characters")
            }
            else -> {
                ValidationResult.Valid(word.trim())
            }
        }
    }

    /**
     * Validates word meaning
     *
     * @param meaning Meaning to validate
     * @return ValidationResult with success or error message
     */
    fun validateMeaning(meaning: String?): ValidationResult {
        return when {
            meaning.isNullOrBlank() -> {
                ValidationResult.Invalid("Meaning cannot be empty")
            }
            meaning.length > MEANING_MAX_LENGTH -> {
                ValidationResult.Invalid("Meaning cannot exceed $MEANING_MAX_LENGTH characters")
            }
            containsControlCharacters(meaning) -> {
                ValidationResult.Invalid("Meaning contains invalid characters")
            }
            else -> {
                ValidationResult.Valid(meaning.trim())
            }
        }
    }

    /**
     * Sanitizes CSV input to prevent CSV injection attacks
     *
     * CSV injection occurs when formulas (=, +, -, @) are placed at the start of a cell,
     * which Excel/LibreOffice may execute.
     *
     * @param value Value to sanitize
     * @return Sanitized value safe for CSV export
     */
    fun sanitizeCsvValue(value: String): String {
        var sanitized = value.trim()

        // Check if value starts with potentially dangerous characters
        if (sanitized.isNotEmpty() && CSV_INJECTION_PATTERNS.any { sanitized.startsWith(it) }) {
            // Prepend single quote to prevent formula execution
            sanitized = "'$sanitized"
        }

        // Escape double quotes for CSV format
        sanitized = sanitized.replace("\"", "\"\"")

        return sanitized
    }

    /**
     * Validates and sanitizes file name
     *
     * Removes potentially dangerous characters and path traversal attempts
     *
     * @param fileName File name to validate
     * @return Sanitized file name
     */
    fun sanitizeFileName(fileName: String): String {
        var sanitized = fileName.trim()

        // Remove path traversal attempts
        sanitized = sanitized.replace("..", "_")
        sanitized = sanitized.replace("/", "_")
        sanitized = sanitized.replace("\\", "_")

        // Remove null bytes
        sanitized = sanitized.replace("\u0000", "")

        // Remove control characters
        sanitized = sanitized.filter { !it.isISOControl() }

        // Limit length
        if (sanitized.length > 255) {
            sanitized = sanitized.substring(0, 255)
        }

        return sanitized.ifBlank { "untitled" }
    }

    /**
     * Checks if string contains control characters (except newline and tab)
     *
     * @param text Text to check
     * @return true if contains control characters
     */
    private fun containsControlCharacters(text: String): Boolean {
        return text.any { char ->
            char.isISOControl() && char != '\n' && char != '\t'
        }
    }

    /**
     * Validates backup file extension
     *
     * @param filePath File path to validate
     * @return ValidationResult with success or error
     */
    fun validateBackupFile(filePath: String?): ValidationResult {
        return when {
            filePath.isNullOrBlank() -> {
                ValidationResult.Invalid("File path cannot be empty")
            }
            !filePath.endsWith(".json") && !filePath.endsWith(".enc") && !filePath.endsWith(".csv") -> {
                ValidationResult.Invalid("Invalid backup file format. Supported: .json, .enc, .csv")
            }
            else -> {
                ValidationResult.Valid(filePath)
            }
        }
    }

    /**
     * Validates email format (for future cloud backup features)
     *
     * @param email Email to validate
     * @return ValidationResult with success or error
     */
    fun validateEmail(email: String?): ValidationResult {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

        return when {
            email.isNullOrBlank() -> {
                ValidationResult.Invalid("Email cannot be empty")
            }
            !email.matches(emailRegex) -> {
                ValidationResult.Invalid("Invalid email format")
            }
            else -> {
                ValidationResult.Valid(email.trim().lowercase())
            }
        }
    }
}

/**
 * Result of validation operation
 */
sealed class ValidationResult {
    data class Valid(val value: String) : ValidationResult()
    data class Invalid(val error: String) : ValidationResult()

    fun isValid(): Boolean = this is Valid
    fun isInvalid(): Boolean = this is Invalid

    fun getValueOrNull(): String? = (this as? Valid)?.value
    fun getErrorOrNull(): String? = (this as? Invalid)?.error
}
