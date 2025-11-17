package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordAskedInExams
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class WordViewModel @Inject constructor(
    private val repository: IWordRepository
) : ViewModel() {

    private val _words = MutableStateFlow<List<WordAskedInExams>>(emptyList())
    val words: StateFlow<List<WordAskedInExams>> = _words

    private val _filteredWords = MutableStateFlow<List<Word>>(emptyList())
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
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
            _words.value = repository.getAllWordsAskedInExams()
        }
    }

    fun insertWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertWord(word)
            fetchWords()
        }
    }

    /**
     * Update search query. Filtering happens automatically with debounce.
     */
    fun filterWords(query: String) {
        _searchQuery.value = query
    }

    // Get a specific word by its word ID
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
        val statistic = repository.getWordStats(word)
        val exams = repository.getExamsForWord(wordId)
        return WordFullDetail(word, statistic, exams)
    }
}