package com.gultekinahmetabdullah.trainvoc.repository

import com.gultekinahmetabdullah.trainvoc.classes.enums.QuizType
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Question
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordAskedInExams
import com.gultekinahmetabdullah.trainvoc.database.ExamDao
import com.gultekinahmetabdullah.trainvoc.database.StatisticDao
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import com.gultekinahmetabdullah.trainvoc.database.WordExamCrossRefDao
import kotlinx.coroutines.flow.Flow

class WordRepository(
    private val wordDao: WordDao,
    private val statisticDao: StatisticDao,
    private val wordExamCrossRefDao: WordExamCrossRefDao,
    private val examDao: ExamDao
) {

    suspend fun resetProgress() {
        statisticDao.resetProgress()
        wordDao.resetAllWordStatIds()
        wordDao.resetAllWords()
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

        val isExisting = existingStatistic?.statId != null

        if (isExisting) {
            // If the statistic exists, update the word's stat_id
            wordDao.updateWordStatId(existingStatistic.statId, word.word)
        } else {
            // If the statistic does not exist, insert the new statistic
            println(
                "Inserting new statistic: correctCount=${statistic.correctCount}, " +
                        "wrongCount=${statistic.wrongCount}, skippedCount=${statistic.skippedCount}"
            )
            val newStatId = statisticDao.insertStatistic(
                Statistic(
                    statId = 0, // Assuming statId is auto-generated
                    correctCount = statistic.correctCount,
                    wrongCount = statistic.wrongCount,
                    skippedCount = statistic.skippedCount
                )
            ).toInt()
            println(
                "Inserted new statistic with ID: $newStatId, " +
                        "correctCount=${statistic.correctCount}, " +
                        "wrongCount=${statistic.wrongCount}, skippedCount=${statistic.skippedCount}"
            )
            wordDao.updateWordStatId(newStatId, word.word)
            println(
                "Updated word '${word.word}' with new statId: $newStatId"
            )
        }

        // Check if the previous statistic has no relation with any word
        val previousStatId = word.statId
        val wordCount = statisticDao.getWordCountByStatId(previousStatId)
        if (wordCount == 0) {
            // If no word is associated with the previous statistic, delete it
            statisticDao.deleteStatistic(previousStatId)
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

    suspend fun generateTenQuestions(
        quizType: QuizType, quizParameter: QuizParameter
    ): MutableList<Question> {

        val tenQuestions = mutableListOf<Question>()
        repeat(10) {
            val fiveWords = getFiveWords(quizType = quizType, parameter = quizParameter)
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

    private suspend fun getFiveWords(quizType: QuizType, parameter: QuizParameter): List<Word> {
        return when (parameter) {
            is QuizParameter.Level -> {
                val level = when (parameter.wordLevel) {
                    WordLevel.A1 -> "A1"
                    WordLevel.A2 -> "A2"
                    WordLevel.B1 -> "B1"
                    WordLevel.B2 -> "B2"
                    WordLevel.C1 -> "C1"
                    WordLevel.C2 -> "C2"
                }
                when (quizType) {
                    QuizType.RANDOM -> wordDao.getRandomFiveWordsByLevel(level)
                    QuizType.LEAST_CORRECT -> wordDao.getLeastCorrectFiveWordsByLevel(level)
                    QuizType.LEAST_WRONG -> wordDao.getLeastWrongFiveWordsByLevel(level)
                    QuizType.LEAST_REVIEWED -> wordDao.getLeastReviewedFiveWordsByLevel(level)
                    QuizType.LEAST_RECENT -> wordDao.getLeastRecentFiveWordsByLevel(level)
                    QuizType.MOST_CORRECT -> wordDao.getMostCorrectFiveWordsByLevel(level)
                    QuizType.MOST_WRONG -> wordDao.getMostWrongFiveWordsByLevel(level)
                    QuizType.MOST_REVIEWED -> wordDao.getMostReviewedFiveWordsByLevel(level)
                    QuizType.MOST_RECENT -> wordDao.getMostRecentFiveWordsByLevel(level)
                }
            }

            is QuizParameter.ExamType -> {
                when (quizType) {
                    QuizType.RANDOM -> wordDao.getRandomFiveWordsByExam(parameter.exam.exam)
                    QuizType.LEAST_CORRECT -> wordDao.getLeastCorrectFiveWordsByExam(parameter.exam.exam)
                    QuizType.LEAST_WRONG -> wordDao.getLeastWrongFiveWordsByExam(parameter.exam.exam)
                    QuizType.LEAST_REVIEWED -> wordDao.getLeastReviewedFiveWordsByExam(parameter.exam.exam)
                    QuizType.LEAST_RECENT -> wordDao.getLeastRecentFiveWordsByExam(parameter.exam.exam)
                    QuizType.MOST_CORRECT -> wordDao.getMostCorrectFiveWordsByExam(parameter.exam.exam)
                    QuizType.MOST_WRONG -> wordDao.getMostWrongFiveWordsByExam(parameter.exam.exam)
                    QuizType.MOST_REVIEWED -> wordDao.getMostReviewedFiveWordsByExam(parameter.exam.exam)
                    QuizType.MOST_RECENT -> wordDao.getMostRecentFiveWordsByExam(parameter.exam.exam)
                }
            }

            else -> {
                when (quizType) {
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
    }

    suspend fun isLevelUnlocked(level: WordLevel): Boolean {
        // First level is unlocked by default
        if (level == WordLevel.A1) return true

        val levelUnlockerWordCount = wordDao.getLevelUnlockerWordCount(level.name)
        val levelWordCount = wordDao.getWordCountByLevel(level.name)
        return levelWordCount == levelUnlockerWordCount
    }

    suspend fun getWordById(wordId: String): Word = wordDao.getWord(wordId)

    suspend fun getExamsForWord(wordId: String): List<String> {
        return wordExamCrossRefDao.getExamNamesByWord(wordId)
    }
}