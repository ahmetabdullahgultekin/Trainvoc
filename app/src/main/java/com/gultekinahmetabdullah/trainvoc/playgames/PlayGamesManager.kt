package com.gultekinahmetabdullah.trainvoc.playgames

import android.app.Activity
import android.content.Context
import com.google.android.gms.games.PlayGames
import com.gultekinahmetabdullah.trainvoc.gamification.Achievement
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Main coordinator for Google Play Games Services
 *
 * Features:
 * - Achievement unlocking (44 achievements)
 * - Leaderboard posting
 * - Cloud save/load
 * - Sign-in management
 *
 * Usage:
 * ```
 * // Initialize in Application.onCreate()
 * playGamesManager.initialize()
 *
 * // Sign in
 * playGamesManager.signInSilently()
 *
 * // Unlock achievement
 * playGamesManager.unlockAchievement(Achievement.STREAK_7)
 *
 * // Post to leaderboard
 * playGamesManager.postScore(LeaderboardType.TOTAL_WORDS, 1500)
 *
 * // Sync progress
 * playGamesManager.syncProgress()
 * ```
 */
@Singleton
class PlayGamesManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val signInManager: PlayGamesSignInManager,
    private val cloudSyncManager: PlayGamesCloudSyncManager
) {

    /**
     * Initialize Play Games SDK
     * Call this in Application.onCreate()
     */
    fun initialize() {
        signInManager.initialize()
    }

    // ========== Sign-In ==========

    /**
     * Check if user is authenticated
     */
    suspend fun isAuthenticated(): Boolean {
        return signInManager.isAuthenticated()
    }

    /**
     * Sign in silently (auto sign-in if previously signed in)
     */
    suspend fun signInSilently(): Boolean {
        return signInManager.signInSilently()
    }

    /**
     * Sign in with UI
     */
    suspend fun signIn(): Result<Unit> {
        return signInManager.signIn()
    }

    /**
     * Sign out
     */
    suspend fun signOut(): Result<Unit> {
        return signInManager.signOut()
    }

    /**
     * Get player info
     */
    suspend fun getPlayerInfo(): PlayerInfo? {
        val playerId = signInManager.getPlayerId()
        val playerName = signInManager.getPlayerName()

        return if (playerId != null && playerName != null) {
            PlayerInfo(playerId, playerName)
        } else {
            null
        }
    }

    // ========== Achievements ==========

    /**
     * Unlock achievement
     */
    suspend fun unlockAchievement(achievement: Achievement): Result<Unit> {
        if (!isAuthenticated()) {
            return Result.failure(Exception("Not signed in"))
        }

        return try {
            val playGamesId = PlayGamesAchievementMapper.getPlayGamesId(achievement)
            val client = PlayGames.getAchievementsClient(context as Activity)
            client.unlock(playGamesId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Increment incremental achievement
     * (for achievements that require multiple steps)
     */
    suspend fun incrementAchievement(achievement: Achievement, steps: Int = 1): Result<Unit> {
        if (!isAuthenticated()) {
            return Result.failure(Exception("Not signed in"))
        }

        return try {
            val playGamesId = PlayGamesAchievementMapper.getPlayGamesId(achievement)
            val client = PlayGames.getAchievementsClient(context as Activity)
            client.increment(playGamesId, steps)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Show achievements UI
     */
    suspend fun showAchievementsUI(): Result<Unit> {
        if (!isAuthenticated()) {
            return Result.failure(Exception("Not signed in"))
        }

        return try {
            val client = PlayGames.getAchievementsClient(context as Activity)
            val intent = client.achievementsIntent.await()
            (context as Activity).startActivityForResult(intent, RC_ACHIEVEMENT_UI)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== Leaderboards ==========

    /**
     * Post score to leaderboard
     */
    suspend fun postScore(leaderboardType: LeaderboardType, score: Long): Result<Unit> {
        if (!isAuthenticated()) {
            return Result.failure(Exception("Not signed in"))
        }

        return try {
            val leaderboardId = getLeaderboardId(leaderboardType)
            val client = PlayGames.getLeaderboardsClient(context as Activity)
            client.submitScore(leaderboardId, score)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Show leaderboard UI
     */
    suspend fun showLeaderboardUI(leaderboardType: LeaderboardType): Result<Unit> {
        if (!isAuthenticated()) {
            return Result.failure(Exception("Not signed in"))
        }

        return try {
            val leaderboardId = getLeaderboardId(leaderboardType)
            val client = PlayGames.getLeaderboardsClient(context as Activity)
            val intent = client.getLeaderboardIntent(leaderboardId).await()
            (context as Activity).startActivityForResult(intent, RC_LEADERBOARD_UI)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Show all leaderboards UI
     */
    suspend fun showAllLeaderboardsUI(): Result<Unit> {
        if (!isAuthenticated()) {
            return Result.failure(Exception("Not signed in"))
        }

        return try {
            val client = PlayGames.getLeaderboardsClient(context as Activity)
            val intent = client.allLeaderboardsIntent.await()
            (context as Activity).startActivityForResult(intent, RC_LEADERBOARD_UI)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== Cloud Sync ==========

    /**
     * Save progress to cloud
     */
    suspend fun saveProgressToCloud(): Result<Unit> {
        if (!isAuthenticated()) {
            return Result.failure(Exception("Not signed in"))
        }

        return cloudSyncManager.saveProgressToCloud()
    }

    /**
     * Load progress from cloud
     */
    suspend fun loadProgressFromCloud(): Result<CloudSaveData> {
        if (!isAuthenticated()) {
            return Result.failure(Exception("Not signed in"))
        }

        return cloudSyncManager.loadProgressFromCloud()
    }

    /**
     * Sync progress (smart merge)
     */
    suspend fun syncProgress(): Result<SyncResult> {
        if (!isAuthenticated()) {
            return Result.failure(Exception("Not signed in"))
        }

        return cloudSyncManager.sync()
    }

    // ========== Private Helpers ==========

    private fun getLeaderboardId(type: LeaderboardType): String {
        return when (type) {
            LeaderboardType.TOTAL_WORDS -> "CgkI_trainvoc_leaderboard_total_words"
            LeaderboardType.LONGEST_STREAK -> "CgkI_trainvoc_leaderboard_longest_streak"
            LeaderboardType.TOTAL_QUIZZES -> "CgkI_trainvoc_leaderboard_total_quizzes"
            LeaderboardType.SPEED_MATCH_BEST -> "CgkI_trainvoc_leaderboard_speed_match"
            LeaderboardType.FLIP_CARDS_BEST -> "CgkI_trainvoc_leaderboard_flip_cards"
            LeaderboardType.WEEKLY_WORDS -> "CgkI_trainvoc_leaderboard_weekly_words"
        }
    }

    companion object {
        private const val RC_ACHIEVEMENT_UI = 9001
        private const val RC_LEADERBOARD_UI = 9002
    }
}

/**
 * Player info
 */
data class PlayerInfo(
    val playerId: String,
    val displayName: String
)

/**
 * Leaderboard types
 * These must be created in Play Console
 */
enum class LeaderboardType {
    TOTAL_WORDS,        // Total words learned
    LONGEST_STREAK,     // Longest learning streak
    TOTAL_QUIZZES,      // Total quizzes completed
    SPEED_MATCH_BEST,   // Best speed match time
    FLIP_CARDS_BEST,    // Best flip cards time
    WEEKLY_WORDS        // Words learned this week
}
