package com.gultekinahmetabdullah.trainvoc.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordExamCrossRef
import kotlinx.coroutines.Dispatchers
import java.io.InputStreamReader

@Database(
    entities = [Word::class, Statistic::class, Exam::class, WordExamCrossRef::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    object DatabaseBuilder {
        private const val DATABASE_NAME = "word-db"

        private var instance: AppDatabase? = null

        private val scope = kotlinx.coroutines.CoroutineScope(Dispatchers.IO)

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = buildRoomDB(context)
                }
            }

            /* Populate Database
            * Uncomment the following code to populate the database with words from the json file
            */
            /*            scope.launch(Dispatchers.IO) {
                            populateDatabase(context, instance!!.wordDao())
                        }*/


            return instance!!
        }

        private fun buildRoomDB(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
            /* Uncomment the following line to create the database in memory
            *
             */
        ).createFromAsset("database/word-db.db")
            .build()


        /*
        * Populate the database with words from the json file
        * Uncomment the following code to populate the database with words from the json file
        *
        */
        private suspend fun populateDatabase(context: Context, wordDao: WordDao) {

            val words = getWords(context)
            fillWordTable(context, wordDao, words)
            fillExamTable(wordDao)
            fillWordExamCrossRefTable(context, wordDao, words)
            fillStatisticTable(wordDao)
        }

        private fun getWords(context: Context): List<Word> {
            val inputStream = context.assets.open("json/extracted_words.json")
            val reader = InputStreamReader(inputStream)
            val wordListType = object : TypeToken<List<Word>>() {}.type
            return Gson().fromJson(reader, wordListType)
        }

        private suspend fun fillWordTable(context: Context, wordDao: WordDao, words: List<Word>) {
            val words = getWords(context)
            val wordEntities = words.map {
                Word(
                    word = it.word,
                    meaning = it.meaning,
                    level = it.level,
                    lastReviewed = it.lastReviewed,
                    secondsSpent = it.secondsSpent
                )
            }
            wordDao.insertWords(wordEntities)
        }

        private suspend fun fillExamTable(wordDao: WordDao) {
            val examEntities = Exam.examTypes.map {
                Exam(exam = it.exam)
            }
            wordDao.insertExams(examEntities)
        }

        private suspend fun fillWordExamCrossRefTable(
            context: Context, wordDao: WordDao, words: List<Word>
        ) {
            val words = getWords(context)
            val wordExamCrossRefs = words.map {
                WordExamCrossRef(
                    word = it.word,
                    // All of them YDS for now
                    exam = "YDS"
                )
            }
            wordDao.insertWordExamCrossRefs(wordExamCrossRefs)
        }

        private suspend fun fillStatisticTable(wordDao: WordDao) {
            val statistic = Statistic(
                correctCount = 0,
                wrongCount = 0,
                skippedCount = 0,
            )
            wordDao.insertStatistic(statistic)
        }

    }
}
