package com.gultekinahmetabdullah.trainvoc.integration

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Quiz
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration tests for quiz functionality
 *
 * Tests the complete quiz flow including:
 * - Question generation
 * - Answer checking
 * - Statistics updates
 * - Learning progress tracking
 */
@RunWith(AndroidJUnit4::class)
class QuizFlowIntegrationTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var repository: WordRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        database = AppDatabase.DatabaseBuilder.createTestDatabase(context)
        repository = WordRepository(
            wordDao = database.wordDao(),
            statisticDao = database.statisticDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testCompleteQuizFlow() = runBlocking {
        // Given: Database with words and initial statistics
        val words = createSampleWords(15)
        words.forEach { word ->
            database.wordDao().insertWord(word)

            val statistic = Statistic(
                statId = word.statId,
                correctCount = 0,
                wrongCount = 0,
                skippedCount = 0,
                learned = false
            )
            database.statisticDao().insertStatistic(statistic)
        }

        // When: Generate quiz questions
        val quizParameter = QuizParameter.LevelQuizParameter.A1
        val quiz = Quiz.Random
        val questions = repository.generateTenQuestions(quiz.type, quizParameter)

        // Then: Should generate questions
        assertTrue(questions.isNotEmpty())
        assertTrue(questions.size <= 10)

        // When: Answer questions and update statistics
        questions.forEach { question ->
            val word = words.find { it.word == question.correctWord.word }
            assertNotNull(word)

            val stats = repository.getWordStats(word!!)
            val updatedStats = stats.copy(correctCount = stats.correctCount + 1)

            repository.updateWordStats(updatedStats, word)
        }

        // Then: Statistics should be updated
        val updatedStats = database.statisticDao().getAllStatistics()
        assertTrue(updatedStats.any { it.correctCount > 0 })
    }

    @Test
    fun testLearningProgressTracking() = runBlocking {
        // Given: Word with statistics
        val word = Word(
            word = "test",
            meaning = "a test",
            level = WordLevel.A1,
            lastReviewed = null,
            statId = 1,
            secondsSpent = 0
        )
        database.wordDao().insertWord(word)

        val initialStat = Statistic(
            statId = 1,
            correctCount = 0,
            wrongCount = 0,
            skippedCount = 0,
            learned = false
        )
        database.statisticDao().insertStatistic(initialStat)

        // When: Answer correctly 5 times
        repeat(5) {
            val stats = repository.getWordStats(word)
            val updated = stats.copy(correctCount = stats.correctCount + 1)
            repository.updateWordStats(updated, word)
        }

        // Then: Word should be marked as learned
        val finalStats = repository.getWordStats(word)
        assertTrue(finalStats.correctCount == 5)
        assertTrue(repository.isLearned(finalStats))
    }

    @Test
    fun testQuizQuestionDiversity() = runBlocking {
        // Given: Words from different levels
        val levels = listOf(WordLevel.A1, WordLevel.A2, WordLevel.B1, WordLevel.B2)
        levels.forEachIndexed { levelIndex, level ->
            repeat(10) { wordIndex ->
                val word = Word(
                    word = "word_${level.name}_$wordIndex",
                    meaning = "meaning_${level.name}_$wordIndex",
                    level = level,
                    lastReviewed = null,
                    statId = (levelIndex * 10) + wordIndex + 1,
                    secondsSpent = 0
                )
                database.wordDao().insertWord(word)

                val stat = Statistic(
                    statId = word.statId,
                    correctCount = 0,
                    wrongCount = 0,
                    skippedCount = 0,
                    learned = false
                )
                database.statisticDao().insertStatistic(stat)
            }
        }

        // When: Generate questions for specific level
        val questionsA1 = repository.generateTenQuestions(
            Quiz.Random.type,
            QuizParameter.LevelQuizParameter.A1
        )

        val questionsB1 = repository.generateTenQuestions(
            Quiz.Random.type,
            QuizParameter.LevelQuizParameter.B1
        )

        // Then: Questions should be from requested levels
        assertTrue(questionsA1.all { it.correctWord.level == WordLevel.A1 })
        assertTrue(questionsB1.all { it.correctWord.level == WordLevel.B1 })
    }

    @Test
    fun testStatisticsConsistency() = runBlocking {
        // Given: Word and statistic
        val word = Word(
            word = "consistency",
            meaning = "state of being consistent",
            level = WordLevel.B1,
            lastReviewed = null,
            statId = 1,
            secondsSpent = 0
        )
        database.wordDao().insertWord(word)

        val stat = Statistic(
            statId = 1,
            correctCount = 0,
            wrongCount = 0,
            skippedCount = 0,
            learned = false
        )
        database.statisticDao().insertStatistic(stat)

        // When: Update with correct, wrong, and skipped answers
        var currentStat = repository.getWordStats(word)
        currentStat = currentStat.copy(correctCount = 3)
        repository.updateWordStats(currentStat, word)

        currentStat = repository.getWordStats(word)
        currentStat = currentStat.copy(wrongCount = 2)
        repository.updateWordStats(currentStat, word)

        currentStat = repository.getWordStats(word)
        currentStat = currentStat.copy(skippedCount = 1)
        repository.updateWordStats(currentStat, word)

        // Then: All counts should be maintained
        val finalStat = repository.getWordStats(word)
        assertEquals(3, finalStat.correctCount)
        assertEquals(2, finalStat.wrongCount)
        assertEquals(1, finalStat.skippedCount)
    }

    // Helper functions

    private fun createSampleWords(count: Int): List<Word> {
        return (1..count).map { index ->
            Word(
                word = "testword$index",
                meaning = "test meaning $index",
                level = WordLevel.A1,
                lastReviewed = null,
                statId = index,
                secondsSpent = 0
            )
        }
    }

    private fun assertNotNull(value: Any?) {
        assertTrue(value != null, "Value should not be null")
    }
}
