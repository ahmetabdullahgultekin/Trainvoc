package com.gultekinahmetabdullah.trainvoc.di

import com.gultekinahmetabdullah.trainvoc.domain.usecase.CalculateProgressUseCase
import com.gultekinahmetabdullah.trainvoc.domain.usecase.CheckLevelUnlockedUseCase
import com.gultekinahmetabdullah.trainvoc.domain.usecase.GenerateQuizQuestionsUseCase
import com.gultekinahmetabdullah.trainvoc.domain.usecase.UpdateWordStatisticsUseCase
import com.gultekinahmetabdullah.trainvoc.repository.WordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for providing Use Cases.
 * Use Cases encapsulate business logic and make it reusable and testable.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGenerateQuizQuestionsUseCase(
        repository: WordRepository
    ): GenerateQuizQuestionsUseCase {
        return GenerateQuizQuestionsUseCase(repository)
    }

    @Provides
    fun provideUpdateWordStatisticsUseCase(
        repository: WordRepository
    ): UpdateWordStatisticsUseCase {
        return UpdateWordStatisticsUseCase(repository)
    }

    @Provides
    fun provideCheckLevelUnlockedUseCase(
        repository: WordRepository
    ): CheckLevelUnlockedUseCase {
        return CheckLevelUnlockedUseCase(repository)
    }

    @Provides
    fun provideCalculateProgressUseCase(
        repository: WordRepository
    ): CalculateProgressUseCase {
        return CalculateProgressUseCase(repository)
    }
}
