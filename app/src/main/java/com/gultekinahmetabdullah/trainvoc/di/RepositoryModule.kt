package com.gultekinahmetabdullah.trainvoc.di

import com.gultekinahmetabdullah.trainvoc.database.ExamDao
import com.gultekinahmetabdullah.trainvoc.database.StatisticDao
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import com.gultekinahmetabdullah.trainvoc.database.WordExamCrossRefDao
import com.gultekinahmetabdullah.trainvoc.repository.IPreferencesRepository
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
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
 * Uses Dependency Inversion Principle (SOLID) - depends on repository interfaces.
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
    ): IWordRepository {
        return WordRepository(wordDao, statisticDao, wordExamCrossRefDao, examDao)
    }
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
