package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.gamification.AchievementProgress
import com.gultekinahmetabdullah.trainvoc.gamification.GamificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Gamification features (Achievements, Streaks, Daily Goals)
 */
@HiltViewModel
class GamificationViewModel @Inject constructor(
    private val gamificationManager: GamificationManager
) : ViewModel() {

    private val _achievementProgress = MutableStateFlow<List<AchievementProgress>>(emptyList())
    val achievementProgress: StateFlow<List<AchievementProgress>> = _achievementProgress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadAchievements()
    }

    fun loadAchievements() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Initialize achievements if not already done
                gamificationManager.initializeAchievements()

                // Load all achievements with progress
                val achievements = gamificationManager.getAllAchievementsWithProgress()
                _achievementProgress.value = achievements
            } catch (e: Exception) {
                // Handle error - for now just keep empty list
                _achievementProgress.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        loadAchievements()
    }
}
