package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import com.gultekinahmetabdullah.trainvoc.features.WordOfDay
import com.gultekinahmetabdullah.trainvoc.features.WordOfDayDao
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for WordOfTheDayScreen
 *
 * Manages the daily featured word with:
 * - Daily rotation at midnight
 * - Random word selection from user's level range
 * - View tracking
 * - Favorite toggling
 */
@HiltViewModel
class WordOfDayViewModel @Inject constructor(
    private val wordOfDayDao: WordOfDayDao,
    private val wordDao: WordDao,
    private val repository: IWordRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _wordOfDay = MutableStateFlow<Word?>(null)
    val wordOfDay: StateFlow<Word?> = _wordOfDay.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _currentDate = MutableStateFlow("")
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadWordOfDay()
    }

    /**
     * Load or generate the word of the day.
     */
    private fun loadWordOfDay() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val today = getTodayDateString()
                _currentDate.value = getFormattedDate()

                // Check if we already have a word of the day for today
                var wordOfDayEntry = wordOfDayDao.getWordOfDay(today)

                if (wordOfDayEntry == null) {
                    // Generate new word of the day
                    wordOfDayEntry = generateNewWordOfDay(today)
                }

                if (wordOfDayEntry != null) {
                    // Load the actual word details
                    val word = wordDao.getWord(wordOfDayEntry.wordId)
                    if (word != null) {
                        _wordOfDay.value = word
                        _isFavorite.value = word.isFavorite

                        // Mark as viewed if not already
                        if (!wordOfDayEntry.wasViewed) {
                            wordOfDayDao.markAsViewed(today)
                        }
                    } else {
                        // Word no longer exists, generate a new one
                        val newEntry = generateNewWordOfDay(today)
                        if (newEntry != null) {
                            val newWord = wordDao.getWord(newEntry.wordId)
                            _wordOfDay.value = newWord
                            _isFavorite.value = newWord?.isFavorite ?: false
                        } else {
                            _error.value = "Could not find any words"
                        }
                    }
                } else {
                    _error.value = "Could not load word of the day"
                }
            } catch (e: Exception) {
                _error.value = "Error loading word: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Generate a new word of the day by selecting a random word.
     */
    private suspend fun generateNewWordOfDay(date: String): WordOfDay? {
        // Get a random word (prioritize words that haven't been used recently)
        val randomWord = wordDao.getRandomWordForNotification(
            includeLearned = true,
            level = null
        )

        return if (randomWord != null) {
            val newEntry = WordOfDay(
                wordId = randomWord.word,
                date = date,
                wasViewed = false
            )
            wordOfDayDao.insertWordOfDay(newEntry)

            // Clean up old entries (keep only last 30 days)
            val cutoffDate = getCutoffDateString(30)
            wordOfDayDao.deleteOldEntries(cutoffDate)

            newEntry
        } else {
            null
        }
    }

    /**
     * Toggle favorite status for the current word of the day.
     */
    fun toggleFavorite() {
        val word = _wordOfDay.value ?: return
        viewModelScope.launch {
            val newState = !_isFavorite.value
            repository.setFavorite(
                wordId = word.word,
                isFavorite = newState,
                timestamp = if (newState) System.currentTimeMillis() else null
            )
            _isFavorite.value = newState
        }
    }

    /**
     * Retry loading if there was an error.
     */
    fun retry() {
        loadWordOfDay()
    }

    /**
     * Get today's date in YYYY-MM-DD format.
     */
    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    /**
     * Get formatted date for display.
     */
    private fun getFormattedDate(): String {
        return SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())
    }

    /**
     * Get cutoff date for cleanup (N days ago).
     */
    private fun getCutoffDateString(daysAgo: Int): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -daysAgo)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }
}
