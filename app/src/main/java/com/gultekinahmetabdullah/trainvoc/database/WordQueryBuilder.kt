package com.gultekinahmetabdullah.trainvoc.database

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.gultekinahmetabdullah.trainvoc.classes.enums.QuizType

/**
 * Builder for creating dynamic Word queries to eliminate duplicate DAO methods.
 * This class consolidates 30+ nearly identical query methods into a single flexible builder.
 *
 * SECURITY: Uses parameterized queries to prevent SQL injection attacks.
 */
object WordQueryBuilder {

    /**
     * Builds a dynamic SQL query for fetching 5 words based on quiz type and optional filters.
     *
     * @param quizType The type of quiz (RANDOM, LEAST_CORRECT, MOST_WRONG, etc.)
     * @param level Optional word level filter (e.g., "A1", "B2")
     * @param exam Optional exam filter (e.g., "TOEFL", "IELTS")
     * @param limit Number of words to return (default: 5)
     * @return SupportSQLiteQuery ready to execute via @RawQuery with parameterized bind args
     */
    fun buildQuery(
        quizType: QuizType,
        level: String? = null,
        exam: String? = null,
        limit: Int = 5
    ): SupportSQLiteQuery {
        val bindArgs = mutableListOf<Any>()

        val baseQuery = buildString {
            append("SELECT w.* FROM words w")

            // Add JOIN if statistics-based or exam-based filtering is needed
            val needsStatsJoin = quizType != QuizType.RANDOM && quizType != QuizType.LEAST_RECENT && quizType != QuizType.MOST_RECENT
            val needsExamJoin = exam != null

            if (needsStatsJoin || quizType == QuizType.NOT_LEARNED) {
                append(" JOIN statistics s ON w.stat_id = s.stat_id")
            }

            if (needsExamJoin) {
                append(" JOIN word_exam_cross_ref wec ON w.word = wec.word")
                append(" JOIN exams e ON wec.exam = e.exam")
            }

            // WHERE clause with parameterized queries
            val whereConditions = mutableListOf<String>()

            // Level filter - use parameterized query to prevent SQL injection
            level?.let {
                whereConditions.add("w.level = ?")
                bindArgs.add(it)
            }

            // Exam filter - use parameterized query to prevent SQL injection
            exam?.let {
                whereConditions.add("e.exam = ?")
                bindArgs.add(it)
            }

            if (whereConditions.isNotEmpty()) {
                append(" WHERE ${whereConditions.joinToString(" AND ")}")
            }

            // ORDER BY clause based on quiz type
            append(" ORDER BY ")
            append(getOrderByClause(quizType))
            append(", RANDOM()")

            // LIMIT - safe as it's an Int parameter
            append(" LIMIT $limit")
        }

        return SimpleSQLiteQuery(baseQuery, bindArgs.toTypedArray())
    }

    /**
     * Generates the ORDER BY clause based on quiz type.
     */
    private fun getOrderByClause(quizType: QuizType): String {
        return when (quizType) {
            QuizType.NOT_LEARNED -> "s.learned ASC"
            QuizType.RANDOM -> "RANDOM()"
            QuizType.LEAST_CORRECT -> "s.correct_count ASC"
            QuizType.MOST_CORRECT -> "s.correct_count DESC"
            QuizType.LEAST_WRONG -> "s.wrong_count ASC"
            QuizType.MOST_WRONG -> "s.wrong_count DESC"
            QuizType.LEAST_REVIEWED -> "(s.correct_count + s.wrong_count + s.skipped_count) ASC"
            QuizType.MOST_REVIEWED -> "(s.correct_count + s.wrong_count + s.skipped_count) DESC"
            QuizType.LEAST_RECENT -> "w.last_reviewed ASC"
            QuizType.MOST_RECENT -> "w.last_reviewed DESC"
        }
    }
}
