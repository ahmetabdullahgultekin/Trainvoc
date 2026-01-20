package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordAskedInExams
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import com.gultekinahmetabdullah.trainvoc.repository.IWordStatisticsService
import com.gultekinahmetabdullah.trainvoc.core.common.DispatcherProvider
import com.gultekinahmetabdullah.trainvoc.utils.InputValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing vocabulary words and search operations
 *
 * This ViewModel provides:
 * - Word list management with automatic updates
 * - Debounced search functionality (300ms delay)
 * - Word insertion and retrieval operations
 * - Thread-safe operations using DispatcherProvider
 *
 * All operations use StateFlow for reactive UI updates.
 * Search queries are automatically validated and sanitized.
 *
 * @property repository Repository for word data operations
 * @property wordStatisticsService Service for word statistics
 * @property dispatchers Dispatcher provider for coroutine execution
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class WordViewModel @Inject constructor(
    private val repository: IWordRepository,
    private val wordStatisticsService: IWordStatisticsService,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _words = MutableStateFlow<List<WordAskedInExams>>(emptyList())
    /**
     * StateFlow of all words with exam information
     *
     * Automatically updated when words are inserted or modified.
     * Collect this flow in UI to observe word list changes.
     */
    val words: StateFlow<List<WordAskedInExams>> = _words

    private val _filteredWords = MutableStateFlow<List<Word>>(emptyList())
    /**
     * StateFlow of filtered search results
     *
     * Updated automatically when search query changes (with 300ms debounce).
     * Empty list indicates no active search or no results.
     */
    val filteredWords: StateFlow<List<Word>> = _filteredWords

    private val _searchQuery = MutableStateFlow("")

    init {
        fetchWords()
        setupSearchDebounce()
    }

    /**
     * Setup debounced search to avoid filtering on every keystroke.
     * Waits 300ms after user stops typing before filtering.
     */
    private fun setupSearchDebounce() {
        viewModelScope.launch(dispatchers.io) {
            _searchQuery
                .debounce(300) // Wait 300ms after last input
                .distinctUntilChanged() // Only process if query changed
                .filter { it.isNotBlank() || _filteredWords.value.isNotEmpty() }
                .collect { query ->
                    performFilter(query)
                }
        }
    }

    private fun performFilter(query: String) {
        _filteredWords.value = if (query.isBlank()) {
            emptyList()
        } else {
            _words.value
                .map { it.word }
                .filter {
                    it.word.contains(query, ignoreCase = true) ||
                            it.meaning.contains(query, ignoreCase = true)
                }
                .sortedBy { it.word }
        }
    }

    private fun fetchWords() {
        viewModelScope.launch(dispatchers.io) {
            _words.value = repository.getAllWordsAskedInExams()
        }
    }

    /**
     * Insert a new word into the vocabulary database
     *
     * This operation runs on IO dispatcher and refreshes the word list
     * after insertion completes. The UI will be updated automatically
     * via the words StateFlow.
     *
     * @param word The word to insert into the database
     */
    fun insertWord(word: Word) {
        viewModelScope.launch(dispatchers.io) {
            repository.insertWord(word)
            fetchWords()
        }
    }

    /**
     * Update search query. Filtering happens automatically with debounce.
     */
    fun filterWords(query: String) {
        // Validate search query before processing
        val validatedQuery = InputValidation.validateSearchQuery(query).getOrElse {
            // On validation error, set empty query (safe fallback)
            ""
        }
        _searchQuery.value = validatedQuery
    }

    /**
     * Retrieve a specific word by its ID
     *
     * Searches the current word list for a word with matching ID.
     * This is a synchronous operation using cached data, so it's
     * safe to call from the UI thread.
     *
     * @param wordId The unique identifier of the word to retrieve
     * @return The matching Word object, or null if not found
     */
    fun getWordById(wordId: String): Word? {
        return _words.value.map { it.word }.find { it.word == wordId }
    }

    data class WordFullDetail(
        val word: Word?,
        val statistic: com.gultekinahmetabdullah.trainvoc.classes.word.Statistic?,
        val exams: List<String>
    )

    suspend fun getWordFullDetail(wordId: String): WordFullDetail? {
        val word = repository.getWordById(wordId)
        val statistic = wordStatisticsService.getWordStats(word)
        val exams = repository.getExamsForWord(wordId)
        return WordFullDetail(word, statistic, exams)
    }

    /**
     * Toggle favorite status for a word.
     * @param wordId The word ID to toggle
     * @param isFavorite The new favorite status
     */
    fun toggleFavorite(wordId: String, isFavorite: Boolean) {
        viewModelScope.launch(dispatchers.io) {
            val timestamp = if (isFavorite) System.currentTimeMillis() else null
            repository.setFavorite(wordId, isFavorite, timestamp)
        }
    }
}