package com.gultekinahmetabdullah.trainvoc.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.gultekinahmetabdullah.trainvoc.classes.enums.ColorPalettePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.LanguagePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.ThemePreference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for managing user preferences using EncryptedSharedPreferences.
 * Centralizes all preference access and provides type-safe methods with AES-256-GCM encryption.
 *
 * Security:
 * - All preferences encrypted at rest using Android Keystore
 * - AES-256-GCM for values, AES-256-SIV for keys
 * - Keys stored securely in hardware-backed Keystore when available
 */
@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : IPreferencesRepository {

    companion object {
        private const val PREFS_NAME = "secure_user_prefs"
        private const val KEY_USERNAME = "username"
        private const val KEY_THEME = "theme"
        private const val KEY_COLOR_PALETTE = "color_palette"
        private const val KEY_NOTIFICATIONS = "notifications"
        private const val KEY_LANGUAGE = "language"
        // Accessibility keys
        private const val KEY_HIGH_CONTRAST = "high_contrast"
        private const val KEY_COLOR_BLIND_MODE = "color_blind_mode"
        private const val KEY_TEXT_SIZE_SCALE = "text_size_scale"
        private const val KEY_HAPTIC_FEEDBACK = "haptic_feedback"
        private const val KEY_REDUCE_MOTION = "reduce_motion"
    }

    private val prefs: SharedPreferences by lazy {
        // Create or retrieve MasterKey for encryption
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // Create EncryptedSharedPreferences
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

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
        // Use commit() instead of apply() to ensure synchronous write
        // This is critical because activity recreation reads this value immediately
        prefs.edit().putString(KEY_LANGUAGE, language.code).commit()
    }

    override fun clearAll() {
        prefs.edit { clear() }
    }

    override fun clearUsername() {
        prefs.edit { remove(KEY_USERNAME) }
    }

    // Accessibility Settings Implementation

    override fun isHighContrastEnabled(): Boolean =
        prefs.getBoolean(KEY_HIGH_CONTRAST, false)

    override fun setHighContrastEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_HIGH_CONTRAST, enabled) }
    }

    override fun getColorBlindMode(): String? =
        prefs.getString(KEY_COLOR_BLIND_MODE, null)

    override fun setColorBlindMode(mode: String?) {
        prefs.edit {
            if (mode != null) {
                putString(KEY_COLOR_BLIND_MODE, mode)
            } else {
                remove(KEY_COLOR_BLIND_MODE)
            }
        }
    }

    override fun getTextSizeScale(): Float =
        prefs.getFloat(KEY_TEXT_SIZE_SCALE, 1.0f)

    override fun setTextSizeScale(scale: Float) {
        prefs.edit { putFloat(KEY_TEXT_SIZE_SCALE, scale.coerceIn(0.8f, 1.5f)) }
    }

    override fun isHapticFeedbackEnabled(): Boolean =
        prefs.getBoolean(KEY_HAPTIC_FEEDBACK, true)

    override fun setHapticFeedbackEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_HAPTIC_FEEDBACK, enabled) }
    }

    override fun isReduceMotionEnabled(): Boolean =
        prefs.getBoolean(KEY_REDUCE_MOTION, false)

    override fun setReduceMotionEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_REDUCE_MOTION, enabled) }
    }
}
