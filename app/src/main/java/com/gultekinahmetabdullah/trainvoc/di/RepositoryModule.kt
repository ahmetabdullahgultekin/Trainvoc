package com.gultekinahmetabdullah.trainvoc.di

import com.gultekinahmetabdullah.trainvoc.database.ExamDao
import com.gultekinahmetabdullah.trainvoc.database.StatisticDao
import com.gultekinahmetabdullah.trainvoc.database.WordDao
import com.gultekinahmetabdullah.trainvoc.database.WordExamCrossRefDao
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
}
