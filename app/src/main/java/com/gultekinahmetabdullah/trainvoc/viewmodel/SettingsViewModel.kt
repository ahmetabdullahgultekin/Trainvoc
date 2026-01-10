package com.gultekinahmetabdullah.trainvoc.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.enums.ColorPalettePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.LanguagePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.ThemePreference
import com.gultekinahmetabdullah.trainvoc.repository.IPreferencesRepository
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing application settings.
 * Now uses PreferencesRepository for cleaner architecture and better testability.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: IWordRepository,
    private val preferencesRepository: IPreferencesRepository
) : ViewModel() {
    private val appContext = context.applicationContext

    private val _theme = MutableStateFlow(preferencesRepository.getTheme())
    val theme: StateFlow<ThemePreference> = _theme

    private val _colorPalette = MutableStateFlow(preferencesRepository.getColorPalette())
    val colorPalette: StateFlow<ColorPalettePreference> = _colorPalette

    private val _language = MutableStateFlow(getLanguageWithSystemFallback())
    val language: StateFlow<LanguagePreference> = _language

    private val _languageChanged = MutableSharedFlow<Unit>()
    val languageChanged = _languageChanged

    private val _notificationsEnabled =
        MutableStateFlow(preferencesRepository.isNotificationsEnabled())
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    fun getTheme(): ThemePreference = preferencesRepository.getTheme()

    fun setTheme(theme: ThemePreference) {
        preferencesRepository.setTheme(theme)
        _theme.value = theme
    }

    fun getColorPalette(): ColorPalettePreference = preferencesRepository.getColorPalette()

    fun setColorPalette(palette: ColorPalettePreference) {
        preferencesRepository.setColorPalette(palette)
        _colorPalette.value = palette
    }

    fun isNotificationsEnabled(): Boolean = preferencesRepository.isNotificationsEnabled()

    fun setNotificationsEnabled(enabled: Boolean) {
        preferencesRepository.setNotificationsEnabled(enabled)
        _notificationsEnabled.value = enabled
    }

    /**
     * Get language preference with system fallback.
     * If no language is saved, defaults to system language or Turkish.
     */
    private fun getLanguageWithSystemFallback(): LanguagePreference {
        // Try to get saved language first
        val savedLanguage = preferencesRepository.getLanguage()

        // If it's the default (ENGLISH), check if we should use system language instead
        if (savedLanguage == LanguagePreference.ENGLISH) {
            val systemLang = java.util.Locale.getDefault().language
            return LanguagePreference.entries.find { it.code == systemLang }
                ?: LanguagePreference.TURKISH
        }

        return savedLanguage
    }

    fun getLanguage(): LanguagePreference = preferencesRepository.getLanguage()

    fun setLanguage(language: LanguagePreference) {
        preferencesRepository.setLanguage(language)
        _language.value = language
        // Emit event to trigger activity recreation
        // The locale will be applied via MainActivity.attachBaseContext()
        viewModelScope.launch { _languageChanged.emit(Unit) }
    }

    /**
     * Update the app locale and layout direction based on language preference.
     * Supports RTL languages like Arabic.
     *
     * Note: The actual locale application happens in MainActivity.attachBaseContext()
     * using Context.createConfigurationContext() which is the modern approach.
     * This method sets the default locale for the app and triggers activity recreation.
     */
    fun updateLocale(languageCode: String, activity: android.app.Activity? = null) {
        val locale = java.util.Locale(languageCode)
        java.util.Locale.setDefault(locale)

        // Note: Locale changes are applied via:
        // 1. MainActivity.attachBaseContext() using createConfigurationContext()
        // 2. Activity.recreate() to apply the new configuration
        //
        // The deprecated updateConfiguration is no longer used.
        // The caller should trigger activity recreation after calling this method.
        activity?.recreate()
    }

    fun resetProgress() {
        // Reset all preferences
        preferencesRepository.clearAll()

        // Reset database statistics and progress
        viewModelScope.launch(Dispatchers.IO) {
            repository.resetProgress()
        }
    }

    fun logout() {
        preferencesRepository.clearUsername()
    }
}