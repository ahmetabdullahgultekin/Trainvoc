package com.gultekinahmetabdullah.trainvoc.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Cloud Backup Manager
 *
 * Manages cloud backup operations with Google Drive or other cloud providers.
 *
 * Features:
 * - Automatic cloud sync on data changes
 * - Background upload/download with WorkManager
 * - Network state monitoring (WiFi-only option)
 * - Conflict resolution for multi-device sync
 * - Scheduled automatic backups
 * - Manual backup/restore
 *
 * Setup:
 * ```kotlin
 * // In Application.onCreate()
 * val cloudBackup = CloudBackupManager(context, database)
 * cloudBackup.initialize()
 * cloudBackup.enableAutoBackup(intervalHours = 24)
 * ```
 *
 * Usage:
 * ```kotlin
 * // Manual backup
 * val result = cloudBackup.backupToCloud()
 * when (result) {
 *     is CloudBackupResult.Success -> println("Backed up to cloud")
 *     is CloudBackupResult.Failure -> println("Backup failed: ${result.error}")
 * }
 *
 * // Manual restore
 * val restoreResult = cloudBackup.restoreFromCloud()
 * ```
 */
class CloudBackupManager(
    private val context: Context,
    private val database: AppDatabase
) {
    private val exporter = DataExporter(context, database)
    private val importer = DataImporter(context, database)

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: Flow<SyncState> = _syncState.asStateFlow()

    private val _lastSyncTime = MutableStateFlow<Long?>(null)
    val lastSyncTime: Flow<Long?> = _lastSyncTime.asStateFlow()

    companion object {
        private const val BACKUP_WORK_TAG = "cloud_backup_work"
        private const val SYNC_WORK_TAG = "cloud_sync_work"
        private const val PREFS_NAME = "cloud_backup_prefs"
        private const val KEY_LAST_SYNC = "last_sync_time"
        private const val KEY_AUTO_BACKUP_ENABLED = "auto_backup_enabled"
        private const val KEY_WIFI_ONLY = "wifi_only"
    }

    /**
     * Initialize cloud backup manager
     * Sets up WorkManager and loads preferences
     */
    fun initialize() {
        // Load last sync time
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _lastSyncTime.value = prefs.getLong(KEY_LAST_SYNC, 0).takeIf { it > 0 }
    }

    /**
     * Enable automatic cloud backup
     *
     * @param intervalHours Backup interval in hours (default: 24)
     * @param wifiOnly Only backup on WiFi connection (default: true)
     */
    fun enableAutoBackup(intervalHours: Long = 24, wifiOnly: Boolean = true) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (wifiOnly) NetworkType.UNMETERED else NetworkType.CONNECTED
            )
            .setRequiresBatteryNotLow(true)
            .build()

        val backupRequest = PeriodicWorkRequestBuilder<CloudBackupWorker>(
            intervalHours,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag(BACKUP_WORK_TAG)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            BACKUP_WORK_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            backupRequest
        )

        // Save preference
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_AUTO_BACKUP_ENABLED, true)
            .putBoolean(KEY_WIFI_ONLY, wifiOnly)
            .apply()
    }

    /**
     * Disable automatic cloud backup
     */
    fun disableAutoBackup() {
        WorkManager.getInstance(context).cancelAllWorkByTag(BACKUP_WORK_TAG)

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_AUTO_BACKUP_ENABLED, false)
            .apply()
    }

    /**
     * Check if automatic backup is enabled
     */
    fun isAutoBackupEnabled(): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_AUTO_BACKUP_ENABLED, false)
    }

    /**
     * Manually backup to cloud
     * Exports data and uploads to cloud storage
     *
     * @param onProgress Progress callback (0.0 to 1.0)
     * @return CloudBackupResult with status
     */
    suspend fun backupToCloud(
        onProgress: ((Float) -> Unit)? = null
    ): CloudBackupResult = withContext(Dispatchers.IO) {
        try {
            _syncState.value = SyncState.Uploading

            // Check network connectivity
            if (!isNetworkAvailable()) {
                _syncState.value = SyncState.Error("No network connection")
                return@withContext CloudBackupResult.Failure("No network connection")
            }

            onProgress?.invoke(0.1f)

            // Export to local JSON file
            val exportResult = exporter.exportToJson()
            if (exportResult !is BackupResult.Success) {
                _syncState.value = SyncState.Error("Export failed")
                return@withContext CloudBackupResult.Failure("Failed to create backup")
            }

            onProgress?.invoke(0.3f)

            // Upload to cloud (placeholder - implement with actual cloud provider)
            val uploadResult = uploadToCloudProvider(
                filePath = exportResult.filePath,
                onProgress = { progress ->
                    onProgress?.invoke(0.3f + progress * 0.6f)
                }
            )

            onProgress?.invoke(0.9f)

            if (uploadResult.isSuccess) {
                // Update last sync time
                val currentTime = System.currentTimeMillis()
                _lastSyncTime.value = currentTime
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putLong(KEY_LAST_SYNC, currentTime)
                    .apply()

                _syncState.value = SyncState.Synced
                onProgress?.invoke(1.0f)

                CloudBackupResult.Success(
                    cloudFileId = uploadResult.getOrNull() ?: "",
                    sizeBytes = exportResult.sizeBytes
                )
            } else {
                _syncState.value = SyncState.Error("Upload failed")
                CloudBackupResult.Failure("Failed to upload to cloud: ${uploadResult.exceptionOrNull()?.message}")
            }
        } catch (e: Exception) {
            _syncState.value = SyncState.Error(e.message ?: "Unknown error")
            CloudBackupResult.Failure("Backup failed: ${e.message}", e)
        }
    }

    /**
     * Manually restore from cloud
     * Downloads latest backup and imports data
     *
     * @param conflictStrategy Strategy for handling conflicts
     * @param onProgress Progress callback (0.0 to 1.0)
     * @return RestoreResult with status
     */
    suspend fun restoreFromCloud(
        conflictStrategy: ConflictStrategy = ConflictStrategy.MERGE_SMART,
        onProgress: ((Float) -> Unit)? = null
    ): RestoreResult = withContext(Dispatchers.IO) {
        try {
            _syncState.value = SyncState.Downloading

            // Check network connectivity
            if (!isNetworkAvailable()) {
                _syncState.value = SyncState.Error("No network connection")
                return@withContext RestoreResult.Failure("No network connection")
            }

            onProgress?.invoke(0.1f)

            // Download from cloud (placeholder - implement with actual cloud provider)
            val downloadResult = downloadFromCloudProvider { progress ->
                onProgress?.invoke(0.1f + progress * 0.4f)
            }

            if (downloadResult.isFailure) {
                _syncState.value = SyncState.Error("Download failed")
                return@withContext RestoreResult.Failure("Failed to download from cloud")
            }

            val localFilePath = downloadResult.getOrNull()
            if (localFilePath == null) {
                _syncState.value = SyncState.Error("Download failed")
                return@withContext RestoreResult.Failure("Failed to download from cloud")
            }

            onProgress?.invoke(0.5f)

            // Import data
            val importResult = importer.importFromJson(
                filePath = localFilePath,
                conflictStrategy = conflictStrategy,
                onProgress = { progress ->
                    onProgress?.invoke(0.5f + progress * 0.4f)
                }
            )

            onProgress?.invoke(0.9f)

            // Update last sync time
            val currentTime = System.currentTimeMillis()
            _lastSyncTime.value = currentTime
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putLong(KEY_LAST_SYNC, currentTime)
                .apply()

            _syncState.value = SyncState.Synced
            onProgress?.invoke(1.0f)

            importResult
        } catch (e: Exception) {
            _syncState.value = SyncState.Error(e.message ?: "Unknown error")
            RestoreResult.Failure("Restore failed: ${e.message}", e)
        }
    }

    /**
     * Sync with cloud (smart sync)
     * Compares local and cloud versions, merges changes
     *
     * @return SyncResult with status
     */
    suspend fun syncWithCloud(
        onProgress: ((Float) -> Unit)? = null
    ): SyncResult = withContext(Dispatchers.IO) {
        try {
            _syncState.value = SyncState.Syncing

            // Check network connectivity
            if (!isNetworkAvailable()) {
                _syncState.value = SyncState.Error("No network connection")
                return@withContext SyncResult.Failure("No network connection")
            }

            onProgress?.invoke(0.1f)

            // Get cloud backup metadata
            val cloudMetadata = getCloudBackupMetadata()
            if (cloudMetadata == null) {
                // No cloud backup exists, upload local data
                return@withContext when (val result = backupToCloud(onProgress)) {
                    is CloudBackupResult.Success -> SyncResult.Success(
                        uploaded = true,
                        downloaded = false,
                        conflictsResolved = 0
                    )

                    is CloudBackupResult.Failure -> SyncResult.Failure(result.error)
                }
            }

            onProgress?.invoke(0.2f)

            // Compare timestamps to determine sync direction
            val localBackupTime = getLastLocalBackupTime()

            when {
                cloudMetadata.timestamp > localBackupTime -> {
                    // Cloud is newer, download and merge
                    val restoreResult = restoreFromCloud(
                        conflictStrategy = ConflictStrategy.MERGE_SMART,
                        onProgress = { progress ->
                            onProgress?.invoke(0.2f + progress * 0.7f)
                        }
                    )

                    when (restoreResult) {
                        is RestoreResult.Success -> {
                            _syncState.value = SyncState.Synced
                            SyncResult.Success(
                                uploaded = false,
                                downloaded = true,
                                conflictsResolved = restoreResult.conflictsResolved
                            )
                        }

                        is RestoreResult.Failure -> {
                            _syncState.value = SyncState.Error(restoreResult.error)
                            SyncResult.Failure(restoreResult.error)
                        }

                        is RestoreResult.PartialSuccess -> {
                            _syncState.value = SyncState.Synced
                            SyncResult.Success(
                                uploaded = false,
                                downloaded = true,
                                conflictsResolved = 0
                            )
                        }

                        is RestoreResult.Conflict -> {
                            _syncState.value = SyncState.ConflictDetected
                            SyncResult.Conflict(restoreResult.conflicts)
                        }
                    }
                }

                localBackupTime > cloudMetadata.timestamp -> {
                    // Local is newer, upload
                    val backupResult = backupToCloud { progress ->
                        onProgress?.invoke(0.2f + progress * 0.7f)
                    }

                    when (backupResult) {
                        is CloudBackupResult.Success -> {
                            _syncState.value = SyncState.Synced
                            SyncResult.Success(
                                uploaded = true,
                                downloaded = false,
                                conflictsResolved = 0
                            )
                        }

                        is CloudBackupResult.Failure -> {
                            _syncState.value = SyncState.Error(backupResult.error)
                            SyncResult.Failure(backupResult.error)
                        }
                    }
                }

                else -> {
                    // Already in sync
                    _syncState.value = SyncState.Synced
                    onProgress?.invoke(1.0f)
                    SyncResult.Success(
                        uploaded = false,
                        downloaded = false,
                        conflictsResolved = 0
                    )
                }
            }
        } catch (e: Exception) {
            _syncState.value = SyncState.Error(e.message ?: "Unknown error")
            SyncResult.Failure("Sync failed: ${e.message}")
        }
    }

    /**
     * Check if network is available
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Check if WiFi is available
     */
    fun isWiFiAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    /**
     * Upload to cloud provider (Coming Soon)
     *
     * Cloud backup is planned for a future release. Currently returns a user-friendly
     * "coming soon" message. Local backup works and is recommended until cloud is available.
     *
     * TODO: Implement with Google Drive API when ready for production
     */
    private suspend fun uploadToCloudProvider(
        filePath: String,
        onProgress: ((Float) -> Unit)? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        Result.failure(CloudBackupComingSoonException(
            "Cloud backup coming soon! Your data is safely stored locally."
        ))
    }

    /**
     * Exception indicating cloud backup feature is coming soon
     */
    class CloudBackupComingSoonException(message: String) : Exception(message)

    /**
     * Download from cloud provider (Coming Soon)
     *
     * Cloud restore is planned for a future release. Currently returns a user-friendly
     * "coming soon" message. Local restore works and is recommended until cloud is available.
     *
     * TODO: Implement with Google Drive API when ready for production
     */
    private suspend fun downloadFromCloudProvider(
        onProgress: ((Float) -> Unit)? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        Result.failure(CloudBackupComingSoonException(
            "Cloud restore coming soon! Use local backup for now."
        ))
    }

    /**
     * Get cloud backup metadata (placeholder)
     */
    private suspend fun getCloudBackupMetadata(): CloudBackupMetadata? {
        // Placeholder: In real implementation, fetch from cloud provider
        return null
    }

    /**
     * Get last local backup time
     */
    private fun getLastLocalBackupTime(): Long {
        val backupDir = exporter.getAvailableBackups()
        return backupDir.maxOfOrNull { it.timestamp } ?: 0
    }
}

