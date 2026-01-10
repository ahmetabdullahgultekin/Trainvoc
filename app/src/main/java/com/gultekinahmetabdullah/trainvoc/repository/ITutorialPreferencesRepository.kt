package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.ui.games.GameType

/**
 * Repository interface for tutorial preferences.
 * Follows Interface Segregation Principle - focused on tutorial concerns only.
 */
interface ITutorialPreferencesRepository {

    /**
     * Check if this is the first time playing a specific game.
     */
    fun isFirstPlay(gameType: GameType): Boolean

    /**
     * Mark a game tutorial as completed.
     */
    fun markTutorialCompleted(gameType: GameType)

    /**
     * Reset tutorial status for a game (enables "Show Tutorial Again").
     */
    fun resetTutorialStatus(gameType: GameType)

    /**
     * Reset all tutorial statuses.
     */
    fun resetAllTutorials()
}
