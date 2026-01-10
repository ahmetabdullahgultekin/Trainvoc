package com.gultekinahmetabdullah.trainvoc.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordAskedInExams
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    /**
     * Dynamic query method to replace 30+ duplicate methods.
     * Uses WordQueryBuilder to construct queries based on quiz type and filters.
     */
    @RawQuery
    suspend fun getWordsByQuery(query: SupportSQLiteQuery): List<Word>

    /**
     * Word queries
     *
     * These queries are used to interact with the words in the database.
     *
     * The words are stored in the database and can be retrieved, inserted, and updated.
     *
     * The words are used to generate questions for the quiz.
     *
     */

    // --- WORD QUERIES ---
    @Query("SELECT * FROM words WHERE word = :word")
    suspend fun getWord(word: String): Word

    @Query("SELECT * FROM words ORDER BY word ASC")
    fun getAllWords(): Flow<List<Word>>

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT 100")
    suspend fun getAllWordsList(): List<Word>

    // Get word count of a specific level
    @Query("SELECT COUNT(*) FROM words WHERE level = :level")
    suspend fun getWordCountByLevel(level: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word)

    // Insert words, if the words already exist, return the word
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWords(words: MutableSet<Word>): List<Long>

    /**
     * NOTE: All quiz-related word fetching methods (30+ methods) have been replaced
     * with a single dynamic query method: getWordsByQuery()
     * Use WordQueryBuilder to construct queries based on QuizType and filters.
     *
     * Previous methods eliminated:
     * - getRandomFiveWords/ByLevel/ByExam
     * - get[Least|Most][Correct|Wrong|Reviewed|Recent]FiveWords/ByLevel/ByExam
     * - getRandomFiveNotLearnedWords/ByLevel/ByExam
     *
     * This eliminates ~340 lines of duplicate code and improves maintainability.
     */

    /**
     * Level and statistics queries
     */
    @Query(
        """
    SELECT
        COUNT(*) AS learned_word_count
    FROM words w
    JOIN statistics s ON w.stat_id = s.stat_id
    WHERE w.level = :level AND s.learned = 1
    """
    )
    suspend fun getLevelUnlockerWordCount(level: String): Int

    // Get the total number of correct answers
    @Query(
        """
        SELECT SUM(correct_count) FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        """
    )
    suspend fun getCorrectAnswers(): Int

    // Get the total number of wrong answers
    @Query(
        """
        SELECT SUM(wrong_count) FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        """
    )
    suspend fun getWrongAnswers(): Int

    // Get the total number of skipped answers
    @Query(
        """
        SELECT SUM(skipped_count) FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        """
    )
    suspend fun getSkippedAnswers(): Int

    // Get the total number of answers
    @Query(
        """
        SELECT SUM(correct_count + wrong_count + skipped_count) FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        """
    )
    suspend fun getTotalAnswers(): Int

    // Get the total number of words
    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int

    // Get the statistics of a word
    @Query("SELECT * FROM statistics WHERE stat_id = :statId")
    suspend fun getStatById(statId: Int): Statistic

    @Query(
        """
        SELECT * FROM statistics
        WHERE correct_count = :correctCount
        AND wrong_count = :wrongCount
        AND skipped_count = :skippedCount
        AND learned = :learned
        """
    )
    suspend fun getStatByValues(
        correctCount: Int,
        wrongCount: Int,
        skippedCount: Int,
        learned: Boolean
    ): Statistic?

    // Get all the word with exams
    @Transaction
    @Query("SELECT * FROM words")
    suspend fun getAllWordsWithExams(): List<WordAskedInExams>

    // Get the exams of a word
    @Transaction
    @Query("SELECT * FROM words WHERE word = :word")
    suspend fun getExamsOfWord(word: String): List<WordAskedInExams>

    // Get the total time spent on the words
    @Query("SELECT SUM(seconds_spent) FROM words")
    suspend fun getTotalTimeSpent(): Int

    // Get the time spent on a specific word
    @Query("SELECT seconds_spent FROM words WHERE word = :word")
    suspend fun getTimeSpent(word: String): Int

    // Get the last time the word was answered
    @Query("SELECT last_reviewed FROM words WHERE word = :word")
    suspend fun getLastAnswered(word: String): Long

    // Get the last time any word was answered
    @Query("SELECT last_reviewed FROM words ORDER BY last_reviewed DESC LIMIT 1")
    suspend fun getLastAnswered(): Long

    // Update the last time the word was answered
    @Query("UPDATE words SET last_reviewed = :time WHERE word = :word")
    suspend fun updateLastReviewed(word: String, time: Long = System.currentTimeMillis())

    // Update the seconds spent on the word
    @Query("UPDATE words SET seconds_spent = seconds_spent + :secondsSpent WHERE word = :word")
    suspend fun updateSecondsSpent(secondsSpent: Int, word: String)

    // Update the word stat id
    @Query("UPDATE words SET stat_id = :statId WHERE word = :word")
    suspend fun updateWordStatId(statId: Int, word: String)

    @Query("UPDATE words SET stat_id = 0")
    suspend fun resetAllWordStatIds()

    @Query("UPDATE words SET last_reviewed = 0, seconds_spent = 0")
    suspend fun resetAllWords()

    // Get the word with the most wrong answers
    @Query(
        """
        SELECT w.word FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        ORDER BY s.wrong_count DESC LIMIT 1
    """
    )
    suspend fun getMostWrongWord(): String?

    // Get the best category (level with most correct answers)
    @Query(
        """
        SELECT w.level FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        GROUP BY w.level
        ORDER BY SUM(s.correct_count) DESC LIMIT 1
    """
    )
    suspend fun getBestCategory(): String?

    // Get total word count for a specific exam
    @Query(
        """
        SELECT COUNT(*) FROM words
        JOIN word_exam_cross_ref ON words.word = word_exam_cross_ref.word
        JOIN exams ON word_exam_cross_ref.exam = exams.exam
        WHERE exams.exam = :exam
        """
    )
    suspend fun getWordCountByExam(exam: String): Int

    // Get learned word count for a specific exam
    @Query(
        """
        SELECT COUNT(*) FROM words
        JOIN word_exam_cross_ref ON words.word = word_exam_cross_ref.word
        JOIN exams ON word_exam_cross_ref.exam = exams.exam
        JOIN statistics ON words.stat_id = statistics.stat_id
        WHERE exams.exam = :exam AND statistics.learned = 1
        """
    )
    suspend fun getLearnedWordCountByExam(exam: String): Int

    /**
     * Get a random word for notifications with optional filtering
     * Used by WordNotificationWorker for word quiz notifications
     */
    @Query(
        """
        SELECT w.* FROM words w
        LEFT JOIN statistics s ON w.stat_id = s.stat_id
        WHERE (:includeLearned = 1 OR s.learned = 0 OR s.learned IS NULL)
        AND (:level IS NULL OR w.level = :level)
        ORDER BY RANDOM()
        LIMIT 1
        """
    )
    suspend fun getRandomWordForNotification(
        includeLearned: Boolean = false,
        level: String? = null
    ): Word?

    /**
     * Get a random word from specified levels for notifications
     */
    @Query(
        """
        SELECT w.* FROM words w
        LEFT JOIN statistics s ON w.stat_id = s.stat_id
        WHERE (:includeLearned = 1 OR s.learned = 0 OR s.learned IS NULL)
        AND w.level IN (:levels)
        ORDER BY RANDOM()
        LIMIT 1
        """
    )
    suspend fun getRandomWordFromLevels(
        levels: List<String>,
        includeLearned: Boolean = false
    ): Word?
}