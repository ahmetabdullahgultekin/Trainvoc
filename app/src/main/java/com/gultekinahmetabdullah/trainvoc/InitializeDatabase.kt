package com.gultekinahmetabdullah.trainvoc

import android.app.Application
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase

class InitializeDatabase : Application() {

    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.DatabaseBuilder.getInstance(applicationContext)
    }
}