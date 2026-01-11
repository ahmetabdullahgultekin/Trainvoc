package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for FavoritesScreen
 *
 * Manages favorite words data with search functionality.
 * Uses debounced search to optimize performance.
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: IWordRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Flow of favorite words, automatically filters based on search query.
     * Uses debounce to avoid excessive database queries during typing.
     */
    val favoriteWords: StateFlow<List<Word>> = _searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            _isLoading.value = true
            if (query.isBlank()) {
                repository.getFavoriteWords()
            } else {
                repository.searchFavoriteWords(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Watch for changes in favoriteWords to update loading state
        viewModelScope.launch {
            favoriteWords.collect {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update the search query for filtering favorites.
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Toggle favorite status for a word.
     * @param wordId The word to toggle
     * @param currentState Current favorite status (true = is favorite)
     */
    fun toggleFavorite(wordId: String, currentState: Boolean) {
        viewModelScope.launch {
            repository.setFavorite(
                wordId = wordId,
                isFavorite = !currentState,
                timestamp = if (!currentState) System.currentTimeMillis() else null
            )
        }
    }

    /**
     * Remove a word from favorites.
     */
    fun removeFromFavorites(wordId: String) {
        viewModelScope.launch {
            repository.setFavorite(wordId, false, null)
        }
    }

    /**
     * Clear all favorites.
     */
    fun clearAllFavorites() {
        viewModelScope.launch {
            repository.clearAllFavorites()
        }
    }
}
