package com.gultekinahmetabdullah.trainvoc.viewmodel

import android.content.Context
import com.gultekinahmetabdullah.trainvoc.classes.enums.ColorPalettePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.LanguagePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.ThemePreference
import com.gultekinahmetabdullah.trainvoc.repository.IPreferencesRepository
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import com.gultekinahmetabdullah.trainvoc.test.util.TestDispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for SettingsViewModel
 *
 * Tests:
 * - Theme preference management
 * - Color palette management
 * - Language preference management
 * - Notifications settings
 * - Progress reset
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var context: Context
    private lateinit var repository: IWordRepository
    private lateinit var preferencesRepository: IPreferencesRepository
    private lateinit var dispatchers: TestDispatcherProvider
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        context = mock()
        repository = mock()
        preferencesRepository = mock()
        dispatchers = TestDispatcherProvider()

        // Setup default returns
        whenever(context.applicationContext).thenReturn(context)
        whenever(preferencesRepository.getTheme()).thenReturn(ThemePreference.SYSTEM)
        whenever(preferencesRepository.getColorPalette()).thenReturn(ColorPalettePreference.BLUE)
        whenever(preferencesRepository.getLanguage()).thenReturn(LanguagePreference.ENGLISH)
        whenever(preferencesRepository.isNotificationsEnabled()).thenReturn(true)
    }

    private fun createViewModel(): SettingsViewModel {
        return SettingsViewModel(
            context = context,
            repository = repository,
            preferencesRepository = preferencesRepository,
            dispatchers = dispatchers
        )
    }

    @Test
    fun `initial state loads from preferences repository`() = runTest {
        // Act
        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert
        assertEquals(ThemePreference.SYSTEM, viewModel.theme.value)
        assertEquals(ColorPalettePreference.BLUE, viewModel.colorPalette.value)
        assertTrue(viewModel.notificationsEnabled.value)
    }

    @Test
    fun `setTheme updates state and saves to repository`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.setTheme(ThemePreference.DARK)

        // Assert
        verify(preferencesRepository).setTheme(ThemePreference.DARK)
        assertEquals(ThemePreference.DARK, viewModel.theme.value)
    }

    @Test
    fun `setTheme to LIGHT updates correctly`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.setTheme(ThemePreference.LIGHT)

        // Assert
        verify(preferencesRepository).setTheme(ThemePreference.LIGHT)
        assertEquals(ThemePreference.LIGHT, viewModel.theme.value)
    }

    @Test
    fun `setColorPalette updates state and saves to repository`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.setColorPalette(ColorPalettePreference.GREEN)

        // Assert
        verify(preferencesRepository).setColorPalette(ColorPalettePreference.GREEN)
        assertEquals(ColorPalettePreference.GREEN, viewModel.colorPalette.value)
    }

    @Test
    fun `setColorPalette to PURPLE updates correctly`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.setColorPalette(ColorPalettePreference.PURPLE)

        // Assert
        verify(preferencesRepository).setColorPalette(ColorPalettePreference.PURPLE)
        assertEquals(ColorPalettePreference.PURPLE, viewModel.colorPalette.value)
    }

    @Test
    fun `setLanguage updates state and saves to repository`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.setLanguage(LanguagePreference.TURKISH)

        // Assert
        verify(preferencesRepository).setLanguage(LanguagePreference.TURKISH)
        assertEquals(LanguagePreference.TURKISH, viewModel.language.value)
    }

    @Test
    fun `setNotificationsEnabled true updates state and saves to repository`() = runTest {
        // Arrange
        whenever(preferencesRepository.isNotificationsEnabled()).thenReturn(false)
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.setNotificationsEnabled(true)

        // Assert
        verify(preferencesRepository).setNotificationsEnabled(true)
        assertTrue(viewModel.notificationsEnabled.value)
    }

    @Test
    fun `setNotificationsEnabled false updates state and saves to repository`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.setNotificationsEnabled(false)

        // Assert
        verify(preferencesRepository).setNotificationsEnabled(false)
        assertFalse(viewModel.notificationsEnabled.value)
    }

    @Test
    fun `getTheme returns current theme from repository`() = runTest {
        // Arrange
        whenever(preferencesRepository.getTheme()).thenReturn(ThemePreference.DARK)
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        val theme = viewModel.getTheme()

        // Assert
        assertEquals(ThemePreference.DARK, theme)
    }

    @Test
    fun `getColorPalette returns current palette from repository`() = runTest {
        // Arrange
        whenever(preferencesRepository.getColorPalette()).thenReturn(ColorPalettePreference.ORANGE)
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        val palette = viewModel.getColorPalette()

        // Assert
        assertEquals(ColorPalettePreference.ORANGE, palette)
    }

    @Test
    fun `getLanguage returns current language from repository`() = runTest {
        // Arrange
        whenever(preferencesRepository.getLanguage()).thenReturn(LanguagePreference.GERMAN)
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        val language = viewModel.getLanguage()

        // Assert
        assertEquals(LanguagePreference.GERMAN, language)
    }

    @Test
    fun `isNotificationsEnabled returns current state from repository`() = runTest {
        // Arrange
        whenever(preferencesRepository.isNotificationsEnabled()).thenReturn(false)
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        val enabled = viewModel.isNotificationsEnabled()

        // Assert
        assertFalse(enabled)
    }

    @Test
    fun `resetProgress clears all preferences and resets database`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.resetProgress()
        advanceUntilIdle()

        // Assert
        verify(preferencesRepository).clearAll()
        verify(repository).resetProgress()
    }

    @Test
    fun `logout clears username`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.logout()

        // Assert
        verify(preferencesRepository).clearUsername()
    }

    @Test
    fun `setLanguage emits languageChanged event`() = runTest {
        // Arrange
        viewModel = createViewModel()
        advanceUntilIdle()

        var eventReceived = false
        val job = kotlinx.coroutines.launch {
            viewModel.languageChanged.collect {
                eventReceived = true
            }
        }

        // Act
        viewModel.setLanguage(LanguagePreference.FRENCH)
        advanceUntilIdle()

        // Assert
        assertTrue(eventReceived)
        job.cancel()
    }
}
