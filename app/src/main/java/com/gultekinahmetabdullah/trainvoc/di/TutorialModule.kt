package com.gultekinahmetabdullah.trainvoc.di

import com.gultekinahmetabdullah.trainvoc.repository.ITutorialPreferencesRepository
import com.gultekinahmetabdullah.trainvoc.repository.TutorialPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for tutorial-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class TutorialModule {

    @Binds
    @Singleton
    abstract fun bindTutorialPreferencesRepository(
        repository: TutorialPreferencesRepository
    ): ITutorialPreferencesRepository
}
