package com.gultekinahmetabdullah.trainvoc.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import kotlinx.coroutines.launch

class SettingsViewModel(context: Context, private val repository: WordRepository) : ViewModel() {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    fun getTheme(): String =
        sharedPreferences.getString("theme", "System Default") ?: "System Default"

    fun setTheme(theme: String) = sharedPreferences.edit().putString("theme", theme).apply()

    fun isNotificationsEnabled(): Boolean = sharedPreferences.getBoolean("notifications", true)
    fun setNotificationsEnabled(enabled: Boolean) =
        sharedPreferences.edit().putBoolean("notifications", enabled).apply()

    fun getLanguage(): String = sharedPreferences.getString("language", "English") ?: "English"
    fun setLanguage(language: String) =
        sharedPreferences.edit().putString("language", language).apply()

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
