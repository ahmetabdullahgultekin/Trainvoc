package com.gultekinahmetabdullah.trainvoc.di

import com.gultekinahmetabdullah.trainvoc.domain.usecase.CalculateProgressUseCase
import com.gultekinahmetabdullah.trainvoc.domain.usecase.CheckLevelUnlockedUseCase
import com.gultekinahmetabdullah.trainvoc.domain.usecase.GenerateQuizQuestionsUseCase
import com.gultekinahmetabdullah.trainvoc.domain.usecase.UpdateWordStatisticsUseCase
import com.gultekinahmetabdullah.trainvoc.repository.IAnalyticsService
import com.gultekinahmetabdullah.trainvoc.repository.IProgressService
import com.gultekinahmetabdullah.trainvoc.repository.IQuizService
import com.gultekinahmetabdullah.trainvoc.repository.IWordStatisticsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for providing Use Cases.
 * Use Cases encapsulate business logic and make it reusable and testable.
 * Follows Interface Segregation Principle - each use case depends only on interfaces it needs.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGenerateQuizQuestionsUseCase(
        quizService: IQuizService
    ): GenerateQuizQuestionsUseCase {
        return GenerateQuizQuestionsUseCase(quizService)
    }

    @Provides
    fun provideUpdateWordStatisticsUseCase(
        wordStatisticsService: IWordStatisticsService
    ): UpdateWordStatisticsUseCase {
        return UpdateWordStatisticsUseCase(wordStatisticsService)
    }

    @Provides
    fun provideCheckLevelUnlockedUseCase(
        progressService: IProgressService
    ): CheckLevelUnlockedUseCase {
        return CheckLevelUnlockedUseCase(progressService)
    }

    @Provides
    fun provideCalculateProgressUseCase(
        progressService: IProgressService,
        analyticsService: IAnalyticsService
    ): CalculateProgressUseCase {
        return CalculateProgressUseCase(progressService, analyticsService)
    }
}
