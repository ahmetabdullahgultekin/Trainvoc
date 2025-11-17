package com.gultekinahmetabdullah.trainvoc.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.gultekinahmetabdullah.trainvoc.classes.enums.ColorPalettePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.LanguagePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.ThemePreference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for managing user preferences using SharedPreferences.
 * Centralizes all preference access and provides type-safe methods.
 */
@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : IPreferencesRepository {

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_USERNAME = "username"
        private const val KEY_THEME = "theme"
        private const val KEY_COLOR_PALETTE = "color_palette"
        private const val KEY_NOTIFICATIONS = "notifications"
        private const val KEY_LANGUAGE = "language"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getUsername(): String? =
        prefs.getString(KEY_USERNAME, null)

    override fun setUsername(username: String) {
        prefs.edit { putString(KEY_USERNAME, username) }
    }

    override fun getTheme(): ThemePreference {
        val themeName = prefs.getString(KEY_THEME, ThemePreference.SYSTEM.name)
        return try {
            ThemePreference.valueOf(themeName ?: ThemePreference.SYSTEM.name)
        } catch (e: IllegalArgumentException) {
            ThemePreference.SYSTEM
        }
    }

    override fun setTheme(theme: ThemePreference) {
        prefs.edit { putString(KEY_THEME, theme.name) }
    }

    override fun getColorPalette(): ColorPalettePreference {
        val paletteKey = prefs.getString(KEY_COLOR_PALETTE, ColorPalettePreference.DEFAULT.key)
        return ColorPalettePreference.fromKey(paletteKey ?: ColorPalettePreference.DEFAULT.key)
    }

    override fun setColorPalette(palette: ColorPalettePreference) {
        prefs.edit { putString(KEY_COLOR_PALETTE, palette.key) }
    }

    override fun isNotificationsEnabled(): Boolean =
        prefs.getBoolean(KEY_NOTIFICATIONS, true)

    override fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_NOTIFICATIONS, enabled) }
    }

    override fun getLanguage(): LanguagePreference {
        val code = prefs.getString(KEY_LANGUAGE, null)
        return if (code != null) {
            LanguagePreference.entries.find { it.code == code } ?: LanguagePreference.ENGLISH
        } else {
            LanguagePreference.ENGLISH
        }
    }

    override fun setLanguage(language: LanguagePreference) {
        prefs.edit { putString(KEY_LANGUAGE, language.code) }
    }

    override fun clearAll() {
        prefs.edit { clear() }
    }

    override fun clearUsername() {
        prefs.edit { remove(KEY_USERNAME) }
    }
}
