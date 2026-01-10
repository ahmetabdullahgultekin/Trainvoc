package com.gultekinahmetabdullah.trainvoc.di

import android.content.Context
import androidx.room.Room
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import com.gultekinahmetabdullah.trainvoc.database.ExamDao
import com.gultekinahmetabdullah.trainvoc.database.StatisticDao
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import com.gultekinahmetabdullah.trainvoc.database.WordExamCrossRefDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "trainvoc-db"

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .createFromAsset("database/trainvoc-db.db")
            .build()
    }

    @Provides
    fun provideWordDao(database: AppDatabase): WordDao {
        return database.wordDao()
    }

    @Provides
    fun provideExamDao(database: AppDatabase): ExamDao {
        return database.examDao()
    }

    @Provides
    fun provideWordExamCrossRefDao(database: AppDatabase): WordExamCrossRefDao {
        return database.wordExamCrossRefDao()
    }

    @Provides
    fun provideStatisticDao(database: AppDatabase): StatisticDao {
        return database.statisticDao()
    }

    @Provides
    fun provideAudioCacheDao(database: AppDatabase): com.gultekinahmetabdullah.trainvoc.audio.AudioCacheDao {
        return database.audioCacheDao()
    }

    @Provides
    fun provideWordImageDao(database: AppDatabase): com.gultekinahmetabdullah.trainvoc.images.WordImageDao {
        return database.wordImageDao()
    }

    @Provides
    fun provideExampleSentenceDao(database: AppDatabase): com.gultekinahmetabdullah.trainvoc.examples.ExampleSentenceDao {
        return database.exampleSentenceDao()
    }

    @Provides
    fun provideSyncQueueDao(database: AppDatabase): com.gultekinahmetabdullah.trainvoc.offline.SyncQueueDao {
        return database.syncQueueDao()
    }

    @Provides
    fun provideSubscriptionDao(database: AppDatabase): com.gultekinahmetabdullah.trainvoc.billing.database.SubscriptionDao {
        return database.subscriptionDao()
    }

    @Provides
    fun provideGamificationDao(database: AppDatabase): com.gultekinahmetabdullah.trainvoc.gamification.GamificationDao {
        return database.gamificationDao()
    }
}
