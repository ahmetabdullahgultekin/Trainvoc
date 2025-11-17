package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val wordRepository: IWordRepository
) : ViewModel() {

    private val _levels = MutableStateFlow<Map<WordLevel, Boolean>>(emptyMap())
    val levels: StateFlow<Map<WordLevel, Boolean>> = _levels

    init {
        loadLevels()
    }

    private fun loadLevels() {
        viewModelScope.launch(Dispatchers.IO) {
            val levelStatus = kotlinx.coroutines.coroutineScope {
                WordLevel.entries.map { level ->
                    async { level to isLevelUnlocked(level) }
                }.associate { it.await() }
            }
            _levels.value = levelStatus
        }
    }

    /**
     * Determines if a level is locked or unlocked.
     * Only A1 is unlocked by default, others unlock when all words in the previous level are learned.
     */
    suspend fun isLevelUnlocked(level: WordLevel): Boolean {
        if (level == WordLevel.A1) {
            return true
        }
        val previous = WordLevel.entries.getOrNull(level.ordinal) ?: return false
        return wordRepository.isLevelUnlocked(previous)
    }

    /**
     * Refreshes the locked/unlocked status of all levels.
     * A1 is always unlocked, others unlock when the previous level is completed.
     */
    fun refreshLevelLocks() {
        viewModelScope.launch(Dispatchers.IO) {
            val levelStatus = kotlinx.coroutines.coroutineScope {
                WordLevel.entries.map { level ->
                    async { level to isLevelUnlocked(level) }
                }.associate { it.await() }
            }
            _levels.value = levelStatus
        }
    }
}