/**
 * Cloud backup metadata
 */
data class CloudBackupMetadata(
    val fileId: String,
    val fileName: String,
    val timestamp: Long,
    val sizeBytes: Long,
    val checksum: String
)

/**
 * Cloud backup result
 */
sealed class CloudBackupResult {
    data class Success(
        val cloudFileId: String,
        val sizeBytes: Long
    ) : CloudBackupResult()

    data class Failure(
        val error: String,
        val exception: Throwable? = null
    ) : CloudBackupResult()
}

/**
 * Sync result
 */
sealed class SyncResult {
    data class Success(
        val uploaded: Boolean,
        val downloaded: Boolean,
        val conflictsResolved: Int
    ) : SyncResult()

    data class Failure(
        val error: String
    ) : SyncResult()

    data class Conflict(
        val conflicts: List<DataConflict>
    ) : SyncResult()
}

/**
 * Sync state
 */
sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    object Uploading : SyncState()
    object Downloading : SyncState()
    object Synced : SyncState()
    object ConflictDetected : SyncState()
    data class Error(val message: String) : SyncState()
}

/**
 * Cloud Backup Worker for automatic backups
 */
class CloudBackupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Get database instance (using Hilt or manual injection)
            // val database = AppDatabase.getInstance(applicationContext)
            // val cloudBackup = CloudBackupManager(applicationContext, database)

            // val result = cloudBackup.backupToCloud()

            // when (result) {
            //     is CloudBackupResult.Success -> Result.success()
            //     is CloudBackupResult.Failure -> Result.retry()
            // }

            // Placeholder for now
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

/**
 * Cloud Sync Worker for automatic sync
 */
class CloudSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Similar to CloudBackupWorker but calls syncWithCloud()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
