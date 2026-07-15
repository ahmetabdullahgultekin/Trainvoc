package com.gultekinahmetabdullah.trainvoc.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.gultekinahmetabdullah.trainvoc.di.preferencesRepository
import com.gultekinahmetabdullah.trainvoc.repository.IPreferencesRepository

/**
 * Remembers the singleton [IPreferencesRepository] for composables that
 * need preferences without a ViewModel. Single source of truth for the
 * username/avatar — do not read them from plain SharedPreferences.
 */
@Composable
fun rememberPreferencesRepository(): IPreferencesRepository {
    val context = LocalContext.current
    return remember { preferencesRepository(context) }
}
