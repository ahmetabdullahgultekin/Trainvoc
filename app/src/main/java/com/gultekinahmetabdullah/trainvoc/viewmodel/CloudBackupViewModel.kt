package com.gultekinahmetabdullah.trainvoc.viewmodel

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.gultekinahmetabdullah.trainvoc.cloud.AuthResult
import com.gultekinahmetabdullah.trainvoc.cloud.DriveBackup
import com.gultekinahmetabdullah.trainvoc.cloud.DriveBackupResult
import com.gultekinahmetabdullah.trainvoc.cloud.DriveBackupService
import com.gultekinahmetabdullah.trainvoc.cloud.DriveRestoreResult
import com.gultekinahmetabdullah.trainvoc.cloud.GoogleAuthManager
import com.gultekinahmetabdullah.trainvoc.sync.ConflictStrategy
import com.gultekinahmetabdullah.trainvoc.worker.DriveBackupWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * ViewModel for Cloud Backup screen
 *
 * Manages:
 * - Google authentication state
 * - Backup/restore operations
 * - Backup history
 * - Auto-backup settings
 * - UI state and loading
 *
 * Features:
 * - Sign in/out from Google Drive
 * - Manual backup upload
 * - Browse backup history
 * - Restore from backup
 * - Delete old backups
 * - Enable/disable auto-backup
 *
 * Usage:
 * ```kotlin
 * @Composable
 * fun CloudBackupScreen(
 *     viewModel: CloudBackupViewModel = hiltViewModel()
 * ) {
 *     val authState by viewModel.authState.collectAsState()
 *     val backups by viewModel.backups.collectAsState()
 *
 *     Button(onClick = { viewModel.signIn(activity) }) {
 *         Text("Sign In")
 *     }
 * }
 * ```
 */
