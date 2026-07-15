package com.gultekinahmetabdullah.trainvoc.repository

import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for the one-time legacy prefs migration that fixes the
 * "app asks for the name on every launch" bug: onboarding wrote the
 * username to encrypted prefs while the splash routing read it from a
 * plain prefs file, so the value was never found.
 */
class LegacyPrefsMigratorTest {

    private val secure = FakeSharedPreferences()
    private val userPrefs = FakeSharedPreferences()
    private val trainvocPrefs = FakeSharedPreferences()

    private fun migrate() =
        LegacyPrefsMigrator.migrate(secure, listOf(userPrefs, trainvocPrefs))

    @Test
    fun `username in user_prefs is moved into secure store`() {
        userPrefs.edit().putString("username", "Ahmet").apply()

        migrate()

        assertEquals("Ahmet", secure.getString("username", null))
        assertNull(userPrefs.getString("username", null))
    }

    @Test
    fun `username in trainvoc_prefs is moved into secure store`() {
        trainvocPrefs.edit().putString("username", "Ayşe").apply()

        migrate()

        assertEquals("Ayşe", secure.getString("username", null))
        assertNull(trainvocPrefs.getString("username", null))
    }

    @Test
    fun `existing secure value wins over legacy values`() {
        secure.edit().putString("username", "SecureName").apply()
        userPrefs.edit().putString("username", "LegacyName").apply()

        migrate()

        assertEquals("SecureName", secure.getString("username", null))
        assertNull(userPrefs.getString("username", null))
    }

    @Test
    fun `earlier legacy store wins when both contain a value`() {
        userPrefs.edit().putString("username", "FromUserPrefs").apply()
        trainvocPrefs.edit().putString("username", "FromTrainvocPrefs").apply()

        migrate()

        assertEquals("FromUserPrefs", secure.getString("username", null))
    }

    @Test
    fun `avatar is migrated alongside username`() {
        userPrefs.edit().putString("username", "Ahmet").putString("avatar", "🦊").apply()

        migrate()

        assertEquals("🦊", secure.getString("avatar", null))
        assertNull(userPrefs.getString("avatar", null))
    }

    @Test
    fun `migration runs only once`() {
        migrate()
        assertTrue(secure.getBoolean(LegacyPrefsMigrator.KEY_MIGRATED, false))

        // A value appearing later must not be picked up again.
        userPrefs.edit().putString("username", "TooLate").apply()
        migrate()

        assertNull(secure.getString("username", null))
        // Not cleared either, because the migration is a no-op now.
        assertEquals("TooLate", userPrefs.getString("username", null))
    }

    @Test
    fun `blank legacy value is ignored`() {
        userPrefs.edit().putString("username", "").apply()
        trainvocPrefs.edit().putString("username", "Real").apply()

        migrate()

        assertEquals("Real", secure.getString("username", null))
    }

    @Test
    fun `each key resolves independently across stores`() {
        // username only in the second store, avatar only in the first:
        // resolution is per key, not per store.
        trainvocPrefs.edit().putString("username", "Ahmet").apply()
        userPrefs.edit().putString("avatar", "🦊").apply()

        migrate()

        assertEquals("Ahmet", secure.getString("username", null))
        assertEquals("🦊", secure.getString("avatar", null))
    }

    @Test
    fun `value present in both stores is removed from both`() {
        userPrefs.edit().putString("username", "First").apply()
        trainvocPrefs.edit().putString("username", "Second").apply()

        migrate()

        // Single source of truth afterwards: every legacy copy is gone.
        assertNull(userPrefs.getString("username", null))
        assertNull(trainvocPrefs.getString("username", null))
        assertEquals("First", secure.getString("username", null))
    }

    @Test
    fun `secure value wins but legacy copies are still cleared`() {
        secure.edit().putString("username", "SecureName").apply()
        userPrefs.edit().putString("username", "StaleA").apply()
        trainvocPrefs.edit().putString("username", "StaleB").apply()

        migrate()

        assertEquals("SecureName", secure.getString("username", null))
        assertNull(userPrefs.getString("username", null))
        assertNull(trainvocPrefs.getString("username", null))
    }

    @Test
    fun `migration flag is set even when there is nothing to migrate`() {
        migrate()

        assertTrue(secure.getBoolean(LegacyPrefsMigrator.KEY_MIGRATED, false))
        assertNull(secure.getString("username", null))
    }
}

/** Minimal in-memory SharedPreferences for pure-JVM tests. */
private class FakeSharedPreferences : SharedPreferences {
    private val values = mutableMapOf<String, Any?>()

    override fun getAll(): MutableMap<String, *> = values.toMutableMap()
    override fun getString(key: String, defValue: String?): String? =
        values[key] as? String ?: defValue

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? =
        values[key] as? MutableSet<String> ?: defValues

    override fun getInt(key: String, defValue: Int): Int = values[key] as? Int ?: defValue
    override fun getLong(key: String, defValue: Long): Long = values[key] as? Long ?: defValue
    override fun getFloat(key: String, defValue: Float): Float = values[key] as? Float ?: defValue
    override fun getBoolean(key: String, defValue: Boolean): Boolean =
        values[key] as? Boolean ?: defValue

    override fun contains(key: String): Boolean = values.containsKey(key)
    override fun edit(): SharedPreferences.Editor = FakeEditor()
    override fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?
    ) = Unit

    override fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?
    ) = Unit

    private inner class FakeEditor : SharedPreferences.Editor {
        private val pending = mutableMapOf<String, Any?>()
        private val removals = mutableSetOf<String>()
        private var clearAll = false

        override fun putString(key: String, value: String?) = apply { pending[key] = value }
        override fun putStringSet(key: String, values: MutableSet<String>?) =
            apply { pending[key] = values }

        override fun putInt(key: String, value: Int) = apply { pending[key] = value }
        override fun putLong(key: String, value: Long) = apply { pending[key] = value }
        override fun putFloat(key: String, value: Float) = apply { pending[key] = value }
        override fun putBoolean(key: String, value: Boolean) = apply { pending[key] = value }
        override fun remove(key: String) = apply { removals.add(key) }
        override fun clear() = apply { clearAll = true }

        override fun commit(): Boolean {
            apply()
            return true
        }

        override fun apply() {
            if (clearAll) values.clear()
            removals.forEach(values::remove)
            values.putAll(pending)
        }
    }
}
