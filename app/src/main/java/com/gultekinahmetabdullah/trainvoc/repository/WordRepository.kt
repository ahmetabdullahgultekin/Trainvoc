package com.gultekinahmetabdullah.trainvoc.repository

import androidx.lifecycle.LiveData
import com.gultekinahmetabdullah.trainvoc.classes.Question
import com.gultekinahmetabdullah.trainvoc.classes.Word
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao: WordDao) {

    //fun getAllWords(): LiveData<List<Word>> = wordDao.getAllWords()
    fun getTotalAnswers(): LiveData<Int> = wordDao.getTotalAnswers()
    fun getCorrectPercentage(): LiveData<Double> = wordDao.getCorrectPercentage()
    fun getWrongPercentage(): LiveData<Double> = wordDao.getWrongPercentage()
    fun getSkippedPercentage(): LiveData<Double> = wordDao.getSkippedPercentage()
    fun getLeastKnownWords(): LiveData<List<Word>> = wordDao.getLeastKnownWords()

    suspend fun insert(word: Word) {
        wordDao.insert(word)
    }

    fun getAllWords(): Flow<List<Word>> = wordDao.getAllWords()

    suspend fun insertWord(word: Word) = wordDao.insertWord(word)

    suspend fun getQuizQuestions(): List<Question> {
        val fiveWords = wordDao.getRandomFiveWords()
        val correctWord = fiveWords.random()
        val shuffledWords = fiveWords.shuffled()
        return shuffledWords.map {
            Question(
                correctWord = correctWord,
                incorrectWords = shuffledWords.filter { it != correctWord },
            )
        }
    }

    suspend fun getRandomWord(): Word {
        return wordDao.getRandomWord()
    }

    suspend fun getRandomFiveWords(): List<Word> {
        return wordDao.getRandomFiveWords()
    }

    suspend fun insertWords(words: List<Word>) {
        wordDao.insertWords(words)
    }
}