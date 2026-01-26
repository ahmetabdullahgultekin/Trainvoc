package com.gultekinahmetabdullah.trainvoc.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import com.gultekinahmetabdullah.trainvoc.gamification.DailyGoal
import com.gultekinahmetabdullah.trainvoc.gamification.GamificationDao
import com.gultekinahmetabdullah.trainvoc.gamification.StreakTracking
import com.gultekinahmetabdullah.trainvoc.gamification.UserAchievement
import com.gultekinahmetabdullah.trainvoc.ui.screen.progress.LevelProgress
import com.gultekinahmetabdullah.trainvoc.ui.screen.progress.ReviewSchedule
import com.gultekinahmetabdullah.trainvoc.ui.screen.progress.WordStatusCounts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
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

                // Load total study time (in seconds, convert to minutes)
                val totalStudyTimeSeconds = wordDao.getTotalTimeSpent()
                val totalStudyTimeMinutes = totalStudyTimeSeconds / 60

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

                // Load progress by level (A1-C2)
                val levelProgress = WordLevel.entries.map { level ->
                    LevelProgress(
                        level = level,
                        learned = wordDao.getLearnedWordCountByLevel(level.name),
                        total = wordDao.getWordCountByLevel(level.name)
                    )
                }

                // Load word status breakdown
                val wordStatusCounts = WordStatusCounts(
                    mastered = wordDao.getMasteredWordCount(),
                    learning = wordDao.getLearningWordCount(),
                    struggling = wordDao.getStrugglingWordCount(),
                    notStarted = wordDao.getNotStartedWordCount()
                )

                // Load review schedule (spaced repetition)
                val now = LocalDate.now()
                val todayEnd = now.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val tomorrowEnd = now.plusDays(1).atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val weekEnd = now.plusDays(7).atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val monthEnd = now.plusDays(30).atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                val reviewSchedule = ReviewSchedule(
                    today = wordDao.getWordsToReviewByDate(todayEnd),
                    tomorrow = wordDao.getWordsToReviewInRange(todayEnd, tomorrowEnd),
                    thisWeek = wordDao.getWordsToReviewInRange(tomorrowEnd, weekEnd),
                    thisMonth = wordDao.getWordsToReviewInRange(weekEnd, monthEnd)
                )

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
                    wordsProgress = if (totalWords > 0) learnedWords.toFloat() / totalWords else 0f,
                    levelProgress = levelProgress,
                    wordStatusCounts = wordStatusCounts,
                    reviewSchedule = reviewSchedule,
                    totalStudyTimeMinutes = totalStudyTimeMinutes,
                    totalCorrectAnswers = totalCorrect
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
        val streak = StreakTracking.initial()
        gamificationDao.insertStreakTracking(streak)
        return streak
    }

    private suspend fun createDefaultDailyGoal(): DailyGoal {
        val goal = DailyGoal.default()
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
    val wordsProgress: Float = 0f,

    // Detailed Progress (for WordProgressScreen)
    val levelProgress: List<LevelProgress> = emptyList(),
    val wordStatusCounts: WordStatusCounts? = null,
    val reviewSchedule: ReviewSchedule? = null,

    // Total accumulated stats (for Profile)
    val totalStudyTimeMinutes: Int = 0,
    val totalCorrectAnswers: Int = 0
) {
    // Computed properties for daily tasks
    val quizzesCompleted: Int get() = dailyGoal?.quizzesToday ?: 0
    val quizzesGoal: Int get() = dailyGoal?.quizzesGoal ?: 3
    val quizzesProgress: Float get() = if (quizzesGoal > 0) (quizzesCompleted.toFloat() / quizzesGoal).coerceIn(0f, 1f) else 0f

    val wordsLearnedToday: Int get() = dailyGoal?.wordsToday ?: 0
    val wordsGoalToday: Int get() = dailyGoal?.wordsGoal ?: 10
    val wordsTodayProgress: Float get() = if (wordsGoalToday > 0) (wordsLearnedToday.toFloat() / wordsGoalToday).coerceIn(0f, 1f) else 0f

    // Study time goals
    val studyTimeToday: Int get() = dailyGoal?.timeTodayMinutes ?: 0
    val studyTimeGoal: Int get() = dailyGoal?.timeGoalMinutes ?: 30
    val studyTimeProgress: Float get() = if (studyTimeGoal > 0) (studyTimeToday.toFloat() / studyTimeGoal).coerceIn(0f, 1f) else 0f

    // Reviews goals
    val reviewsToday: Int get() = dailyGoal?.reviewsToday ?: 0
    val reviewsGoal: Int get() = dailyGoal?.reviewsGoal ?: 20
    val reviewsProgress: Float get() = if (reviewsGoal > 0) (reviewsToday.toFloat() / reviewsGoal).coerceIn(0f, 1f) else 0f

    val achievementsUnlocked: Int get() = unlockedAchievements.size
}
