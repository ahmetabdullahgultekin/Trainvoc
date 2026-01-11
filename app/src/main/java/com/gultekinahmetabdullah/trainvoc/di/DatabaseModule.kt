package com.gultekinahmetabdullah.trainvoc.di

import android.content.Context
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

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        // Use the centralized DatabaseBuilder which has all migrations properly configured
        return AppDatabase.DatabaseBuilder.getInstance(context)
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

    @Provides
    fun provideGamesDao(database: AppDatabase): com.gultekinahmetabdullah.trainvoc.games.GamesDao {
        return database.gamesDao()
    }

    @Provides
    fun provideWordOfDayDao(database: AppDatabase): com.gultekinahmetabdullah.trainvoc.features.WordOfDayDao {
        return database.wordOfDayDao()
    }

    @Provides
    fun provideQuizHistoryDao(database: AppDatabase): com.gultekinahmetabdullah.trainvoc.quiz.QuizHistoryDao {
        return database.quizHistoryDao()
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): androidx.work.WorkManager {
        return androidx.work.WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideDataExporter(
        @ApplicationContext context: Context,
        database: AppDatabase
    ): com.gultekinahmetabdullah.trainvoc.sync.DataExporter {
        return com.gultekinahmetabdullah.trainvoc.sync.DataExporter(context, database)
    }
}