@HiltViewModel
class CloudBackupViewModel @Inject constructor(
    private val authManager: GoogleAuthManager,
    private val backupService: DriveBackupService,
    private val workManager: WorkManager
) : ViewModel() {

    companion object {
        private const val TAG = "CloudBackupViewModel"
    }

    // Authentication state
    private val _authState = MutableStateFlow<CloudAuthState>(CloudAuthState.Loading)
    val authState: StateFlow<CloudAuthState> = _authState.asStateFlow()

    // Backup list
    private val _backups = MutableStateFlow<List<DriveBackup>>(emptyList())
    val backups: StateFlow<List<DriveBackup>> = _backups.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Auto-backup enabled state
    private val _autoBackupEnabled = MutableStateFlow(false)
    val autoBackupEnabled: StateFlow<Boolean> = _autoBackupEnabled.asStateFlow()

    // Error/success messages
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        checkAuthState()
        // TODO: Load auto-backup preference from SharedPreferences
        _autoBackupEnabled.value = false
    }

    /**
     * Check current authentication state
     */
    fun checkAuthState() {
        viewModelScope.launch {
            _authState.value = CloudAuthState.Loading

            val account = authManager.getSignedInAccount()
            _authState.value = if (account != null && authManager.isSignedIn()) {
                CloudAuthState.SignedIn(
                    email = account.email ?: "Unknown",
                    displayName = account.displayName ?: "User"
                )
            } else {
                CloudAuthState.SignedOut
            }

            // Load backups if signed in
            if (_authState.value is CloudAuthState.SignedIn) {
                refreshBackups()
            }
        }
    }

    /**
     * Get sign-in intent for launching authentication flow
     *
     * Call this and launch the intent with startActivityForResult
     */
    fun getSignInIntent(): Intent {
        return authManager.getSignInIntent()
    }

    /**
     * Handle sign-in result from activity
     *
     * Call this from onActivityResult or ActivityResultLauncher
     */
    fun handleSignInResult(data: Intent?) {
        viewModelScope.launch {
            _isLoading.value = true

            when (val result = authManager.handleSignInResult(data)) {
                is AuthResult.Success -> {
                    Log.i(TAG, "Sign-in successful: ${result.account.email}")
                    _message.value = "Signed in successfully"
                    checkAuthState()
                }
                is AuthResult.Failure -> {
                    Log.e(TAG, "Sign-in failed: ${result.error}")
                    _message.value = "Sign-in failed: ${result.error}"
                    _authState.value = CloudAuthState.SignedOut
                }
                AuthResult.Cancelled -> {
                    Log.d(TAG, "Sign-in cancelled")
                    _message.value = null
                    _authState.value = CloudAuthState.SignedOut
                }
            }

            _isLoading.value = false
        }
    }

    /**
     * Sign out from Google Drive
     */
    fun signOut() {
        viewModelScope.launch {
            _isLoading.value = true

            authManager.signOut()
            _authState.value = CloudAuthState.SignedOut
            _backups.value = emptyList()
            _message.value = "Signed out successfully"

            // Disable auto-backup when signing out
            if (_autoBackupEnabled.value) {
                setAutoBackup(false)
            }

            _isLoading.value = false
        }
    }

    /**
     * Upload current data as backup to Google Drive
     */
    fun uploadBackup() {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = null

            when (val result = backupService.uploadBackup()) {
                is DriveBackupResult.Success -> {
                    Log.i(TAG, "Backup uploaded: ${result.fileName}")
                    _message.value = "Backup uploaded successfully (${result.wordCount} words)"
                    refreshBackups()
                }
                is DriveBackupResult.Failure -> {
                    Log.e(TAG, "Backup upload failed: ${result.error}")
                    _message.value = "Backup failed: ${result.error}"
                }
            }

            _isLoading.value = false
        }
    }

    /**
     * Refresh list of available backups
     */
    fun refreshBackups() {
        viewModelScope.launch {
            _isLoading.value = true

            val fetchedBackups = backupService.listBackups()
            _backups.value = fetchedBackups

            Log.d(TAG, "Loaded ${fetchedBackups.size} backup(s)")
            _isLoading.value = false
        }
    }

    /**
     * Restore data from a backup
     *
     * @param fileId Google Drive file ID to restore from
     * @param conflictStrategy How to handle conflicts
     */
    fun restoreBackup(
        fileId: String,
        conflictStrategy: ConflictStrategy = ConflictStrategy.MERGE_PREFER_REMOTE
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = "Restoring backup..."

            when (val result = backupService.downloadAndRestoreBackup(fileId, conflictStrategy)) {
                is DriveRestoreResult.Success -> {
                    Log.i(TAG, "Backup restored: ${result.wordsRestored} words")
                    _message.value = "Restored successfully (${result.wordsRestored} words)"
                }
                is DriveRestoreResult.Failure -> {
                    Log.e(TAG, "Restore failed: ${result.error}")
                    _message.value = "Restore failed: ${result.error}"
                }
            }

            _isLoading.value = false
        }
    }

    /**
     * Delete a backup from Google Drive
     *
     * @param fileId Google Drive file ID to delete
     */
    fun deleteBackup(fileId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val success = backupService.deleteBackup(fileId)
            if (success) {
                Log.i(TAG, "Backup deleted")
                _message.value = "Backup deleted"
                refreshBackups()
            } else {
                Log.e(TAG, "Delete failed")
                _message.value = "Failed to delete backup"
            }

            _isLoading.value = false
        }
    }

    /**
     * Enable or disable automatic daily backups
     *
     * @param enabled true to enable auto-backup, false to disable
     */
    fun setAutoBackup(enabled: Boolean) {
        viewModelScope.launch {
            _autoBackupEnabled.value = enabled

            if (enabled) {
                scheduleAutoBackup()
                _message.value = "Auto-backup enabled (daily, WiFi only)"
            } else {
                cancelAutoBackup()
                _message.value = "Auto-backup disabled"
            }

            // TODO: Save preference to SharedPreferences
        }
    }

    /**
     * Schedule periodic auto-backup work
     */
    private fun scheduleAutoBackup() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)  // WiFi only
            .setRequiresBatteryNotLow(true)                  // Good battery
            .build()

        val workRequest = PeriodicWorkRequestBuilder<DriveBackupWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.HOURS)  // First backup in 1 hour
            .build()

        workManager.enqueueUniquePeriodicWork(
            DriveBackupWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,  // Don't restart if already scheduled
            workRequest
        )

        Log.d(TAG, "Auto-backup scheduled")
    }

    /**
     * Cancel scheduled auto-backup work
     */
    private fun cancelAutoBackup() {
        workManager.cancelUniqueWork(DriveBackupWorker.WORK_NAME)
        Log.d(TAG, "Auto-backup cancelled")
    }

    /**
     * Clear current message
     */
    fun clearMessage() {
        _message.value = null
    }
}

/**
 * Authentication state for cloud backup
 */
sealed class CloudAuthState {
    /**
     * Loading authentication state
     */
    data object Loading : CloudAuthState()

    /**
     * User is signed in
     *
     * @property email User's email address
     * @property displayName User's display name
     */
    data class SignedIn(
        val email: String,
        val displayName: String
    ) : CloudAuthState()

    /**
     * User is not signed in
     */
    data object SignedOut : CloudAuthState()
}
