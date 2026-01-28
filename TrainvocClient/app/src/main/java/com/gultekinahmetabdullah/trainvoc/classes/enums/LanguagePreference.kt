package com.gultekinahmetabdullah.trainvoc.classes.enums

/**
 * Language Preference Enum
 *
 * Supported languages for the Trainvoc app.
 * Each language has a code following ISO 639-1 standard.
 *
 * Supported languages:
 * - English (en) - Default
 * - Turkish (tr) - Original app language
 */
enum class LanguagePreference(
    val code: String,
    val displayName: String
) {
    ENGLISH("en", "English"),
    TURKISH("tr", "Türkçe");

    companion object {
        /**
         * Get language preference from code
         */
        fun fromCode(code: String): LanguagePreference {
            return entries.find { it.code == code } ?: ENGLISH
        }

        /**
         * Toggle between English and Turkish
         */
        fun toggle(current: LanguagePreference): LanguagePreference {
            return if (current == ENGLISH) TURKISH else ENGLISH
        }
    }
}