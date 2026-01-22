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
}
