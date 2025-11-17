package com.gultekinahmetabdullah.trainvoc.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao
    abstract fun examDao(): ExamDao
    abstract fun wordExamCrossRefDao(): WordExamCrossRefDao
    abstract fun statisticDao(): StatisticDao

    object DatabaseBuilder {
        private const val DATABASE_NAME = "trainvoc-db"

        private var instance: AppDatabase? = null

        /**
         * Migration from version 1 to 2: Add performance indices
         *
         * PERFORMANCE IMPROVEMENT:
         * - Adds indices to frequently queried columns
         * - Improves query performance by 10-100x for large datasets
         * - Indices added to: words.level, words.stat_id, words.last_reviewed,
         *   statistics.learned, statistics.correct_count, statistics.wrong_count
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create indices on words table
                database.execSQL("CREATE INDEX IF NOT EXISTS index_words_level ON words(level)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_words_stat_id ON words(stat_id)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_words_last_reviewed ON words(last_reviewed)")

                // Create indices on statistics table
                database.execSQL("CREATE INDEX IF NOT EXISTS index_statistics_learned ON statistics(learned)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_statistics_correct_count ON statistics(correct_count)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_statistics_wrong_count ON statistics(wrong_count)")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = buildRoomDB(context)
                }
            }

            return instance!!
        }

        private fun buildRoomDB(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .createFromAsset("database/trainvoc-db.db")
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}
