package com.gultekinahmetabdullah.trainvoc.playgames

import android.app.Activity
import android.content.Context
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.PlayGamesSdk
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages Google Play Games Services sign-in
 *
 * Features:
 * - Automatic sign-in (if previously signed in)
 * - Manual sign-in flow
 * - Sign-out
 * - Sign-in state tracking
 */
@Singleton
class PlayGamesSignInManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Initialize Play Games SDK
     * Call this in Application.onCreate()
     */
    fun initialize() {
        PlayGamesSdk.initialize(context)
    }

    /**
     * Check if user is authenticated
     */
    suspend fun isAuthenticated(): Boolean {
        return try {
            val client = PlayGames.getGamesSignInClient(context as Activity)
            val result = client.isAuthenticated().await()
            result.isAuthenticated
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Sign in silently (no UI if already signed in)
     * Returns true if signed in, false if needs manual sign-in
     */
    suspend fun signInSilently(): Boolean {
        return try {
            val client = PlayGames.getGamesSignInClient(context as Activity)
            val result = client.isAuthenticated().await()

            if (result.isAuthenticated) {
                true
            } else {
                // Try signing in silently
                client.signIn().await()
                true
            }
        } catch (e: Exception) {
            // Silent sign-in failed, user needs to sign in manually
            false
        }
    }

    /**
     * Sign in manually (shows sign-in UI)
     * Call this when user clicks "Sign In" button
     */
    suspend fun signIn(): Result<Unit> {
        return try {
            val client = PlayGames.getGamesSignInClient(context as Activity)
            client.signIn().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign out
     * Note: The new Play Games Services SDK (v2) doesn't provide a signOut() method.
     * Users manage their Google account sign-out through system settings.
     * This method returns success as a no-op for API compatibility.
     */
    @Suppress("RedundantSuspendModifier")
    suspend fun signOut(): Result<Unit> {
        // Play Games Services v2 SDK does not support programmatic sign-out.
        // Users can manage sign-out through Google account settings on the device.
        // We return success to maintain API compatibility.
        return Result.success(Unit)
    }

    /**
     * Get current player ID
     */
    suspend fun getPlayerId(): String? {
        return try {
            val client = PlayGames.getPlayersClient(context as Activity)
            val player = client.currentPlayer.await()
            player.playerId
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get current player display name
     */
    suspend fun getPlayerName(): String? {
        return try {
            val client = PlayGames.getPlayersClient(context as Activity)
            val player = client.currentPlayer.await()
            player.displayName
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Sign-in state
 */
sealed class SignInState {
    object NotSignedIn : SignInState()
    object SigningIn : SignInState()
    data class SignedIn(val playerId: String, val playerName: String) : SignInState()
    data class Error(val message: String) : SignInState()
}
