package com.gultekinahmetabdullah.trainvoc.repository

import android.content.SharedPreferences

/**
 * One-time migration of profile values that older app versions wrote to
 * plain SharedPreferences files ("user_prefs", "trainvoc_prefs") while the
 * onboarding flow wrote them to the encrypted "secure_user_prefs" store.
 *
 * The split caused the returning-user check to always miss the username,
 * so the app showed the Welcome/Username onboarding on every launch.
 * See PreferencesRepository, which now owns all reads and writes.
 */
object LegacyPrefsMigrator {

    const val KEY_MIGRATED = "legacy_prefs_migrated"

    /** Keys that may exist in the legacy plain stores. */
    private val LEGACY_KEYS = listOf("username", "avatar")

    /**
     * Copies legacy values into [secure] (values already present in
     * [secure] win), then removes them from every legacy store so there
     * is a single source of truth. Runs once, guarded by [KEY_MIGRATED].
     */
    fun migrate(secure: SharedPreferences, legacyStores: List<SharedPreferences>) {
        if (secure.getBoolean(KEY_MIGRATED, false)) return

        val editor = secure.edit()
        for (key in LEGACY_KEYS) {
            if (secure.getString(key, null).isNullOrEmpty()) {
                legacyStores
                    .firstNotNullOfOrNull { store ->
                        store.getString(key, null)?.takeIf { it.isNotEmpty() }
                    }
                    ?.let { editor.putString(key, it) }
            }
        }
        editor.putBoolean(KEY_MIGRATED, true).apply()

        legacyStores.forEach { store ->
            store.edit().apply { LEGACY_KEYS.forEach(::remove) }.apply()
        }
    }
}
