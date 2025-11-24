package com.gultekinahmetabdullah.trainvoc.viewmodel

import android.content.Context
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.gultekinahmetabdullah.trainvoc.classes.enums.ColorPalettePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.LanguagePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.ThemePreference
import com.gultekinahmetabdullah.trainvoc.repository.IPreferencesRepository
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import com.gultekinahmetabdullah.trainvoc.testing.BaseTest
import com.gultekinahmetabdullah.trainvoc.testing.TestDispatcherProvider
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for SettingsViewModel
 *
 * Tests cover:
 * - Theme preference management
 * - Color palette preference management
 * - Language preference management
 * - Notifications toggle
 * - Progress reset
 * - Logout functionality
 * - StateFlow emissions
 *
 * Uses dependency injection with mocked dependencies:
 * - MockK for creating test doubles
 * - Turbine for testing StateFlow emissions
 * - Truth for assertions
 * - TestDispatcherProvider for deterministic coroutine testing
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest : BaseTest() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var mockContext: Context
    private lateinit var mockWordRepository: IWordRepository
    private lateinit var mockPreferencesRepository: IPreferencesRepository
    private lateinit var testDispatchers: TestDispatcherProvider

    @Before
    override fun setup() {
        super.setup()

        // Create mocks
        mockContext = mockk(relaxed = true)
        mockWordRepository = mockk(relaxed = true)
        mockPreferencesRepository = mockk(relaxed = true)
        testDispatchers = TestDispatcherProvider()

        // Setup default mock behaviors
        every { mockPreferencesRepository.getTheme() } returns ThemePreference.SYSTEM
        every { mockPreferencesRepository.getColorPalette() } returns ColorPalettePreference.DEFAULT
        every { mockPreferencesRepository.getLanguage() } returns LanguagePreference.ENGLISH
        every { mockPreferencesRepository.isNotificationsEnabled() } returns true
        every { mockPreferencesRepository.setTheme(any()) } just Runs
        every { mockPreferencesRepository.setColorPalette(any()) } just Runs
        every { mockPreferencesRepository.setLanguage(any()) } just Runs
        every { mockPreferencesRepository.setNotificationsEnabled(any()) } just Runs
        every { mockPreferencesRepository.clearAll() } just Runs
        every { mockPreferencesRepository.clearUsername() } just Runs
        coEvery { mockWordRepository.resetProgress() } just Runs

        // Mock context.applicationContext
        every { mockContext.applicationContext } returns mockContext

        // Create ViewModel with mocked dependencies
        viewModel = SettingsViewModel(
            context = mockContext,
            repository = mockWordRepository,
            preferencesRepository = mockPreferencesRepository,
            dispatchers = testDispatchers
        )
    }

    @Test
    fun `initial state reflects repository values`() {
        // Then - Verify initial state
        assertThat(viewModel.theme.value).isEqualTo(ThemePreference.SYSTEM)
        assertThat(viewModel.colorPalette.value).isEqualTo(ColorPalettePreference.DEFAULT)
        assertThat(viewModel.language.value).isEqualTo(LanguagePreference.ENGLISH)
        assertThat(viewModel.notificationsEnabled.value).isTrue()
    }

    @Test
    fun `setTheme updates repository and state flow`() = runTest {
        // Given
        val newTheme = ThemePreference.DARK

        // When
        viewModel.setTheme(newTheme)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPreferencesRepository.setTheme(newTheme) }
        assertThat(viewModel.theme.value).isEqualTo(newTheme)
    }

    @Test
    fun `setColorPalette updates repository and state flow`() = runTest {
        // Given
        val newPalette = ColorPalettePreference.OCEAN

        // When
        viewModel.setColorPalette(newPalette)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPreferencesRepository.setColorPalette(newPalette) }
        assertThat(viewModel.colorPalette.value).isEqualTo(newPalette)
    }

    @Test
    fun `setLanguage updates repository, state flow, and emits change event`() = runTest {
        // Given
        val newLanguage = LanguagePreference.TURKISH

        // When - Collect languageChanged events
        viewModel.languageChanged.test {
            viewModel.setLanguage(newLanguage)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            verify { mockPreferencesRepository.setLanguage(newLanguage) }
            assertThat(viewModel.language.value).isEqualTo(newLanguage)

            // Verify event was emitted
            awaitItem()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setNotificationsEnabled updates repository and state flow`() = runTest {
        // Given
        val newValue = false

        // When
        viewModel.setNotificationsEnabled(newValue)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPreferencesRepository.setNotificationsEnabled(newValue) }
        assertThat(viewModel.notificationsEnabled.value).isFalse()
    }

    @Test
    fun `getTheme returns value from repository`() {
        // Given
        every { mockPreferencesRepository.getTheme() } returns ThemePreference.AMOLED

        // When
        val theme = viewModel.getTheme()

        // Then
        assertThat(theme).isEqualTo(ThemePreference.AMOLED)
        verify { mockPreferencesRepository.getTheme() }
    }

    @Test
    fun `getColorPalette returns value from repository`() {
        // Given
        every { mockPreferencesRepository.getColorPalette() } returns ColorPalettePreference.LAVENDER

        // When
        val palette = viewModel.getColorPalette()

        // Then
        assertThat(palette).isEqualTo(ColorPalettePreference.LAVENDER)
        verify { mockPreferencesRepository.getColorPalette() }
    }

    @Test
    fun `getLanguage returns value from repository`() {
        // Given
        every { mockPreferencesRepository.getLanguage() } returns LanguagePreference.SPANISH

        // When
        val language = viewModel.getLanguage()

        // Then
        assertThat(language).isEqualTo(LanguagePreference.SPANISH)
        verify { mockPreferencesRepository.getLanguage() }
    }

    @Test
    fun `isNotificationsEnabled returns value from repository`() {
        // Given
        every { mockPreferencesRepository.isNotificationsEnabled() } returns false

        // When
        val enabled = viewModel.isNotificationsEnabled()

        // Then
        assertThat(enabled).isFalse()
        verify { mockPreferencesRepository.isNotificationsEnabled() }
    }

    @Test
    fun `resetProgress clears preferences and resets database`() = runTest {
        // When
        viewModel.resetProgress()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify { mockPreferencesRepository.clearAll() }
        coVerify { mockWordRepository.resetProgress() }
    }

    @Test
    fun `logout clears username from preferences`() {
        // When
        viewModel.logout()

        // Then
        verify { mockPreferencesRepository.clearUsername() }
    }

    @Test
    fun `theme StateFlow emits correct values using Turbine`() = runTest {
        // When/Then - Use Turbine to test emissions
        viewModel.theme.test {
            // Initial value
            assertThat(awaitItem()).isEqualTo(ThemePreference.SYSTEM)

            // Change theme
            viewModel.setTheme(ThemePreference.DARK)
            assertThat(awaitItem()).isEqualTo(ThemePreference.DARK)

            // Change again
            viewModel.setTheme(ThemePreference.LIGHT)
            assertThat(awaitItem()).isEqualTo(ThemePreference.LIGHT)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `colorPalette StateFlow emits correct values using Turbine`() = runTest {
        // When/Then - Use Turbine to test emissions
        viewModel.colorPalette.test {
            // Initial value
            assertThat(awaitItem()).isEqualTo(ColorPalettePreference.DEFAULT)

            // Change palette
            viewModel.setColorPalette(ColorPalettePreference.SUNSET)
            assertThat(awaitItem()).isEqualTo(ColorPalettePreference.SUNSET)

            // Change again
            viewModel.setColorPalette(ColorPalettePreference.MINT)
            assertThat(awaitItem()).isEqualTo(ColorPalettePreference.MINT)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `language StateFlow emits correct values using Turbine`() = runTest {
        // When/Then - Use Turbine to test emissions
        viewModel.language.test {
            // Initial value
            assertThat(awaitItem()).isEqualTo(LanguagePreference.ENGLISH)

            // Change language
            viewModel.setLanguage(LanguagePreference.GERMAN)
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(LanguagePreference.GERMAN)

            // Change again
            viewModel.setLanguage(LanguagePreference.FRENCH)
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(LanguagePreference.FRENCH)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `notificationsEnabled StateFlow emits correct values using Turbine`() = runTest {
        // When/Then - Use Turbine to test emissions
        viewModel.notificationsEnabled.test {
            // Initial value
            assertThat(awaitItem()).isTrue()

            // Toggle off
            viewModel.setNotificationsEnabled(false)
            assertThat(awaitItem()).isFalse()

            // Toggle on
            viewModel.setNotificationsEnabled(true)
            assertThat(awaitItem()).isTrue()

            cancelAndIgnoreRemainingEvents()
        }
    }
}
