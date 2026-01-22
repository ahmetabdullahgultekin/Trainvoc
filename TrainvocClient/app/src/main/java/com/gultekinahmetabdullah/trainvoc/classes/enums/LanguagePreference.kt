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
 * - Spanish (es) - Sprint 7
 * - German (de) - Sprint 7
 * - French (fr) - Sprint 7
 * - Arabic (ar) - Sprint 7 with RTL support
 */
enum class LanguagePreference(
    val code: String,
    val displayName: String,
    val isRTL: Boolean = false
) {
    ENGLISH("en", "English", false),
    TURKISH("tr", "Türkçe", false),
    SPANISH("es", "Español", false),
    GERMAN("de", "Deutsch", false),
    FRENCH("fr", "Français", false),
    ARABIC("ar", "العربية", true);  // RTL language

    companion object {
        /**
         * Get language preference from code
         */
        fun fromCode(code: String): LanguagePreference {
            return entries.find { it.code == code } ?: ENGLISH
        }

        /**
         * Get all non-RTL languages
         */
        fun getLTRLanguages(): List<LanguagePreference> {
            return entries.filter { !it.isRTL }
        }

        /**
         * Get all RTL languages
         */
        fun getRTLLanguages(): List<LanguagePreference> {
            return entries.filter { it.isRTL }
        }
    }
}