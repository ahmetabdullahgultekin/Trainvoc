import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WordDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var wordDao: WordDao

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        wordDao = db.wordDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun testGetWord() = runTest {
        val word = Word("test", "testMeaning", WordLevel.A1, null)
        wordDao.insertWord(word)
        val fetched = wordDao.getWord("test")
        assertEquals("testMeaning", fetched.meaning)
    }

    @Test
    fun testGetAllWords() = runTest {
        val words = (1..3).map { Word("Word$it", "Meaning$it", WordLevel.B1, null) }.toMutableSet()
        wordDao.insertWords(words)
        val fetched = wordDao.getAllWords().first()
        assertEquals(3, fetched.size)
    }

    @Test
    fun testInsertWords() = runTest {
        val words = setOf(
            Word("test1", "mean1", WordLevel.A1, null),
            Word("test2", "mean2", WordLevel.C1, null)
        )
        val result = wordDao.insertWords(words.toMutableSet())
        assertEquals(2, result.size)
    }

    @Test
    fun testStatisticsQueries() = runTest {
        val statId = 1
        val statistic = Statistic(statId, 2, 1, 1)
        wordDao.insertStatistic(statistic)

        val word = Word("example", "örnek", WordLevel.A1, null)
        wordDao.insertWord(word)

        assertEquals(2, wordDao.getCorrectAnswers())
        assertEquals(1, wordDao.getWrongAnswers())
        assertEquals(1, wordDao.getSkippedAnswers())
        assertEquals(4, wordDao.getTotalAnswers())
    }


    @Test
    fun testUpdateSecondsSpent() = runTest {
        val word = Word("timeTest", "meaning", WordLevel.A1, null, secondsSpent = 0)
        wordDao.insertWord(word)
        wordDao.updateSecondsSpent(10, "timeTest")
        assertEquals(10, wordDao.getTimeSpent("timeTest"))
    }

    @Test
    fun testResetProgress() = runTest {
        wordDao.insertStatistic(Statistic(1, 1, 1, 1))
        wordDao.resetProgress()
        assertNull(wordDao.getStatById(1))
    }

    @Test
    fun testGetRandomFiveWords() = runTest {
        val words = (1..10).map { Word("Word$it", "Meaning$it", WordLevel.B1, null) }.toMutableSet()
        wordDao.insertWords(words)
        val randomWords = wordDao.getRandomFiveWords()
        assertEquals(5, randomWords.size)
    }

    @Test
    fun testUpdateLastReviewed() = runTest {
        val word = Word("reviewTest", "meaning", WordLevel.A1, null)
        wordDao.insertWord(word)
        val currentTime = System.currentTimeMillis()
        wordDao.updateLastReviewed("reviewTest", currentTime)
        assertEquals(currentTime, wordDao.getLastAnswered("reviewTest"))
    }

    @Test
    fun testGetWordCount() = runTest {
        val words = (1..5).map { Word("Word$it", "Meaning$it", WordLevel.B1, null) }.toMutableSet()
        wordDao.insertWords(words)
        assertEquals(5, wordDao.getWordCount())
    }

    @Test
    fun testDeleteStatistic() = runTest {
        val stat = Statistic(1, 3, 2, 1)
        wordDao.insertStatistic(stat)
        wordDao.deleteStatistic(1)
        assertNull(wordDao.getStatById(1))
    }
}