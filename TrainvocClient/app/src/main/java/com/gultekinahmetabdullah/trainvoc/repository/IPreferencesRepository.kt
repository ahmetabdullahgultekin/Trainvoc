package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.classes.enums.ColorPalettePreference
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
     * Get the stored avatar (emoji string).
     * @return Avatar string, or null if not set
     */
    fun getAvatar(): String?

    /**
     * Set the avatar.
     * @param avatar The avatar (emoji string) to store
     */
    fun setAvatar(avatar: String)

    /**
     * Get the learning (dictionary) language code, e.g. "en".
     * This is the language being studied, not the app UI language.
     * @return ISO 639-1 code, defaults to "en"
     */
    fun getLearningLanguage(): String

    /**
     * Set the learning (dictionary) language code.
     * @param code ISO 639-1 code of an available dictionary language
     */
    fun setLearningLanguage(code: String)

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
     * Get the current color palette preference.
     * @return ColorPalettePreference enum value, defaults to DEFAULT
     */
    fun getColorPalette(): ColorPalettePreference

    /**
     * Set the color palette preference.
     * @param palette The color palette preference to store
     */
    fun setColorPalette(palette: ColorPalettePreference)

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

    /**
     * Clear all preferences (used for reset/logout).
     */
    fun clearAll()

    /**
     * Remove the username (logout).
     */
    fun clearUsername()

    // Accessibility Settings

    /**
     * Check if high contrast mode is enabled.
     * @return True if enabled, defaults to false
     */
    fun isHighContrastEnabled(): Boolean

    /**
     * Set high contrast mode preference.
     * @param enabled True to enable high contrast
     */
    fun setHighContrastEnabled(enabled: Boolean)

    /**
     * Get the current color blind mode.
     * @return Color blind mode string ("deuteranopia", "protanopia", "tritanopia"), or null if disabled
     */
    fun getColorBlindMode(): String?

    /**
     * Set color blind mode preference.
     * @param mode Color blind mode string, or null to disable
     */
    fun setColorBlindMode(mode: String?)

    /**
     * Get the current text size scale.
     * @return Text size scale multiplier, defaults to 1.0f (range: 0.8f - 1.5f)
     */
    fun getTextSizeScale(): Float

    /**
     * Set text size scale preference.
     * @param scale Text size scale multiplier (range: 0.8f - 1.5f)
     */
    fun setTextSizeScale(scale: Float)

    /**
     * Check if haptic feedback is enabled.
     * @return True if enabled, defaults to true
     */
    fun isHapticFeedbackEnabled(): Boolean

    /**
     * Set haptic feedback preference.
     * @param enabled True to enable haptic feedback
     */
    fun setHapticFeedbackEnabled(enabled: Boolean)

    /**
     * Check if reduce motion is enabled.
     * @return True if enabled, defaults to false
     */
    fun isReduceMotionEnabled(): Boolean

    /**
     * Set reduce motion preference.
     * @param enabled True to enable reduce motion
     */
    fun setReduceMotionEnabled(enabled: Boolean)

    // Authentication Token Management

    /**
     * Get the stored authentication token.
     * @return Auth token string, or null if not logged in
     */
    fun getAuthToken(): String?

    /**
     * Set the authentication token.
     * @param token The JWT auth token to store
     */
    fun setAuthToken(token: String)

    /**
     * Clear the authentication token (logout).
     */
    fun clearAuthToken()

    /**
     * Get the stored refresh token.
     * @return Refresh token string, or null if not logged in
     */
    fun getRefreshToken(): String?

    /**
     * Set the refresh token.
     * @param token The refresh token to store
     */
    fun setRefreshToken(token: String)

    /**
     * Clear the refresh token.
     */
    fun clearRefreshToken()

    /**
     * Get the unique device ID for sync operations.
     * @return Device ID string (generated if not exists)
     */
    fun getDeviceId(): String
}
