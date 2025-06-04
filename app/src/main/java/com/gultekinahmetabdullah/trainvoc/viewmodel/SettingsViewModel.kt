package com.gultekinahmetabdullah.trainvoc.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(context: Context, private val repository: WordRepository) : ViewModel() {
    private val appContext = context.applicationContext
    private val sharedPreferences: SharedPreferences =
        appContext.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    private val _theme = MutableStateFlow(getTheme())
    val theme: StateFlow<String> = _theme

    private val _language = MutableStateFlow(getLanguage())
    val language: StateFlow<String> = _language

    private val _languageChanged = MutableSharedFlow<Unit>()
    val languageChanged = _languageChanged

    fun getTheme(): String =
        sharedPreferences.getString("theme", "System Default") ?: "System Default"

    fun setTheme(theme: String) {
        sharedPreferences.edit().putString("theme", theme).apply()
        _theme.value = theme
    }

    fun isNotificationsEnabled(): Boolean = sharedPreferences.getBoolean("notifications", true)
    fun setNotificationsEnabled(enabled: Boolean) =
        sharedPreferences.edit().putBoolean("notifications", enabled).apply()

    fun getLanguage(): String = sharedPreferences.getString("language", "English") ?: "English"
    fun setLanguage(language: String) {
        sharedPreferences.edit().putString("language", language).apply()
        _language.value = language
        setAppLocale(language)
        viewModelScope.launch { _languageChanged.emit(Unit) }
    }

    private fun setAppLocale(language: String) {
        val localeCode = when (language) {
            "Türkçe", "Turkish" -> "tr"
            "English" -> "en"
            else -> "en"
        }
        val locale = java.util.Locale(localeCode)
        java.util.Locale.setDefault(locale)
        val resources = appContext.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun resetProgress() {
        sharedPreferences.edit().clear().apply()
        viewModelScope.launch {
            repository.resetProgress()
        }
    }

    fun logout() {
        sharedPreferences.edit().remove("username").apply()
    }
}