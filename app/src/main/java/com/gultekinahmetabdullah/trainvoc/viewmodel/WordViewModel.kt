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

    private val _filteredWords = MutableStateFlow<List<Word>>(emptyList())
    val filteredWords: StateFlow<List<Word>> = _filteredWords

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

    fun filterWords(query: String) {
        println("Filtering words with query: $query")
        println(
            "Current words count: ${_words.value.size}, Filtered words count: ${_filteredWords.value.size}"
        )
        _filteredWords.value = _words.value
            .map { it.word }
            .filter {
                it.word.contains(query, ignoreCase = true) ||
                        it.meaning.contains(query, ignoreCase = true)
            }
            .sortedBy { it.word }
        println("Filtered words count after filtering: ${_filteredWords.value.size}")
    }

    // Belirli bir kelimeyi word ile getir
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