package com.gultekinahmetabdullah.trainvoc.di

import com.gultekinahmetabdullah.trainvoc.core.common.DefaultDispatcherProvider
import com.gultekinahmetabdullah.trainvoc.core.common.DispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Application-level Hilt module
 *
 * Provides app-wide dependencies that don't fit into more specific modules
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    /**
     * Provides DispatcherProvider for coroutine dispatchers
     *
     * Binds the production implementation (DefaultDispatcherProvider)
     * to the DispatcherProvider interface. In tests, this can be replaced
     * with TestDispatcherProvider.
     */
    @Binds
    @Singleton
    abstract fun bindDispatcherProvider(
        defaultDispatcherProvider: DefaultDispatcherProvider
    ): DispatcherProvider
}
