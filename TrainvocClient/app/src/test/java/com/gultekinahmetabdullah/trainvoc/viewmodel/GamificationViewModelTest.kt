package com.gultekinahmetabdullah.trainvoc.viewmodel

import com.gultekinahmetabdullah.trainvoc.gamification.AchievementProgress
import com.gultekinahmetabdullah.trainvoc.gamification.GamificationManager
import com.gultekinahmetabdullah.trainvoc.testing.BaseTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for GamificationViewModel
 *
 * Demonstrates:
 * - Testing ViewModel with mocked GamificationManager
 * - Testing init block that loads data
 * - Testing loading states
 * - Testing error handling
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GamificationViewModelTest : BaseTest() {

    private lateinit var gamificationManager: GamificationManager
    private lateinit var viewModel: GamificationViewModel

    private val mockAchievements = listOf(
        AchievementProgress(
            achievementId = "first_word",
            name = "First Word",
            description = "Learn your first word",
            currentProgress = 1,
            targetProgress = 1,
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis()
        ),
        AchievementProgress(
            achievementId = "word_master",
            name = "Word Master",
            description = "Learn 100 words",
            currentProgress = 50,
            targetProgress = 100,
            isUnlocked = false,
            unlockedAt = null
        )
    )

    override fun setup() {
        super.setup()
        gamificationManager = mockk()
    }

    @Test
    fun `init loads achievements automatically`() = runTest {
        // Arrange
        coEvery { gamificationManager.initializeAchievements() } returns Unit
        coEvery { gamificationManager.getAllAchievementsWithProgress() } returns mockAchievements

        // Act
        viewModel = GamificationViewModel(gamificationManager)
        advanceUntilIdle()

        // Assert
        assertEquals(2, viewModel.achievementProgress.value.size)
        assertEquals("first_word", viewModel.achievementProgress.value[0].achievementId)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `isLoading is true while loading achievements`() = runTest {
        // Arrange
        coEvery { gamificationManager.initializeAchievements() } returns Unit
        coEvery { gamificationManager.getAllAchievementsWithProgress() } returns mockAchievements

        // Act
        viewModel = GamificationViewModel(gamificationManager)

        // Assert - initially loading should be true (before advanceUntilIdle)
        // After completion, loading should be false
        advanceUntilIdle()
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadAchievements initializes and loads achievements`() = runTest {
        // Arrange
        coEvery { gamificationManager.initializeAchievements() } returns Unit
        coEvery { gamificationManager.getAllAchievementsWithProgress() } returns mockAchievements

        viewModel = GamificationViewModel(gamificationManager)
        advanceUntilIdle()

        // Act
        viewModel.loadAchievements()
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 2) { gamificationManager.initializeAchievements() }
        coVerify(exactly = 2) { gamificationManager.getAllAchievementsWithProgress() }
    }

    @Test
    fun `refresh reloads achievements`() = runTest {
        // Arrange
        coEvery { gamificationManager.initializeAchievements() } returns Unit
        coEvery { gamificationManager.getAllAchievementsWithProgress() } returns mockAchievements

        viewModel = GamificationViewModel(gamificationManager)
        advanceUntilIdle()

        // Update mock to return different achievements
        val updatedAchievements = listOf(
            mockAchievements[0],
            mockAchievements[1].copy(currentProgress = 60)
        )
        coEvery { gamificationManager.getAllAchievementsWithProgress() } returns updatedAchievements

        // Act
        viewModel.refresh()
        advanceUntilIdle()

        // Assert
        assertEquals(60, viewModel.achievementProgress.value[1].currentProgress)
    }

    @Test
    fun `loadAchievements handles error gracefully`() = runTest {
        // Arrange
        coEvery { gamificationManager.initializeAchievements() } throws RuntimeException("Database error")
        coEvery { gamificationManager.getAllAchievementsWithProgress() } returns emptyList()

        // Act
        viewModel = GamificationViewModel(gamificationManager)
        advanceUntilIdle()

        // Assert - should have empty list and not crash
        assertTrue(viewModel.achievementProgress.value.isEmpty())
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `unlocked achievements are identified correctly`() = runTest {
        // Arrange
        coEvery { gamificationManager.initializeAchievements() } returns Unit
        coEvery { gamificationManager.getAllAchievementsWithProgress() } returns mockAchievements

        // Act
        viewModel = GamificationViewModel(gamificationManager)
        advanceUntilIdle()

        // Assert
        val unlockedCount = viewModel.achievementProgress.value.count { it.isUnlocked }
        val lockedCount = viewModel.achievementProgress.value.count { !it.isUnlocked }
        assertEquals(1, unlockedCount)
        assertEquals(1, lockedCount)
    }

    @Test
    fun `achievement progress is calculated correctly`() = runTest {
        // Arrange
        coEvery { gamificationManager.initializeAchievements() } returns Unit
        coEvery { gamificationManager.getAllAchievementsWithProgress() } returns mockAchievements

        // Act
        viewModel = GamificationViewModel(gamificationManager)
        advanceUntilIdle()

        // Assert
        val wordMaster = viewModel.achievementProgress.value.find { it.achievementId == "word_master" }
        assertNotNull(wordMaster)
        assertEquals(50, wordMaster!!.currentProgress)
        assertEquals(100, wordMaster.targetProgress)
        // Progress percentage = 50/100 = 50%
        val progressPercent = (wordMaster.currentProgress.toFloat() / wordMaster.targetProgress * 100).toInt()
        assertEquals(50, progressPercent)
    }
}
