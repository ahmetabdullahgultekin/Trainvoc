package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordAskedInExams
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WordViewModel(private val repository: WordRepository) : ViewModel() {

    private val _words = MutableStateFlow<List<WordAskedInExams>>(emptyList())
    val words: StateFlow<List<WordAskedInExams>> = _words

    init {
        fetchWords()
    }

    private fun fetchWords() {
        viewModelScope.launch {
            _words.value = repository.getAllWordsAskedInExams()
        }
    }

    fun insertWord(word: Word) {
        viewModelScope.launch {
            repository.insertWord(word)
            fetchWords()
        }
    }
}