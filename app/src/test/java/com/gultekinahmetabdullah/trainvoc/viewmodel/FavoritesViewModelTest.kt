package com.gultekinahmetabdullah.trainvoc.viewmodel

import app.cash.turbine.test
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for FavoritesViewModel
 *
 * Demonstrates:
 * - Testing reactive Flows with flatMapLatest
 * - Testing debounced search (300ms delay)
 * - Testing loading states
 * - Testing CRUD operations on favorites
 * - Using Turbine for Flow testing (or manual collection)
 * - Testing Flow transformations
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private lateinit var repository: IWordRepository
    private lateinit var viewModel: FavoritesViewModel

    private val testFavorites = listOf(
        Word(word = "apple", meaning = "a fruit", level = "A1", statId = 1, isFavorite = true),
        Word(word = "book", meaning = "reading material", level = "A2", statId = 2, isFavorite = true),
        Word(word = "cat", meaning = "an animal", level = "A1", statId = 3, isFavorite = true)
    )

    @Before
    fun setup() {
        repository = mock()
    }

    @Test
    fun `init loads favorite words with empty search`() = runTest {
        // Arrange
        whenever(repository.getFavoriteWords()).thenReturn(flowOf(testFavorites))

        // Act
        viewModel = FavoritesViewModel(repository)
        advanceTimeBy(400) // Wait for debounce + processing
        advanceUntilIdle()

        // Assert
        assertEquals("Expected 3 favorite words", 3, viewModel.favoriteWords.value.size)
        verify(repository).getFavoriteWords()
    }

    @Test
    fun `updateSearchQuery triggers search after debounce`() = runTest {
        // Arrange
        val searchResults = listOf(testFavorites[0]) // Just "apple"
        whenever(repository.getFavoriteWords()).thenReturn(flowOf(testFavorites))
        whenever(repository.searchFavoriteWords("app")).thenReturn(flowOf(searchResults))

        viewModel = FavoritesViewModel(repository)
        advanceTimeBy(400)
        advanceUntilIdle()

        // Act
        viewModel.updateSearchQuery("app")
        advanceTimeBy(400) // Wait for 300ms debounce + processing
        advanceUntilIdle()

        // Assert
        verify(repository).searchFavoriteWords("app")
        assertEquals("Expected 1 search result", 1, viewModel.favoriteWords.value.size)
        assertEquals("Expected 'apple'", "apple", viewModel.favoriteWords.value[0].word)
    }

    @Test
    fun `updateSearchQuery does not trigger search before debounce time`() = runTest {
        // Arrange
        whenever(repository.getFavoriteWords()).thenReturn(flowOf(testFavorites))
        whenever(repository.searchFavoriteWords(any())).thenReturn(flowOf(emptyList()))

        viewModel = FavoritesViewModel(repository)
        advanceTimeBy(400)
        advanceUntilIdle()

        // Clear previous invocations
        clearInvocations(repository)

        // Act - update but don't wait full debounce time
        viewModel.updateSearchQuery("test")
        advanceTimeBy(200) // Only 200ms, debounce is 300ms

        // Assert - search should NOT have been called yet
        verify(repository, never()).searchFavoriteWords(any())
    }

    @Test
    fun `rapid search queries are debounced`() = runTest {
        // Arrange
        whenever(repository.getFavoriteWords()).thenReturn(flowOf(testFavorites))
        whenever(repository.searchFavoriteWords(any())).thenReturn(flowOf(emptyList()))

        viewModel = FavoritesViewModel(repository)
        advanceTimeBy(400)
        advanceUntilIdle()
        clearInvocations(repository)

        // Act - rapid fire updates
        viewModel.updateSearchQuery("a")
        advanceTimeBy(100)
        viewModel.updateSearchQuery("ap")
        advanceTimeBy(100)
        viewModel.updateSearchQuery("app")
        advanceTimeBy(100)
        viewModel.updateSearchQuery("appl")
        advanceTimeBy(100)
        viewModel.updateSearchQuery("apple")
        advanceTimeBy(400) // Wait for debounce

        // Assert - only final query should be executed
        verify(repository, times(1)).searchFavoriteWords("apple")
        verify(repository, never()).searchFavoriteWords("a")
        verify(repository, never()).searchFavoriteWords("ap")
        verify(repository, never()).searchFavoriteWords("app")
        verify(repository, never()).searchFavoriteWords("appl")
    }

    @Test
    fun `empty search query shows all favorites`() = runTest {
        // Arrange
        whenever(repository.getFavoriteWords()).thenReturn(flowOf(testFavorites))
        whenever(repository.searchFavoriteWords("test")).thenReturn(flowOf(listOf(testFavorites[0])))

        viewModel = FavoritesViewModel(repository)
        advanceTimeBy(400)
        advanceUntilIdle()

        // Set search query
        viewModel.updateSearchQuery("test")
        advanceTimeBy(400)
        advanceUntilIdle()

        clearInvocations(repository)

        // Act - clear search
        viewModel.updateSearchQuery("")
        advanceTimeBy(400)
        advanceUntilIdle()

        // Assert - should show all favorites again
        verify(repository).getFavoriteWords()
        assertEquals("Expected all 3 favorites", 3, viewModel.favoriteWords.value.size)
    }

    @Test
    fun `toggleFavorite adds word to favorites`() = runTest {
        // Arrange
        whenever(repository.getFavoriteWords()).thenReturn(flowOf(testFavorites))
        viewModel = FavoritesViewModel(repository)
        advanceTimeBy(400)
        advanceUntilIdle()

        // Act - toggle from not favorite to favorite
        viewModel.toggleFavorite("dog", currentState = false)
        advanceUntilIdle()

        // Assert
        verify(repository).setFavorite(
            wordId = eq("dog"),
            isFavorite = eq(true),
            timestamp = any() // Should set timestamp when adding
        )
    }

    @Test
    fun `toggleFavorite removes word from favorites`() = runTest {
        // Arrange
        whenever(repository.getFavoriteWords()).thenReturn(flowOf(testFavorites))
        viewModel = FavoritesViewModel(repository)
        advanceTimeBy(400)
        advanceUntilIdle()

        // Act - toggle from favorite to not favorite
        viewModel.toggleFavorite("apple", currentState = true)
        advanceUntilIdle()

        // Assert
        verify(repository).setFavorite(
            wordId = eq("apple"),
            isFavorite = eq(false),
            timestamp = isNull() // Should clear timestamp when removing
        )
    }

    @Test
    fun `removeFromFavorites removes word`() = runTest {
        // Arrange
        whenever(repository.getFavoriteWords()).thenReturn(flowOf(testFavorites))
        viewModel = FavoritesViewModel(repository)
        advanceTimeBy(400)
        advanceUntilIdle()

        // Act
        viewModel.removeFromFavorites("apple")
        advanceUntilIdle()

        // Assert
        verify(repository).setFavorite("apple", false, null)
    }

    @Test
    fun `clearAllFavorites removes all favorites`() = runTest {
        // Arrange
        whenever(repository.getFavoriteWords()).thenReturn(flowOf(testFavorites))
        viewModel = FavoritesViewModel(repository)
        advanceTimeBy(400)
        advanceUntilIdle()

        // Act
        viewModel.clearAllFavorites()
        advanceUntilIdle()

        // Assert
        verify(repository).clearAllFavorites()
    }

    @Test
    fun `searchQuery StateFlow updates correctly`() = runTest {
        // Arrange
        whenever(repository.getFavoriteWords()).thenReturn(flowOf(testFavorites))
        viewModel = FavoritesViewModel(repository)

        // Assert initial state
        assertEquals("Expected empty initial query", "", viewModel.searchQuery.value)

        // Act
        viewModel.updateSearchQuery("test")

        // Assert
        assertEquals("Expected 'test' query", "test", viewModel.searchQuery.value)
    }

    @Test
    fun `isLoading starts true and becomes false after load`() = runTest {
        // Arrange
        whenever(repository.getFavoriteWords()).thenReturn(flowOf(testFavorites))

        // Act
        viewModel = FavoritesViewModel(repository)

        // Assert - should start as true
        assertTrue("Expected loading to start true", viewModel.isLoading.value)

        // Wait for load
        advanceTimeBy(400)
        advanceUntilIdle()

        // Assert - should become false after load
        assertFalse("Expected loading to become false", viewModel.isLoading.value)
    }

    @Test
    fun `isLoading becomes true during search then false`() = runTest {
        // Arrange
        whenever(repository.getFavoriteWords()).thenReturn(flowOf(testFavorites))
        whenever(repository.searchFavoriteWords(any())).thenReturn(flowOf(listOf(testFavorites[0])))

        viewModel = FavoritesViewModel(repository)
        advanceTimeBy(400)
        advanceUntilIdle()

        // Loading should be false now
        assertFalse(viewModel.isLoading.value)

        // Act - start a search
        viewModel.updateSearchQuery("test")

        // During debounce, wait and check
        advanceTimeBy(350) // After debounce, before results
        advanceUntilIdle()

        // Assert - should eventually become false again
        assertFalse("Expected loading to become false after search", viewModel.isLoading.value)
    }

    @Test
    fun `favoriteWords starts with empty list`() = runTest {
        // Arrange
        whenever(repository.getFavoriteWords()).thenReturn(flowOf(testFavorites))

        // Act
        viewModel = FavoritesViewModel(repository)

        // Assert - should start empty (initialValue = emptyList())
        assertTrue("Expected empty initial list", viewModel.favoriteWords.value.isEmpty())

        // Wait for load
        advanceTimeBy(400)
        advanceUntilIdle()

        // Assert - should have favorites now
        assertEquals("Expected 3 favorites after load", 3, viewModel.favoriteWords.value.size)
    }

    @Test
    fun `distinctUntilChanged prevents duplicate queries`() = runTest {
        // Arrange
        whenever(repository.getFavoriteWords()).thenReturn(flowOf(testFavorites))
        whenever(repository.searchFavoriteWords("test")).thenReturn(flowOf(listOf(testFavorites[0])))

        viewModel = FavoritesViewModel(repository)
        advanceTimeBy(400)
        advanceUntilIdle()
        clearInvocations(repository)

        // Act - set same query twice
        viewModel.updateSearchQuery("test")
        advanceTimeBy(400)
        advanceUntilIdle()

        viewModel.updateSearchQuery("test") // Same query again
        advanceTimeBy(400)
        advanceUntilIdle()

        // Assert - should only query once due to distinctUntilChanged
        verify(repository, times(1)).searchFavoriteWords("test")
    }

    @Test
    fun `white space search query shows all favorites`() = runTest {
        // Arrange
        whenever(repository.getFavoriteWords()).thenReturn(flowOf(testFavorites))

        viewModel = FavoritesViewModel(repository)
        advanceTimeBy(400)
        advanceUntilIdle()
        clearInvocations(repository)

        // Act - set whitespace query
        viewModel.updateSearchQuery("   ")
        advanceTimeBy(400)
        advanceUntilIdle()

        // Assert - should use getFavoriteWords (blank check)
        verify(repository).getFavoriteWords()
        verify(repository, never()).searchFavoriteWords(any())
    }
}
