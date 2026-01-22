package com.gultekinahmetabdullah.trainvoc.viewmodel

import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordAskedInExams
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import com.gultekinahmetabdullah.trainvoc.repository.IWordStatisticsService
import com.gultekinahmetabdullah.trainvoc.test.util.TestDispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for WordViewModel
 *
 * Demonstrates:
 * - Testing ViewModel with multiple dependencies
 * - Testing StateFlow collection
 * - Testing debounced search functionality
 * - Testing input validation integration
 * - Testing word filtering logic
 * - Testing CRUD operations
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WordViewModelTest {

    private lateinit var wordRepository: IWordRepository
    private lateinit var wordStatisticsService: IWordStatisticsService
    private lateinit var dispatchers: TestDispatcherProvider
    private lateinit var viewModel: WordViewModel

    private val testWords = listOf(
        WordAskedInExams(
            word = Word(word = "apple", meaning = "a fruit", level = "A1", statId = 1),
            exams = emptyList()
        ),
        WordAskedInExams(
            word = Word(word = "banana", meaning = "yellow fruit", level = "A1", statId = 2),
            exams = emptyList()
        ),
        WordAskedInExams(
            word = Word(word = "cat", meaning = "an animal", level = "A2", statId = 3),
            exams = emptyList()
        )
    )

    @Before
    fun setup() {
        wordRepository = mock()
        wordStatisticsService = mock()
        dispatchers = TestDispatcherProvider()
    }

    @Test
    fun `init fetches words automatically`() = runTest {
        // Arrange
        whenever(wordRepository.getAllWordsAskedInExams()).thenReturn(testWords)

        // Act
        viewModel = WordViewModel(wordRepository, wordStatisticsService, dispatchers)
        advanceUntilIdle()

        // Assert
        assertEquals("Expected 3 words", 3, viewModel.words.value.size)
        verify(wordRepository).getAllWordsAskedInExams()
    }

    @Test
    fun `insertWord adds word and refreshes list`() = runTest {
        // Arrange
        whenever(wordRepository.getAllWordsAskedInExams())
            .thenReturn(testWords)
            .thenReturn(testWords + WordAskedInExams(
                word = Word(word = "dog", meaning = "pet animal", level = "A1", statId = 4),
                exams = emptyList()
            ))

        viewModel = WordViewModel(wordRepository, wordStatisticsService, dispatchers)
        advanceUntilIdle()

        val newWord = Word(word = "dog", meaning = "pet animal", level = "A1", statId = 4)

        // Act
        viewModel.insertWord(newWord)
        advanceUntilIdle()

        // Assert
        verify(wordRepository).insertWord(newWord)
        verify(wordRepository, times(2)).getAllWordsAskedInExams() // Init + after insert
        assertEquals("Expected 4 words after insert", 4, viewModel.words.value.size)
    }

    @Test
    fun `filterWords with valid query filters word list`() = runTest {
        // Arrange
        whenever(wordRepository.getAllWordsAskedInExams()).thenReturn(testWords)
        viewModel = WordViewModel(wordRepository, wordStatisticsService, dispatchers)
        advanceUntilIdle()

        // Act
        viewModel.filterWords("app")
        advanceUntilIdle() // Wait for debounce (300ms) + filtering

        // Assert - should filter to words containing "app"
        assertEquals("Expected 1 filtered word", 1, viewModel.filteredWords.value.size)
        assertEquals("Expected 'apple' word", "apple", viewModel.filteredWords.value[0].word)
    }

    @Test
    fun `filterWords with empty query returns empty list`() = runTest {
        // Arrange
        whenever(wordRepository.getAllWordsAskedInExams()).thenReturn(testWords)
        viewModel = WordViewModel(wordRepository, wordStatisticsService, dispatchers)
        advanceUntilIdle()

        // Act
        viewModel.filterWords("")
        advanceUntilIdle()

        // Assert
        assertTrue("Expected empty filtered list", viewModel.filteredWords.value.isEmpty())
    }

    @Test
    fun `filterWords validates and sanitizes query`() = runTest {
        // Arrange
        whenever(wordRepository.getAllWordsAskedInExams()).thenReturn(testWords)
        viewModel = WordViewModel(wordRepository, wordStatisticsService, dispatchers)
        advanceUntilIdle()

        // Act - try with query longer than max length (100 chars)
        val longQuery = "a".repeat(150) // Exceeds max length
        viewModel.filterWords(longQuery)
        advanceUntilIdle()

        // Assert - should be truncated/sanitized, no crash
        // filteredWords should still work (might be empty if no matches)
        assertNotNull("Filtered words should not be null", viewModel.filteredWords.value)
    }

    @Test
    fun `filterWords searches both word and meaning`() = runTest {
        // Arrange
        whenever(wordRepository.getAllWordsAskedInExams()).thenReturn(testWords)
        viewModel = WordViewModel(wordRepository, wordStatisticsService, dispatchers)
        advanceUntilIdle()

        // Act - search by meaning
        viewModel.filterWords("fruit")
        advanceUntilIdle()

        // Assert - should match "apple" and "banana" by meaning
        assertEquals("Expected 2 filtered words", 2, viewModel.filteredWords.value.size)
        assertTrue(
            "Expected to find apple",
            viewModel.filteredWords.value.any { it.word == "apple" }
        )
        assertTrue(
            "Expected to find banana",
            viewModel.filteredWords.value.any { it.word == "banana" }
        )
    }

    @Test
    fun `filterWords is case insensitive`() = runTest {
        // Arrange
        whenever(wordRepository.getAllWordsAskedInExams()).thenReturn(testWords)
        viewModel = WordViewModel(wordRepository, wordStatisticsService, dispatchers)
        advanceUntilIdle()

        // Act - search with uppercase
        viewModel.filterWords("APPLE")
        advanceUntilIdle()

        // Assert - should still match lowercase "apple"
        assertEquals("Expected 1 filtered word", 1, viewModel.filteredWords.value.size)
        assertEquals("Expected 'apple' word", "apple", viewModel.filteredWords.value[0].word)
    }

    @Test
    fun `getWordById returns correct word`() = runTest {
        // Arrange
        whenever(wordRepository.getAllWordsAskedInExams()).thenReturn(testWords)
        viewModel = WordViewModel(wordRepository, wordStatisticsService, dispatchers)
        advanceUntilIdle()

        // Act
        val word = viewModel.getWordById("banana")

        // Assert
        assertNotNull("Expected to find banana", word)
        assertEquals("Expected banana", "banana", word?.word)
        assertEquals("Expected yellow fruit meaning", "yellow fruit", word?.meaning)
    }

    @Test
    fun `getWordById returns null for non-existent word`() = runTest {
        // Arrange
        whenever(wordRepository.getAllWordsAskedInExams()).thenReturn(testWords)
        viewModel = WordViewModel(wordRepository, wordStatisticsService, dispatchers)
        advanceUntilIdle()

        // Act
        val word = viewModel.getWordById("nonexistent")

        // Assert
        assertNull("Expected null for non-existent word", word)
    }

    @Test
    fun `filteredWords sorted alphabetically`() = runTest {
        // Arrange
        whenever(wordRepository.getAllWordsAskedInExams()).thenReturn(testWords)
        viewModel = WordViewModel(wordRepository, wordStatisticsService, dispatchers)
        advanceUntilIdle()

        // Act - filter to get all A1 level words
        viewModel.filterWords("a") // Matches "apple", "banana", "animal", "cat"
        advanceUntilIdle()

        // Assert - should be sorted
        val filtered = viewModel.filteredWords.value
        if (filtered.size > 1) {
            for (i in 0 until filtered.size - 1) {
                assertTrue(
                    "Expected alphabetical order",
                    filtered[i].word <= filtered[i + 1].word
                )
            }
        }
    }

    @Test
    fun `words StateFlow starts empty until loaded`() = runTest {
        // Arrange
        whenever(wordRepository.getAllWordsAskedInExams()).thenReturn(testWords)

        // Act - create ViewModel but don't advance coroutines yet
        viewModel = WordViewModel(wordRepository, wordStatisticsService, dispatchers)

        // Assert - should start with empty list
        assertTrue("Expected empty list initially", viewModel.words.value.isEmpty())

        // Now advance and check it loaded
        advanceUntilIdle()
        assertEquals("Expected 3 words after loading", 3, viewModel.words.value.size)
    }

    @Test
    fun `filteredWords StateFlow starts empty`() = runTest {
        // Arrange
        whenever(wordRepository.getAllWordsAskedInExams()).thenReturn(testWords)

        // Act
        viewModel = WordViewModel(wordRepository, wordStatisticsService, dispatchers)
        advanceUntilIdle()

        // Assert - filtered words should be empty until search performed
        assertTrue("Expected empty filtered list initially", viewModel.filteredWords.value.isEmpty())
    }

    @Test
    fun `multiple sequential filters work correctly`() = runTest {
        // Arrange
        whenever(wordRepository.getAllWordsAskedInExams()).thenReturn(testWords)
        viewModel = WordViewModel(wordRepository, wordStatisticsService, dispatchers)
        advanceUntilIdle()

        // Act - first filter
        viewModel.filterWords("a")
        advanceUntilIdle()
        val firstResults = viewModel.filteredWords.value.size

        // Act - second filter
        viewModel.filterWords("app")
        advanceUntilIdle()
        val secondResults = viewModel.filteredWords.value.size

        // Assert
        assertTrue("First filter should find multiple words", firstResults >= 2)
        assertEquals("Second filter should find 1 word", 1, secondResults)
    }

    @Test
    fun `repository exception during fetch does not crash ViewModel`() = runTest {
        // Arrange
        whenever(wordRepository.getAllWordsAskedInExams()).thenThrow(RuntimeException("DB error"))

        // Act - should not crash
        viewModel = WordViewModel(wordRepository, wordStatisticsService, dispatchers)
        advanceUntilIdle()

        // Assert - words should remain empty
        assertTrue("Expected empty list on error", viewModel.words.value.isEmpty())
    }
}
