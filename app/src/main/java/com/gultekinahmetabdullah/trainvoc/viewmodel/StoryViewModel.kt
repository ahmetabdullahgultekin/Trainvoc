package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val wordRepository: WordRepository
) : ViewModel() {

    private val _levels = MutableStateFlow<Map<WordLevel, Boolean>>(emptyMap())
    val levels: StateFlow<Map<WordLevel, Boolean>> = _levels

    init {
        loadLevels()
    }

    private fun loadLevels() {
        viewModelScope.launch {
            val levelStatus = kotlinx.coroutines.coroutineScope {
                WordLevel.entries.map { level ->
                    async { level to isLevelUnlocked(level) }
                }.associate { it.await() }
            }
            println("[StoryViewModel] Loaded levels: $levelStatus")
            _levels.value = levelStatus
        }
    }

    /**
     * Bir seviyenin kilitli olup olmadığını belirler.
     * Sadece A1 başta açık, diğerleri bir önceki seviyedeki tüm kelimeler learned ise açılır.
     */
    suspend fun isLevelUnlocked(level: WordLevel): Boolean {
        if (level == WordLevel.A1) {
            println("[StoryViewModel] isLevelUnlocked: ${level.name} is always unlocked (A1)")
            return true
        }
        val previous = WordLevel.entries.getOrNull(level.ordinal) ?: run {
            println("[StoryViewModel] isLevelUnlocked: ${level.name} has no previous level, returning false")
            return false
        }
        val unlocked = wordRepository.isLevelUnlocked(previous)
        println("[StoryViewModel] isLevelUnlocked: ${level.name}, previous: ${previous.name}, previousUnlocked: $unlocked (details: checking if all words in $previous are learned)")
        return unlocked
    }

    /**
     * Tüm seviyelerin kilitli/açık durumunu günceller.
     * A1 her zaman açık, diğerleri bir önceki seviye learned ise açılır.
     */
    fun refreshLevelLocks() {
        viewModelScope.launch {
            val levelStatus = kotlinx.coroutines.coroutineScope {
                WordLevel.entries.map { level ->
                    async { level to isLevelUnlocked(level) }
                }.associate { it.await() }
            }
            _levels.value = levelStatus
        }
    }
}