package com.gultekinahmetabdullah.trainvoc.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gultekinahmetabdullah.trainvoc.classes.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader

@Database(entities = [Word::class], version = 1)
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
            *
             */
            /*
            scope.launch(Dispatchers.IO) {
                populateDatabase(context, instance!!.wordDao())
            }
             */

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
        *
        private suspend fun populateDatabase(context: Context, wordDao: WordDao) {
            val inputStream = context.assets.open("json/extracted_words.json")
            val reader = InputStreamReader(inputStream)
            val wordListType = object : TypeToken<List<Word>>() {}.type
            val words: List<Word> = Gson().fromJson(reader, wordListType)
            val wordEntities = words.map { Word(word = it.word, meaning = it.meaning) }
            val seenWords = HashSet<String>()
            wordEntities.forEach {
                if (!seenWords.add(it.word)) {
                    println("Duplicate Word: ${it.word}, Meaning: ${it.meaning}")
                }
            }
            println(
                "Total Words: ${wordEntities.size}, Unique Words: ${seenWords.size}"
            )
            wordDao.insertWords(wordEntities)
        }
        */
    }
}
