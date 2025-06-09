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

    fun isLearned(statistic: Statistic): Boolean {
        return statistic.correctCount > (statistic.wrongCount + statistic.skippedCount)
    }

    @Transaction
    suspend fun updateWordStats(statistic: Statistic, word: Word) {
        val updatedStatistic = statistic.copy(learned = isLearned(statistic))
        val wordCount = statisticDao.getWordCountByStatId(word.statId)
        // TODO: There is a error here, SQLConstraintException: UNIQUE constraint failed: statistics.stat_id
        if (wordCount == 1) {
            // Stat sadece bu kelimeye ait, doğrudan güncelle
            statisticDao.updateStatistic(updatedStatistic.copy(statId = word.statId))
        } else {
            // Aynı değerlere ve learned alanına sahip bir Statistic var mı?
            val existingStatistic = wordDao.getStatByValues(
                updatedStatistic.correctCount,
                updatedStatistic.wrongCount,
                updatedStatistic.skippedCount,
                updatedStatistic.learned
            )
            if (existingStatistic != null && existingStatistic.learned == updatedStatistic.learned) {
                // Eğer zaten aynı statId'ye sahipse hiçbir şey yapma
                if (word.statId != existingStatistic.statId) {
                    wordDao.updateWordStatId(existingStatistic.statId, word.word)
                }
                // Güncelleme veya ekleme yapma, çakışmayı önle
            } else {
                // Yeni bir Statistic oluştur ve statId'yi ona güncelle
                val newStatId = statisticDao.insertStatistic(
                    updatedStatistic.copy(statId = 0)
                ).toInt()
                wordDao.updateWordStatId(newStatId, word.word)
            }
        }
        // Önceki Statistic'e bağlı kelime kalmadıysa sil
        val previousStatId = word.statId
        val prevWordCount = statisticDao.getWordCountByStatId(previousStatId)
        if (prevWordCount == 0) {
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
                // If the exam is "Mixed", we treat it as a special case
                if (parameter.exam == Exam.examTypes.last()) {
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
                } else {
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
            }

            /*else -> {
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
            }*/
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

    suspend fun markWordAsLearned(statId: Long) {
        statisticDao.markLearned(statId)
    }

    suspend fun getWordCountByStatId(statId: Int): Int = statisticDao.getWordCountByStatId(statId)

    suspend fun getLearnedStatisticByValues(
        correctCount: Int,
        wrongCount: Int,
        skippedCount: Int
    ): Statistic? =
        statisticDao.getLearnedStatisticByValues(correctCount, wrongCount, skippedCount)

    suspend fun updateWordStatId(statId: Int, word: String) = wordDao.updateWordStatId(statId, word)

    suspend fun insertStatistic(statistic: Statistic): Long =
        statisticDao.insertStatistic(statistic)

    // Toplam yapılan quiz sayısı (örnek: Exam tablosu veya benzeri bir tablodan alınabilir)
    suspend fun getTotalQuizCount(): Int {
        return statisticDao.getTotalAnsweredQuizCount()
    }

    // Bugün doğru cevaplanan soru sayısı
    suspend fun getDailyCorrectAnswers(): Int {
        return statisticDao.getDailyCorrectAnswers()
    }

    // Bu hafta doğru cevaplanan soru sayısı
    suspend fun getWeeklyCorrectAnswers(): Int {
        return statisticDao.getWeeklyCorrectAnswers()
    }

    // En çok yanlış yapılan kelime
    suspend fun getMostWrongWord(): String? {
        return wordDao.getMostWrongWord()
    }

    // En iyi kategori (en çok doğru yapılan seviye)
    suspend fun getBestCategory(): String? {
        return wordDao.getBestCategory()
    }
}