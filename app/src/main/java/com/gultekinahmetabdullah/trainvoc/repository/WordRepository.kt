package com.gultekinahmetabdullah.trainvoc.repository

import androidx.room.Transaction
import com.gultekinahmetabdullah.trainvoc.classes.enums.QuizType
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Question
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam
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
) : IWordRepository {

    override suspend fun resetProgress() {
        statisticDao.resetProgress()
        wordDao.resetAllWordStatIds()
        wordDao.resetAllWords()
    }

    override suspend fun getCorrectAnswers(): Int = wordDao.getCorrectAnswers()

    override suspend fun getWrongAnswers(): Int = wordDao.getWrongAnswers()

    override suspend fun getSkippedAnswers(): Int = wordDao.getSkippedAnswers()

    override suspend fun getTotalTimeSpent(): Int = wordDao.getTotalTimeSpent()

    override suspend fun getLastAnswered(): Long = wordDao.getLastAnswered()

    override suspend fun getAllWordsAskedInExams(): List<WordAskedInExams> = wordDao.getAllWordsWithExams()


    /*suspend fun updateWordStats(statistic: Statistic, word: Word) {
        // Update the statistics of the word
        wordDao.insertStatistic(statistic)
        // Update the related word statistic id
        wordDao.updateWordStatId(word.statId, word.word)
    }*/

    override fun isLearned(statistic: Statistic): Boolean {
        return statistic.correctCount > (statistic.wrongCount + statistic.skippedCount)
    }

    @Transaction
    override suspend fun updateWordStats(statistic: Statistic, word: Word) {
        val updatedStatistic = statistic.copy(learned = isLearned(statistic))
        val wordCount = statisticDao.getWordCountByStatId(word.statId)

        if (wordCount == 1) {
            // Statistic belongs only to this word, update it directly
            statisticDao.updateStatistic(updatedStatistic.copy(statId = word.statId))
        } else {
            // Check if a Statistic with these exact values already exists
            val existingStatistic = wordDao.getStatByValues(
                updatedStatistic.correctCount,
                updatedStatistic.wrongCount,
                updatedStatistic.skippedCount,
                updatedStatistic.learned
            )

            if (existingStatistic != null && existingStatistic.learned == updatedStatistic.learned) {
                // Reuse existing statistic if it's not already assigned to this word
                if (word.statId != existingStatistic.statId) {
                    wordDao.updateWordStatId(existingStatistic.statId, word.word)
                }
            } else {
                // Try to insert new statistic
                val newStatId = statisticDao.insertStatistic(
                    updatedStatistic.copy(statId = 0)
                )

                // Handle race condition: if insert returned -1 (conflict due to UNIQUE constraint)
                if (newStatId == -1L) {
                    // Another thread inserted the same statistic, query for it again
                    val raceConditionStat = wordDao.getStatByValues(
                        updatedStatistic.correctCount,
                        updatedStatistic.wrongCount,
                        updatedStatistic.skippedCount,
                        updatedStatistic.learned
                    )
                    if (raceConditionStat != null) {
                        wordDao.updateWordStatId(raceConditionStat.statId, word.word)
                    }
                } else {
                    // Successfully inserted, use the new stat ID
                    wordDao.updateWordStatId(newStatId.toInt(), word.word)
                }
            }
        }

        // Delete previous statistic if no words are using it
        val previousStatId = word.statId
        val prevWordCount = statisticDao.getWordCountByStatId(previousStatId)
        if (prevWordCount == 0) {
            statisticDao.deleteStatistic(previousStatId)
        }
    }

    override suspend fun getWordStats(word: Word): Statistic {
        return wordDao.getStatById(word.statId)
    }

    override suspend fun updateLastAnswered(word: String) {
        wordDao.updateLastReviewed(word, System.currentTimeMillis())
    }

    override suspend fun updateSecondsSpent(secondsSpent: Int, word: Word) {
        wordDao.updateSecondsSpent(secondsSpent, word.word)
    }

    override fun getAllWords(): Flow<List<Word>> = wordDao.getAllWords()

    override suspend fun insertWord(word: Word) = wordDao.insertWord(word)

    override suspend fun generateTenQuestions(
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
        // Determine level filter
        val level: String? = when (parameter) {
            is QuizParameter.Level -> parameter.wordLevel.name
            else -> null
        }

        // Determine exam filter (null for "Mixed" exam means all exams)
        val exam: String? = when (parameter) {
            is QuizParameter.ExamType -> {
                if (parameter.exam == Exam.examTypes.last()) null // "Mixed" exam
                else parameter.exam.exam
            }
            else -> null
        }

        // Use WordQueryBuilder to create dynamic query - replaces 30+ when branches
        val query = com.gultekinahmetabdullah.trainvoc.database.WordQueryBuilder.buildQuery(
            quizType = quizType,
            level = level,
            exam = exam,
            limit = 5
        )

        return wordDao.getWordsByQuery(query)
    }

    override suspend fun isLevelUnlocked(level: WordLevel): Boolean {
        // First level is unlocked by default
        if (level == WordLevel.A1) return true

        val levelUnlockerWordCount = wordDao.getLevelUnlockerWordCount(level.name)
        val levelWordCount = wordDao.getWordCountByLevel(level.name)
        println("Checking if level ${level.name} is unlocked: $levelWordCount words in level, $levelUnlockerWordCount words needed to unlock")
        return levelWordCount == levelUnlockerWordCount
    }

    override suspend fun getWordById(wordId: String): Word = wordDao.getWord(wordId)

    override suspend fun getExamsForWord(wordId: String): List<String> {
        return wordExamCrossRefDao.getExamNamesByWord(wordId)
    }

    override suspend fun markWordAsLearned(statId: Long) {
        statisticDao.markLearned(statId)
    }

    override suspend fun getWordCountByStatId(statId: Int): Int = statisticDao.getWordCountByStatId(statId)

    override suspend fun getLearnedStatisticByValues(
        correctCount: Int,
        wrongCount: Int,
        skippedCount: Int
    ): Statistic? =
        statisticDao.getLearnedStatisticByValues(correctCount, wrongCount, skippedCount)

    override suspend fun updateWordStatId(statId: Int, word: String) = wordDao.updateWordStatId(statId, word)

    override suspend fun insertStatistic(statistic: Statistic): Long =
        statisticDao.insertStatistic(statistic)

    // Total number of quizzes taken
    override suspend fun getTotalQuizCount(): Int {
        return statisticDao.getTotalAnsweredQuizCount()
    }

    // Number of correctly answered questions today
    override suspend fun getDailyCorrectAnswers(): Int {
        return statisticDao.getDailyCorrectAnswers()
    }

    // Number of correctly answered questions this week
    override suspend fun getWeeklyCorrectAnswers(): Int {
        return statisticDao.getWeeklyCorrectAnswers()
    }

    // Get the word with most wrong answers
    override suspend fun getMostWrongWord(): String? {
        return wordDao.getMostWrongWord()
    }

    // Get the best category (level with most correct answers)
    override suspend fun getBestCategory(): String? {
        return wordDao.getBestCategory()
    }

    override suspend fun getWordCountByLevel(level: String): Int = wordDao.getWordCountByLevel(level)
    override suspend fun getLearnedWordCount(level: String): Int = wordDao.getLevelUnlockerWordCount(level)
    override suspend fun getWordCountByExam(exam: String): Int = wordDao.getWordCountByExam(exam)
    override suspend fun getLearnedWordCountByExam(exam: String): Int =
        wordDao.getLearnedWordCountByExam(exam)
}