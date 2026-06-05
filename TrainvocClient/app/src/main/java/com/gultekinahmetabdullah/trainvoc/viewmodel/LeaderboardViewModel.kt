package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.gamification.GamificationManager
import com.gultekinahmetabdullah.trainvoc.gamification.GamificationStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Leaderboard screen (#194).
 *
 * The global, cross-user online leaderboard depends on the (not-yet-deployed)
 * backend, so for standalone v1 this exposes the user's OWN local gamification
 * stats — a real "personal best" board — instead of an empty placeholder. The
 * online board is surfaced honestly as "coming soon" by the screen.
 */
@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val gamificationManager: GamificationManager
) : ViewModel() {

    private val _stats = MutableStateFlow<GamificationStats?>(null)
    val stats: StateFlow<GamificationStats?> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadStats()
    }

    /**
     * Loads (or reloads) the local personal-best stats.
     */
    fun loadStats() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                gamificationManager.initializeAchievements()
                _stats.value = gamificationManager.getStats()
            } catch (e: Exception) {
                _stats.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}
