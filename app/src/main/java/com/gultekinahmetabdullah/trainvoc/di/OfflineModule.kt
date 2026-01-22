package com.gultekinahmetabdullah.trainvoc.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Dagger Hilt module for offline/sync dependencies
 * Note: Gson is provided by NetworkModule to avoid duplicate bindings
 */
@Module
@InstallIn(SingletonComponent::class)
object OfflineModule {
    // Gson is provided by NetworkModule - no duplicate needed here
}
