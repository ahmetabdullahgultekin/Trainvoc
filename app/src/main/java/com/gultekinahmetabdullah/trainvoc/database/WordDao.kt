package com.gultekinahmetabdullah.trainvoc.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordAskedInExams
import com.gultekinahmetabdullah.trainvoc.classes.word.WordExamCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    /**
     * Reset the progress of the user.
     *
     * Clear the rows in the statistics table and WordWithStats table.
     *
     * This will reset the user's progress and performance.
     *
     */
    @Query("DELETE FROM statistics")
    suspend fun resetProgress()

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

    @Query("SELECT * FROM words WHERE word = :word")
    suspend fun getWord(word: String): Word

    @Query("SELECT * FROM words ORDER BY word ASC")
    fun getAllWords(): Flow<List<Word>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word)

    // Insert words, if the words already exist, return the word
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWords(words: MutableSet<Word>): List<Long>

    @Insert
    suspend fun insertExams(exams: List<Exam>)

    @Transaction
    @Insert
    suspend fun insertWordExamCrossRefs(crossRefs: List<WordExamCrossRef>)

    // Insert statistics, if the statistics already exist, replace them
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStatistic(statistic: Statistic): Long

    @Query("SELECT COUNT(*) FROM words WHERE stat_id = :statId")
    suspend fun getWordCountByStatId(statId: Int): Int

    @Query("DELETE FROM statistics WHERE stat_id = :statId")
    suspend fun deleteStatistic(statId: Int)

    /*    @Transaction
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertWordWithStats(wordWithStats: WordWithStats)*/


    /**
     * Statistics queries
     *
     * These queries are used to get the statistics of the user.
     *
     * The statistics are used to show the user's progress and performance.
     *
     */

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
    """
    )
    suspend fun getStatByValues(correctCount: Int, wrongCount: Int, skippedCount: Int): Statistic

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

    /**
     * Update the statistics of a word in the database.
     *
     * The correct and wrong answers are updated based on the user's input.
     *
     * The word is then updated in the database.
     *
     */

    // Update the last time the word was answered
    @Query("UPDATE words SET last_reviewed = :time WHERE word = :word")
    suspend fun updateLastReviewed(word: String, time: Long = System.currentTimeMillis())

    // Update the seconds spent on the word
    @Query("UPDATE words SET seconds_spent = seconds_spent + :secondsSpent WHERE word = :word")
    suspend fun updateSecondsSpent(secondsSpent: Int, word: String)

    // Update the word stat id
    @Query("UPDATE words SET stat_id = :statId WHERE word = :word")
    suspend fun updateWordStatId(statId: Int, word: String)

    /**
     * Get a random list of words from the database.
     *
     * The list of words is shuffled and returned.
     *
     * @return A list of random words.
     */

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT 5")
    suspend fun getRandomFiveWords(): List<Word>

    // overload the function to get random words with a specific level
    @Query("SELECT * FROM words WHERE level = :level ORDER BY RANDOM() LIMIT 5")
    suspend fun getRandomFiveWordsByLevel(level: String): List<Word>

    // overload the function to get random words with a specific exam
    @Transaction
    @Query(
        """
        SELECT words.* FROM words
        JOIN word_exam_cross_ref ON words.word = word_exam_cross_ref.word
        JOIN exams ON word_exam_cross_ref.exam = exams.exam
        WHERE exams.exam = :exam
        ORDER BY RANDOM() LIMIT 5
    """
    )
    suspend fun getRandomFiveWordsByExam(exam: String): List<Word>

    /**
     * Question queries
     *
     * These queries are used to generate questions for the quiz.
     *
     * The questions are generated by selecting a random word from the database and shuffling the other words.
     *
     * The correct word is then added to the list of incorrect words and shuffled.
     *
     * The question is then created with the correct word and the shuffled list of incorrect words.
     *
     * The question is then added to the list of questions.
     *
     * The list of questions is then returned.
     *
     * The list of questions is then used to populate the quiz screen.
     *
     * The user can then select an answer and the answer is checked against the correct word.
     *
     */

    // Order the words by the number of correct answers in ascending order and return random 5 words
    @Query(
        """
        SELECT w.* FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        ORDER BY s.correct_count ASC, RANDOM()
        LIMIT 5
    """
    )
    suspend fun getLeastCorrectFiveWords(): List<Word>

    // overload the function to get the least correct words with a specific level
    @Query(
        """
        SELECT w.* FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        WHERE w.level = :level
        ORDER BY s.correct_count ASC, RANDOM()
        LIMIT 5
    """
    )
    suspend fun getLeastCorrectFiveWordsByLevel(level: String): List<Word>

    // overload the function to get the least correct words with a specific exam
    @Transaction
    @Query(
        """
        SELECT words.* FROM words
        JOIN word_exam_cross_ref ON words.word = word_exam_cross_ref.word
        JOIN exams ON word_exam_cross_ref.exam = exams.exam
        JOIN statistics ON words.stat_id = statistics.stat_id
        WHERE exams.exam = :exam
        ORDER BY statistics.correct_count ASC, RANDOM() LIMIT 5
    """
    )
    suspend fun getLeastCorrectFiveWordsByExam(exam: String): List<Word>

    // Order the words by the number of correct answers in descending order and return random 5 words
    @Query(
        """
        SELECT w.* FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        ORDER BY s.correct_count DESC, RANDOM()
        LIMIT 5
    """
    )
    suspend fun getMostCorrectFiveWords(): List<Word>

    // overload the function to get the most correct words with a specific level
    @Query(
        """
        SELECT w.* FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        WHERE w.level = :level
        ORDER BY s.correct_count DESC, RANDOM()
        LIMIT 5
    """
    )
    suspend fun getMostCorrectFiveWordsByLevel(level: String): List<Word>

    // overload the function to get the most correct words with a specific exam
    @Transaction
    @Query(
        """
        SELECT words.* FROM words
        JOIN word_exam_cross_ref ON words.word = word_exam_cross_ref.word
        JOIN exams ON word_exam_cross_ref.exam = exams.exam
        JOIN statistics ON words.stat_id = statistics.stat_id
        WHERE exams.exam = :exam
        ORDER BY statistics.correct_count DESC, RANDOM() LIMIT 5
    """
    )
    suspend fun getMostCorrectFiveWordsByExam(exam: String): List<Word>

    // Order the words by the number of wrong answers in ascending order and return random 5 words
    @Query(
        """
        SELECT w.* FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        ORDER BY s.wrong_count ASC, RANDOM()
        LIMIT 5
    """
    )
    suspend fun getLeastWrongFiveWords(): List<Word>

    // overload the function to get the least wrong words with a specific level
    @Query(
        """
        SELECT w.* FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        WHERE w.level = :level
        ORDER BY s.wrong_count ASC, RANDOM()
        LIMIT 5
    """
    )
    suspend fun getLeastWrongFiveWordsByLevel(level: String): List<Word>

    // overload the function to get the least wrong words with a specific exam
    @Transaction
    @Query(
        """
        SELECT words.* FROM words
        JOIN word_exam_cross_ref ON words.word = word_exam_cross_ref.word
        JOIN exams ON word_exam_cross_ref.exam = exams.exam
        JOIN statistics ON words.stat_id = statistics.stat_id
        WHERE exams.exam = :exam
        ORDER BY statistics.wrong_count ASC, RANDOM() LIMIT 5
    """
    )
    suspend fun getLeastWrongFiveWordsByExam(exam: String): List<Word>


    // Order the words by the number of wrong answers in descending order and return random 5 words
    @Query(
        """
        SELECT w.* FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        ORDER BY s.wrong_count DESC, RANDOM()
        LIMIT 5
    """
    )
    suspend fun getMostWrongFiveWords(): List<Word>

    // overload the function to get the most wrong words with a specific level
    @Query(
        """
        SELECT w.* FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        WHERE w.level = :level
        ORDER BY s.wrong_count DESC, RANDOM()
        LIMIT 5
    """
    )
    suspend fun getMostWrongFiveWordsByLevel(level: String): List<Word>

    // overload the function to get the most wrong words with a specific exam
    @Transaction
    @Query(
        """
        SELECT words.* FROM words
        JOIN word_exam_cross_ref ON words.word = word_exam_cross_ref.word
        JOIN exams ON word_exam_cross_ref.exam = exams.exam
        JOIN statistics ON words.stat_id = statistics.stat_id
        WHERE exams.exam = :exam
        ORDER BY statistics.wrong_count DESC, RANDOM() LIMIT 5
    """
    )
    suspend fun getMostWrongFiveWordsByExam(exam: String): List<Word>

    // Order the words by the number of total answers in ascending order and return random 5 words
    @Query(
        """
        SELECT w.* FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        ORDER BY s.correct_count + s.wrong_count + s.skipped_count ASC, RANDOM() 
        LIMIT 5
    """
    )
    suspend fun getLeastReviewedFiveWords(): List<Word>

    // overload the function to get the least reviewed words with a specific level
    @Query(
        """
        SELECT w.* FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        WHERE w.level = :level
        ORDER BY s.correct_count + s.wrong_count + s.skipped_count ASC, RANDOM() 
        LIMIT 5
    """
    )
    suspend fun getLeastReviewedFiveWordsByLevel(level: String): List<Word>

    // overload the function to get the least reviewed words with a specific exam
    @Transaction
    @Query(
        """
        SELECT words.* FROM words
        JOIN word_exam_cross_ref ON words.word = word_exam_cross_ref.word
        JOIN exams ON word_exam_cross_ref.exam = exams.exam
        JOIN statistics ON words.stat_id = statistics.stat_id
        WHERE exams.exam = :exam
        ORDER BY statistics.correct_count + statistics.wrong_count + statistics.skipped_count ASC,
        RANDOM() LIMIT 5
    """
    )
    suspend fun getLeastReviewedFiveWordsByExam(exam: String): List<Word>

    // Order the words by the number of total answers in descending order and return random 5 words
    @Query(
        """
        SELECT w.* FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        ORDER BY s.correct_count + s.wrong_count + s.skipped_count DESC, RANDOM() 
        LIMIT 5
    """
    )
    suspend fun getMostReviewedFiveWords(): List<Word>

    // overload the function to get the most reviewed words with a specific level
    @Query(
        """
        SELECT w.* FROM words w
        JOIN statistics s ON w.stat_id = s.stat_id
        WHERE w.level = :level
        ORDER BY s.correct_count + s.wrong_count + s.skipped_count DESC, RANDOM() 
        LIMIT 5
    """
    )
    suspend fun getMostReviewedFiveWordsByLevel(level: String): List<Word>

    // overload the function to get the most reviewed words with a specific exam
    @Transaction
    @Query(
        """
        SELECT words.* FROM words
        JOIN word_exam_cross_ref ON words.word = word_exam_cross_ref.word
        JOIN exams ON word_exam_cross_ref.exam = exams.exam
        JOIN statistics ON words.stat_id = statistics.stat_id
        WHERE exams.exam = :exam
        ORDER BY statistics.correct_count + statistics.wrong_count + statistics.skipped_count DESC,
        RANDOM() LIMIT 5
    """
    )
    suspend fun getMostReviewedFiveWordsByExam(exam: String): List<Word>

    // Order the words by the last time they were answered in ascending order and return random 5 words
    @Query("SELECT * FROM words ORDER BY last_reviewed ASC, RANDOM() LIMIT 5")
    suspend fun getLeastRecentFiveWords(): List<Word>

    // overload the function to get the least recent words with a specific level
    @Query("SELECT * FROM words WHERE level = :level ORDER BY last_reviewed ASC, RANDOM() LIMIT 5")
    suspend fun getLeastRecentFiveWordsByLevel(level: String): List<Word>

    // overload the function to get the least recent words with a specific exam
    @Transaction
    @Query(
        """
        SELECT words.* FROM words
        JOIN word_exam_cross_ref ON words.word = word_exam_cross_ref.word
        JOIN exams ON word_exam_cross_ref.exam = exams.exam
        WHERE exams.exam = :exam
        ORDER BY words.last_reviewed ASC, RANDOM() LIMIT 5
    """
    )
    suspend fun getLeastRecentFiveWordsByExam(exam: String): List<Word>

    // Order the words by the last time they were answered in descending order and return random 5 words
    @Query("SELECT * FROM words ORDER BY last_reviewed DESC, RANDOM() LIMIT 5")
    suspend fun getMostRecentFiveWords(): List<Word>

    // overload the function to get the most recent words with a specific level
    @Query("SELECT * FROM words WHERE level = :level ORDER BY last_reviewed DESC, RANDOM() LIMIT 5")
    suspend fun getMostRecentFiveWordsByLevel(level: String): List<Word>

    // overload the function to get the most recent words with a specific exam
    @Transaction
    @Query(
        """
        SELECT words.* FROM words
        JOIN word_exam_cross_ref ON words.word = word_exam_cross_ref.word
        JOIN exams ON word_exam_cross_ref.exam = exams.exam
        WHERE exams.exam = :exam
        ORDER BY words.last_reviewed DESC, RANDOM() LIMIT 5
    """
    )
    suspend fun getMostRecentFiveWordsByExam(exam: String): List<Word>
}