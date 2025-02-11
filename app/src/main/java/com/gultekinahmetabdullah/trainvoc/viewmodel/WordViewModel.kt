package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.Word
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WordViewModel(private val repository: WordRepository) : ViewModel() {

    private val _words = MutableStateFlow<List<Word>>(emptyList())
    val words: StateFlow<List<Word>> = _words

    init {
        fetchWords()
    }

    private fun fetchWords() {
        viewModelScope.launch {
            repository.getAllWords().collect { wordList ->
                _words.value = wordList
            }
        }
    }

    fun insertWord(word: Word) {
        viewModelScope.launch {
            repository.insertWord(word)
            fetchWords()
        }
    }
}