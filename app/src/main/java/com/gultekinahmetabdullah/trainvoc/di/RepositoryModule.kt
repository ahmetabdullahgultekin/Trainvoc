package com.gultekinahmetabdullah.trainvoc.di

import com.gultekinahmetabdullah.trainvoc.database.ExamDao
import com.gultekinahmetabdullah.trainvoc.database.StatisticDao
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import com.gultekinahmetabdullah.trainvoc.database.WordExamCrossRefDao
import com.gultekinahmetabdullah.trainvoc.repository.IAnalyticsService
import com.gultekinahmetabdullah.trainvoc.repository.IPreferencesRepository
import com.gultekinahmetabdullah.trainvoc.repository.IProgressService
import com.gultekinahmetabdullah.trainvoc.repository.IQuizService
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import com.gultekinahmetabdullah.trainvoc.repository.IWordStatisticsService
import com.gultekinahmetabdullah.trainvoc.repository.PreferencesRepository
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing repository dependencies.
 * Uses Interface Segregation Principle (SOLID) - provides focused interfaces.
 *
 * Clients can inject only the interface they need:
 * - IWordRepository: Core word data operations
 * - IQuizService: Quiz generation
 * - IWordStatisticsService: Word-level statistics
 * - IProgressService: Progress and level management
 * - IAnalyticsService: Aggregated analytics
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideWordRepository(
        wordDao: WordDao,
        statisticDao: StatisticDao,
        wordExamCrossRefDao: WordExamCrossRefDao,
        examDao: ExamDao
    ): WordRepository {
        return WordRepository(wordDao, statisticDao, wordExamCrossRefDao, examDao)
    }

    // Provide segregated interfaces - all bound to the same WordRepository instance

    @Provides
    @Singleton
    fun provideIWordRepository(wordRepository: WordRepository): IWordRepository = wordRepository

    @Provides
    @Singleton
    fun provideQuizService(wordRepository: WordRepository): IQuizService = wordRepository

    @Provides
    @Singleton
    fun provideWordStatisticsService(wordRepository: WordRepository): IWordStatisticsService = wordRepository

    @Provides
    @Singleton
    fun provideProgressService(wordRepository: WordRepository): IProgressService = wordRepository

    @Provides
    @Singleton
    fun provideAnalyticsService(wordRepository: WordRepository): IAnalyticsService = wordRepository
}

/**
 * Module for binding repository interfaces to implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindingModule {

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        preferencesRepository: PreferencesRepository
    ): IPreferencesRepository
}
