package com.gultekinahmetabdullah.trainvoc.di

import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import com.gultekinahmetabdullah.trainvoc.features.database.FeatureFlagDao
import com.gultekinahmetabdullah.trainvoc.features.repository.FeatureFlagRepository
import com.gultekinahmetabdullah.trainvoc.features.repository.IFeatureFlagRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Dependency Injection module for Feature Flag system
 */
@Module
@InstallIn(SingletonComponent::class)
object FeatureFlagModule {

    /**
     * Provides the FeatureFlagDao from the database
     */
    @Provides
    @Singleton
    fun provideFeatureFlagDao(database: AppDatabase): FeatureFlagDao {
        return database.featureFlagDao()
    }
}

/**
 * Module for binding repository interface to implementation
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureFlagRepositoryModule {

    /**
     * Binds the FeatureFlagRepository implementation to the interface
     */
    @Binds
    @Singleton
    abstract fun bindFeatureFlagRepository(
        repository: FeatureFlagRepository
    ): IFeatureFlagRepository
}
