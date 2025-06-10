package com.gultekinahmetabdullah.trainvoc.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.classes.word.WordExamCrossRef

@Database(
    entities = [
        Word::class,
        Statistic::class,
        Exam::class,
        WordExamCrossRef::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao
    abstract fun examDao(): ExamDao
    abstract fun wordExamCrossRefDao(): WordExamCrossRefDao
    abstract fun statisticDao(): StatisticDao

    object DatabaseBuilder {
        private const val DATABASE_NAME = "trainvoc-db"

        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = buildRoomDB(context)
                }
            }

            /**
             * Populate Database
             * Uncomment the following code to populate the database with words from the animations file
             */

            /*kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                populateDatabase(context, instance!!.wordDao())
            }*/

            return instance!!
        }

        private fun buildRoomDB(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
            /**
             * Uncomment the following line to create the database in memory
             */
        )
            .createFromAsset("database/trainvoc-db.db")
            .build()


        /**
         * Populate the database with words from the animations file
         * Uncomment the following code to populate the database with words from the animations file
         *
         */

        /*private suspend fun populateDatabase(context: Context, wordDao: WordDao) {

            try {
                fillWordsAndExams(
                    context,
                    wordDao,
                    instance!!.examDao(),
                    instance!!.wordExamCrossRefDao()
                )
                fillStatisticTable(
                    instance!!.statisticDao(),
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

        /**
         *  Insert the word if it does not exist in the database.
         *  In the json file, there are duplicate words.
         *  These words have different exam types.
         *  So, we need to insert all of them.
         *  If we encounter a duplicate word, instead of trying to insert it again,
         *  we just add the exam type to the existing word.
         *  This is done by the onConflict = OnConflictStrategy.IGNORE parameter.
         *  If the word already exists in the database, the insert operation is ignored.
         *  This way, we can insert all the words in the json file.
         */

        /*private suspend fun fillWordsAndExams(
            context: Context,
            wordDao: WordDao,
            examDao: ExamDao,
            wordExamCrossRefDao: WordExamCrossRefDao
        ) {
            val inputStream = context.assets.open("database/all_words.json")
            val reader = InputStreamReader(inputStream)
            val wordListType = object : TypeToken<Map<String, List<Word>>>() {}.type
            val wordsMap: Map<String, List<Word>> = Gson().fromJson(reader, wordListType)

            val allWords = mutableSetOf<Word>()
            val examWithWords = mutableSetOf<ExamWithWords>()
            wordsMap.map { (examOrLevel, words) ->
                // Of examOrLevel is a level, set level field of the word as exam
                // If examOrLevel is an exam, return ExamWithWords with the exam field set as exam
                if (Exam.examTypes.map { it.exam }.contains(examOrLevel)) {
                    examWithWords.add(
                        ExamWithWords(
                            exam = Exam(examOrLevel),
                            words = words
                        )
                    )
                }
                allWords.addAll(
                    words.map {
                        val standardizedWord = standardizeWord(it.word)
                        val standardizedMeaning = standardizeMeaning(it.meaning)
                        Word(
                            word = standardizedWord,
                            meaning = standardizedMeaning,
                            // examOrLevel is a
                            // level: A! -> Beginner, A2 -> Elementary, B1 -> Intermediate,
                            // B2 -> Upper Intermediate, C1 -> Advanced, C2 -> Proficiency
                            level = WordLevel.entries.find { level ->
                                level.name.contains(examOrLevel)
                            },
                            lastReviewed = it.lastReviewed,
                            secondsSpent = it.secondsSpent
                        )
                    }
                )
            }

            wordDao.insertWords(allWords)
            fillExamTable(examDao)
            fillWordExamCrossRefTable(wordExamCrossRefDao, examWithWords)
        }

        private suspend fun fillExamTable(examDao: ExamDao) {
            val examEntities = Exam.examTypes.map {
                Exam(exam = it.exam)
            }
            examDao.insertExams(examEntities)
        }

        private suspend fun fillWordExamCrossRefTable(
            wordExamCrossRefDao: WordExamCrossRefDao, words: MutableSet<ExamWithWords>
        ) {
            val wordExamCrossRefs = words.flatMap { examWithWords ->
                examWithWords.words.map { word ->
                    WordExamCrossRef(
                        word = word.word,
                        exam = examWithWords.exam.exam
                    )
                }
            }
            wordExamCrossRefDao.insertWordExamCrossRefs(wordExamCrossRefs)
        }

        private suspend fun fillStatisticTable(statisticDao: StatisticDao) {
            statisticDao.insertDefaultStatistic()
        }

        // İngilizce kelimeler için standardizasyon
        private fun standardizeWord(text: String): String {
            val englishLocale = java.util.Locale.ENGLISH
            return if (text.all { it.isUpperCase() }) {
                // Tamamen büyük harfse olduğu gibi bırak
                text
            } else {
                // Karışık ise standardize et
                // Türkçe büyük İ harfini küçük 'i' yap, büyük I harfini küçük 'i' yap
                val fixed = text.replace('İ', 'I').replace('ı', 'i')
                fixed.lowercase(englishLocale).replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(englishLocale) else it.toString()
                }
            }
        }

        // Türkçe anlamlar için standardizasyon
        private fun standardizeMeaning(text: String): String {
            val turkishLocale = java.util.Locale.forLanguageTag("tr-TR")
            return if (text.all { it.isUpperCase() }) {
                // Tamamen büyük harfse olduğu gibi bırak
                text
            } else {
                // Karışık ise standardize et
                text.lowercase(turkishLocale).replaceFirstChar { it.titlecase(turkishLocale) }
            }
        }*/
    }
}
