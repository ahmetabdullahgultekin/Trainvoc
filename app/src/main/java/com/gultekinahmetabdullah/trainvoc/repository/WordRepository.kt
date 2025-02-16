package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.classes.enums.QuizType
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Question
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordAskedInExams
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao: WordDao) {

    suspend fun resetProgress() {
        wordDao.resetProgress()
    }

    suspend fun getCorrectAnswers(): Int = wordDao.getCorrectAnswers()

    suspend fun getWrongAnswers(): Int = wordDao.getWrongAnswers()

    suspend fun getSkippedAnswers(): Int = wordDao.getSkippedAnswers()

    suspend fun getTotalTimeSpent(): Int = wordDao.getTotalTimeSpent()

    suspend fun getLastAnswered(): Long = wordDao.getLastAnswered()

    suspend fun getAllWordsAskedInExams(): List<WordAskedInExams> = wordDao.getAllWordsWithExams()


    /*suspend fun updateWordStats(statistic: Statistic, word: Word) {
        // Update the statistics of the word
        wordDao.insertStatistic(statistic)
        // Update the related word statistic id
        wordDao.updateWordStatId(word.statId, word.word)
    }*/

    suspend fun updateWordStats(statistic: Statistic, word: Word) {
        // Check if the statistic already exists
        val existingStatistic = wordDao.getStatByValues(
            statistic.correctCount,
            statistic.wrongCount,
            statistic.skippedCount
        )

        if (existingStatistic != null) {
            // If the statistic exists, update the word's stat_id
            wordDao.updateWordStatId(existingStatistic.statId, word.word)
        } else {
            // If the statistic does not exist, insert the new statistic
            val newStatId = wordDao.insertStatistic(statistic).toInt()
            wordDao.updateWordStatId(newStatId, word.word)
        }

        // Check if the previous statistic has no relation with any word
        val previousStatId = word.statId
        val wordCount = wordDao.getWordCountByStatId(previousStatId)
        if (wordCount == 0) {
            // If no word is associated with the previous statistic, delete it
            wordDao.deleteStatistic(previousStatId)
        }
    }

    suspend fun getWordStats(word: Word): Statistic {
        return wordDao.getStatById(word.statId)
    }

    suspend fun updateLastAnswered(word: String) {
        wordDao.updateLastReviewed(word, System.currentTimeMillis())
    }

    suspend fun updateSecondsSpent(secondsSpent: Int, word: Word) {
        wordDao.updateSecondsSpent(secondsSpent, word.word)
    }

    fun getAllWords(): Flow<List<Word>> = wordDao.getAllWords()

    suspend fun insertWord(word: Word) = wordDao.insertWord(word)

    suspend fun generateTenQuestions(quizType: QuizType): MutableList<Question> {
        val tenQuestions = mutableListOf<Question>()
        repeat(10) {
            val fiveWords = getFiveWords(quizType = quizType)
            val correctWord = fiveWords.random()
            val shuffledWords = fiveWords.shuffled()
            tenQuestions.add(
                Question(
                    correctWord = correctWord,
                    incorrectWords = shuffledWords.filter { it != correctWord },
                )
            )
        }
        return tenQuestions
    }

    private suspend fun getFiveWords(quizType: QuizType): List<Word> {
        return when (quizType) {
            QuizType.RANDOM -> wordDao.getRandomFiveWords()
            QuizType.LEAST_CORRECT -> wordDao.getLeastCorrectFiveWords()
            QuizType.LEAST_WRONG -> wordDao.getLeastWrongFiveWords()
            QuizType.LEAST_REVIEWED -> wordDao.getLeastReviewedFiveWords()
            QuizType.LEAST_RECENT -> wordDao.getLeastRecentFiveWords()
            QuizType.MOST_CORRECT -> wordDao.getMostCorrectFiveWords()
            QuizType.MOST_WRONG -> wordDao.getMostWrongFiveWords()
            QuizType.MOST_REVIEWED -> wordDao.getMostReviewedFiveWords()
            QuizType.MOST_RECENT -> wordDao.getMostRecentFiveWords()
        }
    }
}