package com.gultekinahmetabdullah.trainvoc.di

import com.gultekinahmetabdullah.trainvoc.srs.algorithm.FsrsAlgorithm
import com.gultekinahmetabdullah.trainvoc.srs.domain.ISrsSchedulerService
import com.gultekinahmetabdullah.trainvoc.srs.domain.SrsSchedulerService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt wiring for the SRS engine (#99 S3, design doc §7).
 *
 * Provides the pure [FsrsAlgorithm] (published default weights) and binds the
 * [ISrsSchedulerService] to its default implementation. The `ReviewScheduleDao`
 * is provided by [DatabaseModule] alongside the other Room DAOs.
 */
@Module
@InstallIn(SingletonComponent::class)
object SrsModule {

    @Provides
    @Singleton
    fun provideFsrsAlgorithm(): FsrsAlgorithm = FsrsAlgorithm()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SrsBindingModule {

    @Binds
    @Singleton
    abstract fun bindSrsSchedulerService(impl: SrsSchedulerService): ISrsSchedulerService
}
