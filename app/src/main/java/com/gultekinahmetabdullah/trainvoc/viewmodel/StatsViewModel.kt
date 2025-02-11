package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.classes.Word
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class StatsViewModel(private val repository: WordRepository) : ViewModel() {

    val allWords: Flow<List<Word>> = repository.getAllWords()

    fun getTotalAnswers(): LiveData<Int> = repository.getTotalAnswers()
    fun getCorrectPercentage(): LiveData<Double> = repository.getCorrectPercentage()
    fun getWrongPercentage(): LiveData<Double> = repository.getWrongPercentage()
    fun getSkippedPercentage(): LiveData<Double> = repository.getSkippedPercentage()
    fun getLeastKnownWords(): LiveData<List<Word>> = repository.getLeastKnownWords()

    fun insert(word: Word) = viewModelScope.launch {
        repository.insert(word)
    }
}