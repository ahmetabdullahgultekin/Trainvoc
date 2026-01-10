package com.gultekinahmetabdullah.trainvoc.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.gultekinahmetabdullah.trainvoc.ui.games.GameType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for managing tutorial preferences using EncryptedSharedPreferences.
 * Tracks first-play status for each game to show tutorials only once.
 */
@Singleton
class TutorialPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : ITutorialPreferencesRepository {

    companion object {
        private const val PREFS_NAME = "tutorial_prefs"
        private const val KEY_PREFIX_FIRST_PLAY = "first_play_"
    }

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun isFirstPlay(gameType: GameType): Boolean {
        return prefs.getBoolean("$KEY_PREFIX_FIRST_PLAY${gameType.name}", true)
    }

    override fun markTutorialCompleted(gameType: GameType) {
        prefs.edit { putBoolean("$KEY_PREFIX_FIRST_PLAY${gameType.name}", false) }
    }

    override fun resetTutorialStatus(gameType: GameType) {
        prefs.edit { putBoolean("$KEY_PREFIX_FIRST_PLAY${gameType.name}", true) }
    }

    override fun resetAllTutorials() {
        prefs.edit {
            GameType.entries.forEach { gameType ->
                putBoolean("$KEY_PREFIX_FIRST_PLAY${gameType.name}", true)
            }
        }
    }
}
