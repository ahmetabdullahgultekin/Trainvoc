package com.gultekinahmetabdullah.trainvoc.di

import android.content.Context
import com.gultekinahmetabdullah.trainvoc.repository.IPreferencesRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * Hilt entry point for accessing the preferences repository from places
 * that are not constructor-injected (Compose UI without a ViewModel,
 * plain helper classes like DataExporter/DataImporter).
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface PreferencesRepositoryEntryPoint {
    fun preferencesRepository(): IPreferencesRepository
}

/** Resolve the singleton [IPreferencesRepository] from any [Context]. */
fun preferencesRepository(context: Context): IPreferencesRepository =
    EntryPointAccessors.fromApplication(
        context.applicationContext,
        PreferencesRepositoryEntryPoint::class.java
    ).preferencesRepository()
