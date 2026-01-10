package com.gultekinahmetabdullah.trainvoc.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import com.gultekinahmetabdullah.trainvoc.gamification.DailyGoal
import com.gultekinahmetabdullah.trainvoc.gamification.GamificationDao
import com.gultekinahmetabdullah.trainvoc.gamification.StreakTracking
import com.gultekinahmetabdullah.trainvoc.gamification.UserAchievement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * HomeViewModel - Provides real data for the Home screen
 *
 * Fetches actual user stats, achievements, streaks, and daily goals
 * from the database instead of showing mock/placeholder data.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gamificationDao: GamificationDao,
    private val wordDao: WordDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                // Load streak data
                val streak = gamificationDao.getStreakTracking() ?: createDefaultStreak()

                // Load daily goals
                val dailyGoal = gamificationDao.getDailyGoal() ?: createDefaultDailyGoal()

                // Load achievements
                val unlockedAchievements = gamificationDao.getUnlockedAchievements()

                // Load word stats
                val totalWords = wordDao.getWordCount()
                val learnedWords = wordDao.getLearnedWordCount()

                // Calculate total score from statistics
                val totalCorrect = wordDao.getCorrectAnswers()
                val totalScore = totalCorrect * 10

                // Calculate level based on XP (totalScore)
                val level = calculateLevel(totalScore)
                val xpForCurrentLevel = getXpForLevel(level)
                val xpForNextLevel = getXpForLevel(level + 1)
                val xpProgress = if (xpForNextLevel > xpForCurrentLevel) {
                    ((totalScore - xpForCurrentLevel).toFloat() / (xpForNextLevel - xpForCurrentLevel)).coerceIn(0f, 1f)
                } else 0f

                _uiState.value = HomeUiState(
                    isLoading = false,
                    currentStreak = streak.currentStreak,
                    longestStreak = streak.longestStreak,
                    totalScore = totalScore,
                    level = level,
                    xpProgress = xpProgress,
                    xpCurrent = totalScore,
                    xpForNextLevel = xpForNextLevel,
                    dailyGoal = dailyGoal,
                    unlockedAchievements = unlockedAchievements,
                    totalWords = totalWords,
                    learnedWords = learnedWords,
                    wordsProgress = if (totalWords > 0) learnedWords.toFloat() / totalWords else 0f
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadHomeData()
    }

    private fun calculateLevel(xp: Int): Int {
        // Level formula: Level = sqrt(XP / 100) + 1
        // Level 1: 0-99 XP
        // Level 2: 100-399 XP
        // Level 3: 400-899 XP
        // etc.
        return (kotlin.math.sqrt(xp.toDouble() / 100) + 1).toInt().coerceAtLeast(1)
    }

    private fun getXpForLevel(level: Int): Int {
        // Inverse of level formula
        return if (level <= 1) 0 else ((level - 1) * (level - 1) * 100)
    }

    private suspend fun createDefaultStreak(): StreakTracking {
        val streak = StreakTracking(
            id = 0,
            userId = "local_user",
            currentStreak = 0,
            longestStreak = 0,
            lastActivityDate = null,
            streakStartDate = null,
            totalActiveDays = 0,
            streakFreezeCount = 0
        )
        gamificationDao.insertStreakTracking(streak)
        return streak
    }

    private suspend fun createDefaultDailyGoal(): DailyGoal {
        val goal = DailyGoal(
            id = 0,
            userId = "local_user",
            wordsGoal = 10,
            reviewsGoal = 20,
            quizzesGoal = 3,
            timeGoalMinutes = 15,
            wordsToday = 0,
            reviewsToday = 0,
            quizzesToday = 0,
            timeTodayMinutes = 0,
            lastResetDate = System.currentTimeMillis(),
            goalsCompletedTotal = 0
        )
        gamificationDao.insertDailyGoal(goal)
        return goal
    }
}

/**
 * UI State for Home Screen
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val error: String? = null,

    // XP & Level
    val totalScore: Int = 0,
    val level: Int = 1,
    val xpProgress: Float = 0f,
    val xpCurrent: Int = 0,
    val xpForNextLevel: Int = 100,

    // Streaks
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,

    // Daily Goals
    val dailyGoal: DailyGoal? = null,

    // Achievements
    val unlockedAchievements: List<UserAchievement> = emptyList(),

    // Word Progress
    val totalWords: Int = 0,
    val learnedWords: Int = 0,
    val wordsProgress: Float = 0f
) {
    // Computed properties for daily tasks
    val quizzesCompleted: Int get() = dailyGoal?.quizzesToday ?: 0
    val quizzesGoal: Int get() = dailyGoal?.quizzesGoal ?: 3
    val quizzesProgress: Float get() = if (quizzesGoal > 0) (quizzesCompleted.toFloat() / quizzesGoal).coerceIn(0f, 1f) else 0f

    val wordsLearnedToday: Int get() = dailyGoal?.wordsToday ?: 0
    val wordsGoalToday: Int get() = dailyGoal?.wordsGoal ?: 10
    val wordsTodayProgress: Float get() = if (wordsGoalToday > 0) (wordsLearnedToday.toFloat() / wordsGoalToday).coerceIn(0f, 1f) else 0f

    val achievementsUnlocked: Int get() = unlockedAchievements.size
}
