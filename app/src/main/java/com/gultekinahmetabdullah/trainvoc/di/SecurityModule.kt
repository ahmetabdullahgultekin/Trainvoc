package com.gultekinahmetabdullah.trainvoc.di

import android.content.Context
import com.gultekinahmetabdullah.trainvoc.security.SecurePreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for security-related dependencies
 *
 * Provides:
 * - SecurePreferencesManager for encrypted storage
 */
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    /**
     * Provides SecurePreferencesManager singleton
     *
     * Uses EncryptedSharedPreferences backed by Android Keystore
     * for secure data storage.
     */
    @Provides
    @Singleton
    fun provideSecurePreferencesManager(
        @ApplicationContext context: Context
    ): SecurePreferencesManager {
        return SecurePreferencesManager(context)
    }
}
