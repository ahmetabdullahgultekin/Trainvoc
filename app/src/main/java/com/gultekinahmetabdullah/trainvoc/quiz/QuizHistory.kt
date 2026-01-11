package com.gultekinahmetabdullah.trainvoc.quiz

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import kotlinx.coroutines.flow.Flow

/**
 * Quiz History entity
 *
 * Stores overall quiz results including timestamp, total questions,
 * correct/wrong answers, time taken, and quiz type.
 */
@Entity(
    tableName = "quiz_history",
    indices = [Index(value = ["timestamp"])]
)
data class QuizHistory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "totalQuestions")
    val totalQuestions: Int,

    @ColumnInfo(name = "correctAnswers")
    val correctAnswers: Int,

    @ColumnInfo(name = "wrongAnswers")
    val wrongAnswers: Int,

    @ColumnInfo(name = "skippedQuestions")
    val skippedQuestions: Int = 0,

    @ColumnInfo(name = "timeTaken")
    val timeTaken: String, // Format: "MM:SS"

    @ColumnInfo(name = "quizType")
    val quizType: String,

    @ColumnInfo(name = "accuracy")
    val accuracy: Float
)

/**
 * Quiz Question Result entity
 *
 * Stores individual question results for each quiz,
 * allowing users to review missed words.
 */
@Entity(
    tableName = "quiz_question_results",
    foreignKeys = [
        ForeignKey(
            entity = QuizHistory::class,
            parentColumns = ["id"],
            childColumns = ["quizId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Word::class,
            parentColumns = ["word"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["quizId"]),
        Index(value = ["wordId"])
    ]
)
data class QuizQuestionResult(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "quizId")
    val quizId: Int,

    @ColumnInfo(name = "wordId")
    val wordId: String,

    @ColumnInfo(name = "isCorrect")
    val isCorrect: Boolean
)

/**
 * DAO for Quiz History operations
 */
@Dao
interface QuizHistoryDao {

    /**
     * Insert a new quiz result
     * @return the id of the inserted quiz
     */
    @Insert
    suspend fun insertQuizHistory(quizHistory: QuizHistory): Long

    /**
     * Insert question results for a quiz
     */
    @Insert
    suspend fun insertQuestionResults(questionResults: List<QuizQuestionResult>)

    /**
     * Get the last quiz result
     */
    @Query("SELECT * FROM quiz_history ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastQuizResult(): QuizHistory?

    /**
     * Get quiz history (last N quizzes)
     */
    @Query("SELECT * FROM quiz_history ORDER BY timestamp DESC LIMIT :limit")
    fun getQuizHistory(limit: Int = 10): Flow<List<QuizHistory>>

    /**
     * Get quiz history by quiz type
     */
    @Query("SELECT * FROM quiz_history WHERE quizType = :quizType ORDER BY timestamp DESC")
    suspend fun getQuizHistoryByType(quizType: String): List<QuizHistory>

    /**
     * Get question results for a specific quiz
     */
    @Query("SELECT * FROM quiz_question_results WHERE quizId = :quizId")
    suspend fun getQuestionResults(quizId: Int): List<QuizQuestionResult>

    /**
     * Get incorrect word IDs for a specific quiz
     */
    @Query("SELECT wordId FROM quiz_question_results WHERE quizId = :quizId AND isCorrect = 0")
    suspend fun getIncorrectWordIds(quizId: Int): List<String>

    /**
     * Get total quiz count
     */
    @Query("SELECT COUNT(*) FROM quiz_history")
    suspend fun getTotalQuizCount(): Int

    /**
     * Get average accuracy
     */
    @Query("SELECT AVG(accuracy) FROM quiz_history")
    suspend fun getAverageAccuracy(): Float?

    /**
     * Get total questions answered
     */
    @Query("SELECT SUM(totalQuestions) FROM quiz_history")
    suspend fun getTotalQuestionsAnswered(): Int?

    /**
     * Delete old quiz history (older than N days)
     */
    @Query("DELETE FROM quiz_history WHERE timestamp < :cutoffTimestamp")
    suspend fun deleteOldQuizzes(cutoffTimestamp: Long)
}
