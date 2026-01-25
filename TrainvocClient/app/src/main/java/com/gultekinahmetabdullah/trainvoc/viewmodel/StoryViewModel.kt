package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.core.common.DispatcherProvider
import com.gultekinahmetabdullah.trainvoc.repository.IProgressService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Data class for level display info
 */
data class LevelInfo(
    val level: WordLevel,
    val isUnlocked: Boolean,
    val learnedWords: Int,
    val totalWords: Int
) {
    val progress: Float
        get() = if (totalWords > 0) learnedWords.toFloat() / totalWords else 0f
}

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val progressService: IProgressService,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _levels = MutableStateFlow<Map<WordLevel, Boolean>>(emptyMap())
    val levels: StateFlow<Map<WordLevel, Boolean>> = _levels

    private val _levelInfos = MutableStateFlow<List<LevelInfo>>(emptyList())
    val levelInfos: StateFlow<List<LevelInfo>> = _levelInfos

    init {
        loadLevels()
    }

    private fun loadLevels() {
        viewModelScope.launch(dispatchers.io) {
            val levelStatus = kotlinx.coroutines.coroutineScope {
                WordLevel.entries.map { level ->
                    async { level to isLevelUnlocked(level) }
                }.associate { it.await() }
            }
            _levels.value = levelStatus

            // Also load progress info for each level
            val infos = kotlinx.coroutines.coroutineScope {
                WordLevel.entries.map { level ->
                    async {
                        val isUnlocked = levelStatus[level] ?: false
                        val learned = progressService.getLearnedWordCount(level.name)
                        val total = progressService.getWordCountByLevel(level.name)
                        LevelInfo(level, isUnlocked, learned, total)
                    }
                }.map { it.await() }
            }
            _levelInfos.value = infos
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
        return progressService.isLevelUnlocked(previous)
    }

    /**
     * Refreshes the locked/unlocked status of all levels.
     * A1 is always unlocked, others unlock when the previous level is completed.
     */
    fun refreshLevelLocks() {
        viewModelScope.launch(dispatchers.io) {
            val levelStatus = kotlinx.coroutines.coroutineScope {
                WordLevel.entries.map { level ->
                    async { level to isLevelUnlocked(level) }
                }.associate { it.await() }
            }
            _levels.value = levelStatus
        }
    }
}