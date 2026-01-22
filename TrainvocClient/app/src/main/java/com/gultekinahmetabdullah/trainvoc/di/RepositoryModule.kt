package com.gultekinahmetabdullah.trainvoc.di

import com.gultekinahmetabdullah.trainvoc.database.ExamDao
import com.gultekinahmetabdullah.trainvoc.database.StatisticDao
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import com.gultekinahmetabdullah.trainvoc.database.WordExamCrossRefDao
import com.gultekinahmetabdullah.trainvoc.repository.AnalyticsService
import com.gultekinahmetabdullah.trainvoc.repository.IAnalyticsService
import com.gultekinahmetabdullah.trainvoc.repository.IPreferencesRepository
import com.gultekinahmetabdullah.trainvoc.repository.IProgressService
import com.gultekinahmetabdullah.trainvoc.repository.IQuizService
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import com.gultekinahmetabdullah.trainvoc.repository.IWordStatisticsService
import com.gultekinahmetabdullah.trainvoc.repository.PreferencesRepository
import com.gultekinahmetabdullah.trainvoc.repository.ProgressService
import com.gultekinahmetabdullah.trainvoc.repository.QuizService
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import com.gultekinahmetabdullah.trainvoc.repository.WordStatisticsService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing repository dependencies.
 * Uses Interface Segregation and Single Responsibility Principles (SOLID).
 *
 * Each service is now a separate class handling one concern:
 * - WordRepository → IWordRepository: Core word data operations
 * - QuizService → IQuizService: Quiz generation
 * - WordStatisticsService → IWordStatisticsService: Word-level statistics
 * - ProgressService → IProgressService: Progress and level management
 * - AnalyticsService → IAnalyticsService: Aggregated analytics
 *
 * This replaces the previous "God Class" pattern where WordRepository
 * implemented all 5 interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    // Note: These services are @Singleton classes with @Inject constructors,
    // so Hilt can provide them directly. We only need to bind interfaces.

    @Provides
    @Singleton
    fun provideIWordRepository(wordRepository: WordRepository): IWordRepository = wordRepository

    @Provides
    @Singleton
    fun provideQuizService(quizService: QuizService): IQuizService = quizService

    @Provides
    @Singleton
    fun provideWordStatisticsService(
        wordStatisticsService: WordStatisticsService
    ): IWordStatisticsService = wordStatisticsService

    @Provides
    @Singleton
    fun provideProgressService(progressService: ProgressService): IProgressService = progressService

    @Provides
    @Singleton
    fun provideAnalyticsService(analyticsService: AnalyticsService): IAnalyticsService = analyticsService
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
