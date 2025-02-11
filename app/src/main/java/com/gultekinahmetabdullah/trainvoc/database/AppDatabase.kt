package com.gultekinahmetabdullah.trainvoc.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gultekinahmetabdullah.trainvoc.classes.Word

@Database(entities = [Word::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    object DatabaseBuilder {
        private const val DATABASE_NAME = "word-db"

        private var instance: AppDatabase? = null

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
        ).build()
    }
}
