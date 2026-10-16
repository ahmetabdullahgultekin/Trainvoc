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

    /** Legacy plain prefs files older app versions wrote profile data to. */
    const val LEGACY_USER_PREFS = "user_prefs"
    const val LEGACY_TRAINVOC_PREFS = "trainvoc_prefs"

    /** Keys that may exist in the legacy plain stores. */
    private val LEGACY_STRING_KEYS = listOf(
        "username",
        "avatar",
        "last_word_of_day",
        "theme",
        "color_palette",
        "language",
        "learning_language",
        "color_blind_mode",
        "auth_token",
        "refresh_token",
        "device_id",
        "notification_time"
    )

    private val LEGACY_INT_KEYS = listOf(
        "reminder_hour",
        "word_of_day_hour",
        "current_streak",
        "longest_streak",
        "word_quiz_interval",
        "quiet_hours_start",
        "quiet_hours_end"
    )

    private val LEGACY_LONG_KEYS = listOf(
        "account_created",
        "last_practice_date",
        "word_of_day_timestamp"
    )

    private val LEGACY_BOOLEAN_KEYS = listOf(
        "notifications",
        "daily_reminders_enabled",
        "streak_alerts_enabled",
        "word_of_day_enabled",
        "word_quiz_enabled",
        "notification_include_learned",
        "notification_low_accuracy",
        "quiet_hours_enabled",
        "high_contrast",
        "haptic_feedback",
        "reduce_motion"
    )

    private val LEGACY_FLOAT_KEYS = listOf(
        "text_size_scale"
    )

    private val LEGACY_STRING_SET_KEYS = listOf(
        "notification_levels",
        "notification_exams"
    )

    /**
     * Copies legacy values into [secure] (values already present in
     * [secure] win), then removes them from every legacy store so there
     * is a single source of truth. Runs once, guarded by [KEY_MIGRATED].
     */
    fun migrate(secure: SharedPreferences, legacyStores: List<SharedPreferences>) {
        if (secure.getBoolean(KEY_MIGRATED, false)) return

        val editor = secure.edit()

        for (key in LEGACY_STRING_KEYS) {
            if (secure.getString(key, null).isNullOrEmpty()) {
                legacyStores
                    .firstNotNullOfOrNull { store ->
                        store.getString(key, null)?.takeIf { it.isNotEmpty() }
                    }
                    ?.let { editor.putString(key, it) }
            }
        }

        for (key in LEGACY_INT_KEYS) {
            if (!secure.contains(key)) {
                legacyStores
                    .firstNotNullOfOrNull { store ->
                        if (store.contains(key)) store.getInt(key, 0) else null
                    }
                    ?.let { editor.putInt(key, it) }
            }
        }

        for (key in LEGACY_LONG_KEYS) {
            if (!secure.contains(key)) {
                legacyStores
                    .firstNotNullOfOrNull { store ->
                        if (store.contains(key)) store.getLong(key, 0L) else null
                    }
                    ?.takeIf { it > 0L }
                    ?.let { editor.putLong(key, it) }
            }
        }

        for (key in LEGACY_BOOLEAN_KEYS) {
            if (!secure.contains(key)) {
                legacyStores
                    .firstNotNullOfOrNull { store ->
                        if (store.contains(key)) store.getBoolean(key, false) else null
                    }
                    ?.let { editor.putBoolean(key, it) }
            }
        }

        for (key in LEGACY_FLOAT_KEYS) {
            if (!secure.contains(key)) {
                legacyStores
                    .firstNotNullOfOrNull { store ->
                        if (store.contains(key)) store.getFloat(key, 1.0f) else null
                    }
                    ?.let { editor.putFloat(key, it) }
            }
        }

        for (key in LEGACY_STRING_SET_KEYS) {
            if (!secure.contains(key)) {
                legacyStores
                    .firstNotNullOfOrNull { store ->
                        if (store.contains(key)) store.getStringSet(key, null) else null
                    }
                    ?.let { editor.putStringSet(key, it.toSet()) }
            }
        }

        editor.putBoolean(KEY_MIGRATED, true).apply()

        legacyStores.forEach { store ->
            store.edit().apply {
                // Keep "language" in plain prefs only where needed by attachBaseContext,
                // remove all other migrated keys so there is no split-brain state.
                (LEGACY_STRING_KEYS - "language").forEach(::remove)
                LEGACY_INT_KEYS.forEach(::remove)
                LEGACY_LONG_KEYS.forEach(::remove)
                LEGACY_BOOLEAN_KEYS.forEach(::remove)
                LEGACY_FLOAT_KEYS.forEach(::remove)
                LEGACY_STRING_SET_KEYS.forEach(::remove)
            }.apply()
        }
    }
}
