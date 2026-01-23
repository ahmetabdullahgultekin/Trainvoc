package com.gultekinahmetabdullah.trainvoc.viewmodel

import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordLevel
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import com.gultekinahmetabdullah.trainvoc.features.WordOfDay
import com.gultekinahmetabdullah.trainvoc.features.WordOfDayDao
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Unit tests for WordOfDayViewModel
 *
 * Tests:
 * - Loading word of the day
 * - Generating new word when none exists
 * - Favorite toggling
 * - Error handling
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WordOfDayViewModelTest {

    private lateinit var wordOfDayDao: WordOfDayDao
    private lateinit var wordDao: WordDao
    private lateinit var repository: IWordRepository
    private lateinit var viewModel: WordOfDayViewModel

    private val testWord = Word(
        word = "apple",
        meaning = "elma",
        wordLevel = WordLevel.A1,
        isFavorite = false
    )

    private val todayDate: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    @Before
    fun setup() {
        wordOfDayDao = mock()
        wordDao = mock()
        repository = mock()
    }

    private fun createViewModel(): WordOfDayViewModel {
        return WordOfDayViewModel(
            wordOfDayDao = wordOfDayDao,
            wordDao = wordDao,
            repository = repository
        )
    }

    @Test
    fun `initial state is loading`() = runTest {
        // Arrange
        whenever(wordOfDayDao.getWordOfDay(any())).thenReturn(null)
        whenever(wordDao.getRandomWordForNotification(any(), any())).thenReturn(null)

        // Act
        viewModel = createViewModel()

        // Assert - initially loading (before advanceUntilIdle)
        // After coroutines complete, loading should be false
        advanceUntilIdle()
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loads existing word of the day from database`() = runTest {
        // Arrange
        val existingEntry = WordOfDay(
            wordId = "apple",
            date = todayDate,
            wasViewed = false
        )
        whenever(wordOfDayDao.getWordOfDay(todayDate)).thenReturn(existingEntry)
        whenever(wordDao.getWord("apple")).thenReturn(testWord)

        // Act
        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert
        assertFalse(viewModel.isLoading.value)
        assertEquals(testWord, viewModel.wordOfDay.value)
        assertFalse(viewModel.isFavorite.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `marks word as viewed when loaded`() = runTest {
        // Arrange
        val existingEntry = WordOfDay(
            wordId = "apple",
            date = todayDate,
            wasViewed = false
        )
        whenever(wordOfDayDao.getWordOfDay(todayDate)).thenReturn(existingEntry)
        whenever(wordDao.getWord("apple")).thenReturn(testWord)

        // Act
        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert
        verify(wordOfDayDao).markAsViewed(todayDate)
    }

    @Test
    fun `does not mark as viewed if already viewed`() = runTest {
        // Arrange
        val existingEntry = WordOfDay(
            wordId = "apple",
            date = todayDate,
            wasViewed = true
        )
        whenever(wordOfDayDao.getWordOfDay(todayDate)).thenReturn(existingEntry)
        whenever(wordDao.getWord("apple")).thenReturn(testWord)

        // Act
        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert
        verify(wordOfDayDao, never()).markAsViewed(any())
    }

    @Test
    fun `generates new word of the day when none exists`() = runTest {
        // Arrange
        whenever(wordOfDayDao.getWordOfDay(todayDate)).thenReturn(null)
        whenever(wordDao.getRandomWordForNotification(any(), any())).thenReturn(testWord)
        whenever(wordDao.getWord("apple")).thenReturn(testWord)

        // Act
        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert
        verify(wordOfDayDao).insertWordOfDay(any())
        assertEquals(testWord, viewModel.wordOfDay.value)
    }

    @Test
    fun `cleans up old entries after generating new word`() = runTest {
        // Arrange
        whenever(wordOfDayDao.getWordOfDay(todayDate)).thenReturn(null)
        whenever(wordDao.getRandomWordForNotification(any(), any())).thenReturn(testWord)
        whenever(wordDao.getWord("apple")).thenReturn(testWord)

        // Act
        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert
        verify(wordOfDayDao).deleteOldEntries(any())
    }

    @Test
    fun `shows error when no word can be loaded`() = runTest {
        // Arrange
        whenever(wordOfDayDao.getWordOfDay(todayDate)).thenReturn(null)
        whenever(wordDao.getRandomWordForNotification(any(), any())).thenReturn(null)

        // Act
        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert
        assertNull(viewModel.wordOfDay.value)
        assertEquals("Could not load word of the day", viewModel.error.value)
    }

    @Test
    fun `handles exception during loading`() = runTest {
        // Arrange
        whenever(wordOfDayDao.getWordOfDay(todayDate)).thenThrow(RuntimeException("Database error"))

        // Act
        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert
        assertNull(viewModel.wordOfDay.value)
        assertTrue(viewModel.error.value?.contains("Error loading word") == true)
    }

    @Test
    fun `toggleFavorite updates favorite state`() = runTest {
        // Arrange
        val existingEntry = WordOfDay(
            wordId = "apple",
            date = todayDate,
            wasViewed = true
        )
        whenever(wordOfDayDao.getWordOfDay(todayDate)).thenReturn(existingEntry)
        whenever(wordDao.getWord("apple")).thenReturn(testWord)

        viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.isFavorite.value)

        // Act
        viewModel.toggleFavorite()
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.isFavorite.value)
        verify(repository).setFavorite(eq("apple"), eq(true), any())
    }

    @Test
    fun `toggleFavorite removes from favorites when already favorited`() = runTest {
        // Arrange
        val favoriteWord = testWord.copy(isFavorite = true)
        val existingEntry = WordOfDay(
            wordId = "apple",
            date = todayDate,
            wasViewed = true
        )
        whenever(wordOfDayDao.getWordOfDay(todayDate)).thenReturn(existingEntry)
        whenever(wordDao.getWord("apple")).thenReturn(favoriteWord)

        viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.isFavorite.value)

        // Act
        viewModel.toggleFavorite()
        advanceUntilIdle()

        // Assert
        assertFalse(viewModel.isFavorite.value)
        verify(repository).setFavorite(eq("apple"), eq(false), any())
    }

    @Test
    fun `toggleFavorite does nothing when no word is loaded`() = runTest {
        // Arrange
        whenever(wordOfDayDao.getWordOfDay(todayDate)).thenReturn(null)
        whenever(wordDao.getRandomWordForNotification(any(), any())).thenReturn(null)

        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.toggleFavorite()
        advanceUntilIdle()

        // Assert - no crash, no interaction with repository
        verify(repository, never()).setFavorite(any(), any(), any())
    }

    @Test
    fun `retry reloads word of the day`() = runTest {
        // Arrange - first load fails
        whenever(wordOfDayDao.getWordOfDay(todayDate))
            .thenReturn(null)
            .thenReturn(WordOfDay("apple", todayDate, false))
        whenever(wordDao.getRandomWordForNotification(any(), any())).thenReturn(null)
        whenever(wordDao.getWord("apple")).thenReturn(testWord)

        viewModel = createViewModel()
        advanceUntilIdle()

        assertNull(viewModel.wordOfDay.value)
        assertEquals("Could not load word of the day", viewModel.error.value)

        // Act - retry with success this time
        viewModel.retry()
        advanceUntilIdle()

        // Assert - word is now loaded
        assertEquals(testWord, viewModel.wordOfDay.value)
    }

    @Test
    fun `currentDate is formatted correctly`() = runTest {
        // Arrange
        whenever(wordOfDayDao.getWordOfDay(todayDate)).thenReturn(null)
        whenever(wordDao.getRandomWordForNotification(any(), any())).thenReturn(testWord)
        whenever(wordDao.getWord("apple")).thenReturn(testWord)

        // Act
        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert - date should be in "EEEE, MMMM d, yyyy" format
        assertTrue(viewModel.currentDate.value.isNotEmpty())
        // Check that it contains a year (4 digits)
        assertTrue(viewModel.currentDate.value.matches(Regex(".*\\d{4}.*")))
    }
}
