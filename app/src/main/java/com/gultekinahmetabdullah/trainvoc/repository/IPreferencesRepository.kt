package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.classes.enums.LanguagePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.ThemePreference

/**
 * Repository interface for managing user preferences.
 * Abstracts SharedPreferences access to improve testability and maintainability.
 */
interface IPreferencesRepository {
    /**
     * Get the stored username.
     * @return Username string, or null if not set
     */
    fun getUsername(): String?

    /**
     * Set the username.
     * @param username The username to store
     */
    fun setUsername(username: String)

    /**
     * Get the current theme preference.
     * @return ThemePreference enum value, defaults to SYSTEM
     */
    fun getTheme(): ThemePreference

    /**
     * Set the theme preference.
     * @param theme The theme preference to store
     */
    fun setTheme(theme: ThemePreference)

    /**
     * Check if notifications are enabled.
     * @return True if enabled, defaults to true
     */
    fun isNotificationsEnabled(): Boolean

    /**
     * Set notification preference.
     * @param enabled True to enable notifications
     */
    fun setNotificationsEnabled(enabled: Boolean)

    /**
     * Get the current language preference.
     * @return LanguagePreference enum value, defaults to ENGLISH
     */
    fun getLanguage(): LanguagePreference

    /**
     * Set the language preference.
     * @param language The language preference to store
     */
    fun setLanguage(language: LanguagePreference)
}
