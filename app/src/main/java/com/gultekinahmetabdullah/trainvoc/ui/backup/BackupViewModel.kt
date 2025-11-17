package com.gultekinahmetabdullah.trainvoc.ui.backup

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import com.gultekinahmetabdullah.trainvoc.sync.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Backup/Restore operations
 *
 * Manages:
 * - Data export (JSON/CSV)
 * - Data import (JSON/CSV)
 * - Cloud sync
 * - Conflict resolution
 * - UI state and progress
 */
@HiltViewModel
class BackupViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase
) : ViewModel() {

    private val exporter = DataExporter(context, database)
    private val importer = DataImporter(context, database)
    private val cloudBackup = CloudBackupManager(context, database)

    private val _uiState = MutableStateFlow<BackupUiState>(BackupUiState.Idle)
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _availableBackups = MutableStateFlow<List<BackupFileInfo>>(emptyList())
    val availableBackups: StateFlow<List<BackupFileInfo>> = _availableBackups.asStateFlow()

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _lastSyncTime = MutableStateFlow<Long?>(null)
    val lastSyncTime: StateFlow<Long?> = _lastSyncTime.asStateFlow()

    private val _autoBackupEnabled = MutableStateFlow(false)
    val autoBackupEnabled: StateFlow<Boolean> = _autoBackupEnabled.asStateFlow()

    init {
        loadAvailableBackups()
        loadCloudBackupState()
        observeCloudSyncState()
    }

    /**
     * Export data to JSON format
     */
    fun exportToJson(
        includeStatistics: Boolean = true,
        includePreferences: Boolean = true
    ) {
        viewModelScope.launch {
            _uiState.value = BackupUiState.Exporting
            _progress.value = 0f

            val result = exporter.exportToJson(
                includeStatistics = includeStatistics,
                includePreferences = includePreferences
            )

            _progress.value = 1f

            when (result) {
                is BackupResult.Success -> {
                    _uiState.value = BackupUiState.ExportSuccess(
                        filePath = result.filePath,
                        wordCount = result.wordCount,
                        sizeBytes = result.sizeBytes
                    )
                    loadAvailableBackups()
                }
                is BackupResult.Failure -> {
                    _uiState.value = BackupUiState.Error(result.error)
                }
            }
        }
    }

    /**
     * Export data to CSV format
     */
    fun exportToCsv() {
        viewModelScope.launch {
            _uiState.value = BackupUiState.Exporting
            _progress.value = 0f

            val result = exporter.exportToCsv()
            _progress.value = 1f

            when (result) {
                is BackupResult.Success -> {
                    _uiState.value = BackupUiState.ExportSuccess(
                        filePath = result.filePath,
                        wordCount = result.wordCount,
                        sizeBytes = result.sizeBytes
                    )
                    loadAvailableBackups()
                }
                is BackupResult.Failure -> {
                    _uiState.value = BackupUiState.Error(result.error)
                }
            }
        }
    }

    /**
     * Export statistics to CSV
     */
    fun exportStatistics() {
        viewModelScope.launch {
            _uiState.value = BackupUiState.Exporting
            _progress.value = 0f

            val result = exporter.exportStatisticsCsv()
            _progress.value = 1f

            when (result) {
                is BackupResult.Success -> {
                    _uiState.value = BackupUiState.ExportSuccess(
                        filePath = result.filePath,
                        wordCount = result.wordCount,
                        sizeBytes = result.sizeBytes
                    )
                    loadAvailableBackups()
                }
                is BackupResult.Failure -> {
                    _uiState.value = BackupUiState.Error(result.error)
                }
            }
        }
    }

    /**
     * Import data from backup file
     */
    fun importFromBackup(
        filePath: String,
        conflictStrategy: ConflictStrategy = ConflictStrategy.MERGE_SMART
    ) {
        viewModelScope.launch {
            _uiState.value = BackupUiState.Importing
            _progress.value = 0f

            // Validate first
            val validation = importer.validateBackup(filePath)
            if (validation is ValidationResult.Invalid) {
                _uiState.value = BackupUiState.Error(validation.reason)
                return@launch
            }

            // Import
            val result = importer.importFromJson(
                filePath = filePath,
                conflictStrategy = conflictStrategy,
                validateChecksum = true,
                onProgress = { progress ->
                    _progress.value = progress
                }
            )

            when (result) {
                is RestoreResult.Success -> {
                    _uiState.value = BackupUiState.ImportSuccess(
                        wordsRestored = result.wordsRestored,
                        statisticsRestored = result.statisticsRestored,
                        conflictsResolved = result.conflictsResolved
                    )
                }
                is RestoreResult.Failure -> {
                    _uiState.value = BackupUiState.Error(result.error)
                }
                is RestoreResult.Conflict -> {
                    _uiState.value = BackupUiState.ConflictDetected(
                        conflicts = result.conflicts,
                        backupData = result.backupData
                    )
                }
            }
        }
    }

    /**
     * Resolve conflicts manually
     */
    fun resolveConflicts(
        backupData: BackupData,
        strategy: ConflictStrategy
    ) {
        viewModelScope.launch {
            _uiState.value = BackupUiState.Importing
            _progress.value = 0f

            // Re-import with chosen strategy
            val result = importer.importFromJson(
                filePath = "", // Already have data
                conflictStrategy = strategy,
                onProgress = { progress ->
                    _progress.value = progress
                }
            )

            when (result) {
                is RestoreResult.Success -> {
                    _uiState.value = BackupUiState.ImportSuccess(
                        wordsRestored = result.wordsRestored,
                        statisticsRestored = result.statisticsRestored,
                        conflictsResolved = result.conflictsResolved
                    )
                }
                is RestoreResult.Failure -> {
                    _uiState.value = BackupUiState.Error(result.error)
                }
                else -> {
                    _uiState.value = BackupUiState.Error("Conflict resolution failed")
                }
            }
        }
    }

    /**
     * Backup to cloud
     */
    fun backupToCloud() {
        viewModelScope.launch {
            _uiState.value = BackupUiState.UploadingToCloud
            _progress.value = 0f

            val result = cloudBackup.backupToCloud { progress ->
                _progress.value = progress
            }

            when (result) {
                is CloudBackupResult.Success -> {
                    _uiState.value = BackupUiState.CloudBackupSuccess(
                        cloudFileId = result.cloudFileId,
                        sizeBytes = result.sizeBytes
                    )
                    loadCloudBackupState()
                }
                is CloudBackupResult.Failure -> {
                    _uiState.value = BackupUiState.Error(result.error)
                }
            }
        }
    }

    /**
     * Restore from cloud
     */
    fun restoreFromCloud(
        conflictStrategy: ConflictStrategy = ConflictStrategy.MERGE_SMART
    ) {
        viewModelScope.launch {
            _uiState.value = BackupUiState.DownloadingFromCloud
            _progress.value = 0f

            val result = cloudBackup.restoreFromCloud(
                conflictStrategy = conflictStrategy,
                onProgress = { progress ->
                    _progress.value = progress
                }
            )

            when (result) {
                is RestoreResult.Success -> {
                    _uiState.value = BackupUiState.ImportSuccess(
                        wordsRestored = result.wordsRestored,
                        statisticsRestored = result.statisticsRestored,
                        conflictsResolved = result.conflictsResolved
                    )
                    loadCloudBackupState()
                }
                is RestoreResult.Failure -> {
                    _uiState.value = BackupUiState.Error(result.error)
                }
                is RestoreResult.Conflict -> {
                    _uiState.value = BackupUiState.ConflictDetected(
                        conflicts = result.conflicts,
                        backupData = result.backupData
                    )
                }
            }
        }
    }

    /**
     * Sync with cloud (smart sync)
     */
    fun syncWithCloud() {
        viewModelScope.launch {
            _uiState.value = BackupUiState.Syncing
            _progress.value = 0f

            val result = cloudBackup.syncWithCloud { progress ->
                _progress.value = progress
            }

            when (result) {
                is SyncResult.Success -> {
                    _uiState.value = BackupUiState.SyncSuccess(
                        uploaded = result.uploaded,
                        downloaded = result.downloaded,
                        conflictsResolved = result.conflictsResolved
                    )
                    loadCloudBackupState()
                }
                is SyncResult.Failure -> {
                    _uiState.value = BackupUiState.Error(result.error)
                }
                is SyncResult.Conflict -> {
                    _uiState.value = BackupUiState.Error("Sync conflicts detected. Please resolve manually.")
                }
            }
        }
    }

    /**
     * Enable automatic cloud backup
     */
    fun enableAutoBackup(intervalHours: Long = 24, wifiOnly: Boolean = true) {
        cloudBackup.enableAutoBackup(intervalHours, wifiOnly)
        _autoBackupEnabled.value = true
    }

    /**
     * Disable automatic cloud backup
     */
    fun disableAutoBackup() {
        cloudBackup.disableAutoBackup()
        _autoBackupEnabled.value = false
    }

    /**
     * Delete old backups
     */
    fun cleanupOldBackups(keepCount: Int = 5) {
        viewModelScope.launch {
            exporter.cleanupOldBackups(keepCount)
            loadAvailableBackups()
            _uiState.value = BackupUiState.Idle
        }
    }

    /**
     * Load available local backups
     */
    fun loadAvailableBackups() {
        viewModelScope.launch {
            _availableBackups.value = exporter.getAvailableBackups()
        }
    }

    /**
     * Load cloud backup state
     */
    private fun loadCloudBackupState() {
        viewModelScope.launch {
            _autoBackupEnabled.value = cloudBackup.isAutoBackupEnabled()
            cloudBackup.lastSyncTime.collect { time ->
                _lastSyncTime.value = time
            }
        }
    }

    /**
     * Observe cloud sync state
     */
    private fun observeCloudSyncState() {
        viewModelScope.launch {
            cloudBackup.syncState.collect { state ->
                _syncState.value = state
            }
        }
    }

    /**
     * Check if WiFi is available
     */
    fun isWiFiAvailable(): Boolean {
        return cloudBackup.isWiFiAvailable()
    }

    /**
     * Reset UI state to idle
     */
    fun resetState() {
        _uiState.value = BackupUiState.Idle
        _progress.value = 0f
    }
}

/**
 * UI State for Backup operations
 */
sealed class BackupUiState {
    object Idle : BackupUiState()
    object Exporting : BackupUiState()
    object Importing : BackupUiState()
    object UploadingToCloud : BackupUiState()
    object DownloadingFromCloud : BackupUiState()
    object Syncing : BackupUiState()

    data class ExportSuccess(
        val filePath: String,
        val wordCount: Int,
        val sizeBytes: Long
    ) : BackupUiState()

    data class ImportSuccess(
        val wordsRestored: Int,
        val statisticsRestored: Int,
        val conflictsResolved: Int
    ) : BackupUiState()

    data class CloudBackupSuccess(
        val cloudFileId: String,
        val sizeBytes: Long
    ) : BackupUiState()

    data class SyncSuccess(
        val uploaded: Boolean,
        val downloaded: Boolean,
        val conflictsResolved: Int
    ) : BackupUiState()

    data class ConflictDetected(
        val conflicts: List<DataConflict>,
        val backupData: BackupData
    ) : BackupUiState()

    data class Error(val message: String) : BackupUiState()
}
