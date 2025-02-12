package com.gultekinahmetabdullah.trainvoc.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel

class SettingsViewModel(context: Context) : ViewModel() {
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
    }

    fun logout() {
        sharedPreferences.edit().remove("username").apply()
    }
}
