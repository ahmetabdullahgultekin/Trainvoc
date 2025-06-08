package com.gultekinahmetabdullah.trainvoc.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.enums.LanguagePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.ThemePreference
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(context: Context, private val repository: WordRepository) : ViewModel() {
    private val appContext = context.applicationContext
    private val sharedPreferences: SharedPreferences =
        appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _theme = MutableStateFlow(getTheme())
    val theme: StateFlow<ThemePreference> = _theme

    private val _language = MutableStateFlow(getLanguage())
    val language: StateFlow<LanguagePreference> = _language

    private val _languageChanged = MutableSharedFlow<Unit>()
    val languageChanged = _languageChanged

    private val _notificationsEnabled = MutableStateFlow(isNotificationsEnabled())
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    fun getTheme(): ThemePreference =
        try {
            ThemePreference.valueOf(
                sharedPreferences.getString(
                    "theme",
                    ThemePreference.SYSTEM.name
                ) ?: ThemePreference.SYSTEM.name
            )
        } catch (e: Exception) {
            ThemePreference.SYSTEM
        }

    fun setTheme(theme: ThemePreference) {
        sharedPreferences.edit { putString("theme", theme.name) }
        _theme.value = theme
    }

    fun isNotificationsEnabled(): Boolean = sharedPreferences.getBoolean("notifications", true)
    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit { putBoolean("notifications", enabled) }
        _notificationsEnabled.value = enabled
    }

    fun getLanguage(): LanguagePreference {
        val code = sharedPreferences.getString("language", LanguagePreference.ENGLISH.code)
            ?: LanguagePreference.ENGLISH.code
        return LanguagePreference.entries.find { it.code == code } ?: LanguagePreference.ENGLISH
    }

    fun setLanguage(language: LanguagePreference) {
        sharedPreferences.edit { putString("language", language.code) }
        _language.value = language
        setAppLocale(language.code)
        viewModelScope.launch { _languageChanged.emit(Unit) }
    }

    private fun setAppLocale(languageCode: String) {
        val locale = java.util.Locale(languageCode)
        java.util.Locale.setDefault(locale)
        val resources = appContext.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun resetProgress() {
        // Reset the progress in the repository
        // This will reset the statistics and progress in the database
        // Also reset the all shared preferences
        sharedPreferences.edit {
            remove("username")
            remove("theme")
            remove("language")
            remove("notifications")
            clear()
        }
        viewModelScope.launch {
            repository.resetProgress()
        }
    }

    fun logout() {
        sharedPreferences.edit { remove("username") }
    }
}