package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.classes.Question
import com.gultekinahmetabdullah.trainvoc.classes.QuizType
import com.gultekinahmetabdullah.trainvoc.classes.Word
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao: WordDao) {

    suspend fun resetProgress() { wordDao.resetProgress() }

    suspend fun getCorrectAnswers(): Int = wordDao.getCorrectAnswers()

    suspend fun getWrongAnswers(): Int = wordDao.getWrongAnswers()

    suspend fun getSkippedAnswers(): Int = wordDao.getSkippedAnswers()

    suspend fun getTotalTimeSpent(): Int = wordDao.getTotalTimeSpent()

    suspend fun getLastAnswered(): Long = wordDao.getLastAnswered()

    suspend fun increaseCorrectAnswers(word: String) {
        wordDao.increaseCorrectAnswers(word)
    }

    suspend fun increaseWrongAnswers(word: String) {
        wordDao.increaseWrongAnswers(word)
    }

    suspend fun increaseSkippedAnswers(word: String) {
        wordDao.increaseSkippedAnswers(word)
    }

    suspend fun addTimeSpent(word: String, i: Int) {
        wordDao.addTimeSpent(word, i)
    }

    suspend fun updateLastAnswered(word: String) {
        wordDao.updateLastAnswered(word, System.currentTimeMillis())
    }

    fun getAllWords(): Flow<List<Word>> = wordDao.getAllWords()

    suspend fun insertWord(word: Word) = wordDao.insertWord(word)

    suspend fun generateTenQuestions(quizType: QuizType): MutableList<Question> {
        println(
            "WordRepository.generateTenQuestions: quizType = $quizType"
        )
        val tenQuestions = mutableListOf<Question>()
        repeat(10) {
            val fiveWords = getFiveWords(quizType = quizType)
            println(
                "WordRepository.generateTenQuestions: fiveWords = $fiveWords"
            )
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

    /*
    suspend fun getRandomQuizQuestions(): Collection<Question> {
        //val tenQuestions = mutableListOf<Question>()
        repeat(10) {
            val fiveWords = wordDao.getRandomFiveWords()
            generateQuestion(fiveWords, tenQuestions)
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

    suspend fun getLeastCorrectQuizQuestions(): Collection<Question> {
        val tenQuestions = mutableListOf<Question>()
        repeat(10) {
            val fiveWords = wordDao.getLeastCorrectFiveWords()
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

    suspend fun getLeastWrongQuizQuestions(): Collection<Question> {
        val tenQuestions = mutableListOf<Question>()
        repeat(10) {
            val fiveWords = wordDao.getLeastWrongFiveWords()
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

    suspend fun getLeastReviewedQuizQuestions(): Collection<Question> {
        val tenQuestions = mutableListOf<Question>()
        repeat(10) {
            val fiveWords = wordDao.getLeastReviewedFiveWords()
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

    fun getQuizQuestions(quizType: QuizType): Collection<Question> {
        return when (quizType) {
            QuizType.RANDOM -> getRandomQuizQuestions()
            QuizType.LEAST_CORRECT -> getLeastCorrectQuizQuestions()
            QuizType.LEAST_WRONG -> getLeastWrongQuizQuestions()
            QuizType.LEAST_REVIEWED -> getLeastReviewedQuizQuestions()
            QuizType.LEAST_RECENT -> getRandomQuizQuestions()
            QuizType.MOST_CORRECT -> getRandomQuizQuestions()
            QuizType.MOST_WRONG -> getRandomQuizQuestions()
            QuizType.MOST_REVIEWED -> getRandomQuizQuestions()
            QuizType.MOST_RECENT -> getRandomQuizQuestions()
            else -> getRandomQuizQuestions()
        }
    }

    fun getAllWords(): LiveData<List<Word>> = wordDao.getAllWords()
    suspend fun getWrongPercentage(): Double = wordDao.getWrongPercentage()
    suspend fun getLeastKnownWords(): List<Word> = wordDao.getLeastKnownWords()
    suspend fun insert(word: Word) { wordDao.insert(word) }

    suspend fun getQuizQuestions(): List<Question> {
        val tenQuestions = mutableListOf<Question>()
        repeat(10) {
            val fiveWords = wordDao.getRandomFiveWords()
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

    suspend fun updateWordStats(word: Word) {
        wordDao.updateWordStats(
            word.word,
            word.numberOfCorrectAnswers,
            word.numberOfWrongAnswers
        )
    }

    suspend fun generateQuestions(): List<Question> {
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
     */

}