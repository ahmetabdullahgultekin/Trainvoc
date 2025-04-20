package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StoryViewModel(private val wordRepository: WordRepository) : ViewModel() {

    private val _levels = MutableStateFlow<Map<WordLevel, Boolean>>(emptyMap())
    val levels: StateFlow<Map<WordLevel, Boolean>> = _levels

    init {
        loadLevels()
    }

    private fun loadLevels() {
        viewModelScope.launch {
            val levelStatus = WordLevel.entries.associateWith { level ->
                wordRepository.isLevelUnlocked(level)
            }
            _levels.value = levelStatus
        }
    }
}