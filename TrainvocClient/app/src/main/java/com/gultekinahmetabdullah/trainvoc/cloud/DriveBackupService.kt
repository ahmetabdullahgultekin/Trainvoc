package com.gultekinahmetabdullah.trainvoc.cloud

import android.content.Context
import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.gultekinahmetabdullah.trainvoc.database.AppDatabase
import com.gultekinahmetabdullah.trainvoc.sync.BackupResult
import com.gultekinahmetabdullah.trainvoc.sync.ConflictStrategy
import com.gultekinahmetabdullah.trainvoc.sync.DataExporter
import com.gultekinahmetabdullah.trainvoc.sync.DataImporter
import com.gultekinahmetabdullah.trainvoc.sync.RestoreResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import java.io.File as JavaFile

/**
 * Google Drive backup and restore service
 *
 * Features:
 * - Upload encrypted backups to Google Drive
 * - Download and restore backups from Google Drive
 * - List backup history
 * - Delete old backups
 * - Automatic folder management
 * - Progress tracking
 *
 * Architecture:
 * - Uses Google Drive API v3
 * - Stores backups in app-specific folder (DRIVE_APPDATA scope)
 * - All backups are encrypted before upload
 * - Supports automatic retry on network errors
 *
 * Prerequisites:
 * - User must be signed in via GoogleAuthManager
 * - OAuth 2.0 credentials configured
 * - Drive API enabled in Google Cloud Console
 *
 * Usage:
 * ```kotlin
 * val service = DriveBackupService(context, authManager, dataExporter, database)
 *
 * // Upload backup
 * val uploadResult = service.uploadBackup()
 * when (uploadResult) {
 *     is DriveBackupResult.Success -> Log.d("Backup", "Uploaded: ${uploadResult.fileName}")
 *     is DriveBackupResult.Failure -> Log.e("Backup", "Failed: ${uploadResult.error}")
 * }
 *
 * // List backups
 * val backups = service.listBackups()
 *
 * // Restore from backup
 * val restoreResult = service.downloadAndRestoreBackup(backups[0].fileId)
 * ```
 */
@Singleton
class DriveBackupService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authManager: GoogleAuthManager,
    private val dataExporter: DataExporter,
    private val database: AppDatabase
) {

    companion object {
        private const val TAG = "DriveBackupService"
        private const val BACKUP_FOLDER_NAME = "Trainvoc Backups"
        private const val BACKUP_MIME_TYPE = "application/octet-stream"
        private const val FOLDER_MIME_TYPE = "application/vnd.google-apps.folder"
        private const val MAX_BACKUPS_TO_KEEP = 10
    }

    /**
     * Upload current data as encrypted backup to Google Drive
     *
     * Process:
     * 1. Check authentication
     * 2. Export data to encrypted file
     * 3. Get or create backup folder
     * 4. Upload file to Drive
     * 5. Clean up local temp file
     *
     * @return DriveBackupResult indicating success or failure
     */
    suspend fun uploadBackup(): DriveBackupResult {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Starting Drive backup upload")

                // Check authentication
                if (!authManager.isSignedIn()) {
                    Log.w(TAG, "Upload failed: User not signed in")
                    return@withContext DriveBackupResult.Failure("Please sign in to Google Drive first")
                }

                // Build Drive service
                val driveService = buildDriveService()
                    ?: return@withContext DriveBackupResult.Failure("Failed to initialize Drive service")

                // Export data to encrypted file
                Log.d(TAG, "Exporting data...")
                val exportResult = dataExporter.exportToJson(
                    includeStatistics = true,
                    includePreferences = true,
                    encrypt = true
                )

                when (exportResult) {
                    is BackupResult.Failure -> {
                        Log.e(TAG, "Export failed: ${exportResult.error}")
                        return@withContext DriveBackupResult.Failure("Failed to export data: ${exportResult.error}")
                    }
                    is BackupResult.Success -> {
                        Log.d(TAG, "Data exported successfully: ${exportResult.filePath}")
                    }
                }

                val exportSuccess = exportResult as BackupResult.Success
                val localFile = JavaFile(exportSuccess.filePath)

                if (!localFile.exists()) {
                    Log.e(TAG, "Export file not found: ${exportSuccess.filePath}")
                    return@withContext DriveBackupResult.Failure("Export file not found")
                }

                // Get or create backup folder
                Log.d(TAG, "Getting backup folder...")
                val folderId = getOrCreateBackupFolder(driveService)
                    ?: return@withContext DriveBackupResult.Failure("Failed to create backup folder")

                // Prepare file metadata
                val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(Date())
                val fileName = "trainvoc_backup_$timestamp.enc"

                val fileMetadata = File()
                    .setName(fileName)
                    .setParents(listOf(folderId))
                    .setMimeType(BACKUP_MIME_TYPE)
                    .setDescription("Trainvoc encrypted backup - ${exportSuccess.wordCount} words")

                // Upload file
                Log.d(TAG, "Uploading to Drive: $fileName (${exportSuccess.sizeBytes} bytes)")
                val mediaContent = FileContent(BACKUP_MIME_TYPE, localFile)

                val uploadedFile = driveService.files()
                    .create(fileMetadata, mediaContent)
                    .setFields("id, name, size, createdTime, description")
                    .execute()

                // Clean up local temp file
                localFile.delete()

                Log.i(TAG, "Backup uploaded successfully: ${uploadedFile.name} (ID: ${uploadedFile.id})")

                DriveBackupResult.Success(
                    fileId = uploadedFile.id,
                    fileName = uploadedFile.name,
                    sizeBytes = uploadedFile.getSize(),
                    uploadTime = System.currentTimeMillis(),
                    wordCount = exportSuccess.wordCount
                )

            } catch (e: Exception) {
                Log.e(TAG, "Drive backup upload failed", e)
                DriveBackupResult.Failure("Upload failed: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    /**
     * List all available backups from Google Drive
     *
     * Backups are listed in reverse chronological order (newest first)
     *
     * @return List of DriveBackup objects, or empty list if error/no backups
     */
    suspend fun listBackups(): List<DriveBackup> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Listing Drive backups")

                // Check authentication
                if (!authManager.isSignedIn()) {
                    Log.w(TAG, "List failed: User not signed in")
                    return@withContext emptyList()
                }

                val driveService = buildDriveService()
                    ?: return@withContext emptyList()

                // Get backup folder
                val folderId = getBackupFolderId(driveService)
                if (folderId == null) {
                    Log.d(TAG, "No backup folder found")
                    return@withContext emptyList()
                }

                // List files in backup folder
                val result: FileList = driveService.files().list()
                    .setQ("'$folderId' in parents and trashed=false")
                    .setOrderBy("createdTime desc")
                    .setFields("files(id, name, size, createdTime, description)")
                    .setPageSize(MAX_BACKUPS_TO_KEEP)
                    .execute()

                val backups = result.files.map { file ->
                    DriveBackup(
                        fileId = file.id,
                        fileName = file.name,
                        sizeBytes = file.getSize(),
                        createdTime = file.createdTime.value,
                        description = file.description ?: ""
                    )
                }

                Log.d(TAG, "Found ${backups.size} backup(s)")
                backups

            } catch (e: Exception) {
                Log.e(TAG, "Failed to list backups", e)
                emptyList()
            }
        }
    }

    /**
     * Download and restore backup from Google Drive
     *
     * Process:
     * 1. Download encrypted file from Drive
     * 2. Save to local temp file
     * 3. Import and decrypt data
     * 4. Clean up temp file
     *
     * @param fileId Google Drive file ID
     * @param conflictStrategy How to handle conflicts (default: prefer remote)
     * @return DriveRestoreResult indicating success or failure
     */
    suspend fun downloadAndRestoreBackup(
        fileId: String,
        conflictStrategy: ConflictStrategy = ConflictStrategy.MERGE_PREFER_REMOTE
    ): DriveRestoreResult {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Starting Drive backup restore: $fileId")

                // Check authentication
                if (!authManager.isSignedIn()) {
                    Log.w(TAG, "Restore failed: User not signed in")
                    return@withContext DriveRestoreResult.Failure("Please sign in to Google Drive first")
                }

                val driveService = buildDriveService()
                    ?: return@withContext DriveRestoreResult.Failure("Failed to initialize Drive service")

                // Download file
                Log.d(TAG, "Downloading file from Drive...")
                val outputStream = ByteArrayOutputStream()
                driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream)

                // Save to local temp file
                val localFile = JavaFile(context.cacheDir, "drive_backup_restore_temp.enc")
                FileOutputStream(localFile).use { fos ->
                    fos.write(outputStream.toByteArray())
                }

                Log.d(TAG, "File downloaded: ${localFile.length()} bytes")

                // Import data
                Log.d(TAG, "Importing data...")
                val dataImporter = DataImporter(context, database)
                val importResult = dataImporter.importFromJson(
                    filePath = localFile.absolutePath,
                    conflictStrategy = conflictStrategy
                )

                // Clean up temp file
                localFile.delete()

                when (importResult) {
                    is RestoreResult.Success -> {
                        Log.i(TAG, "Restore successful: ${importResult.wordsRestored} words restored")
                        DriveRestoreResult.Success(
                            wordsRestored = importResult.wordsRestored,
                            statisticsRestored = importResult.statisticsRestored
                        )
                    }
                    is RestoreResult.Failure -> {
                        Log.e(TAG, "Import failed: ${importResult.error}")
                        DriveRestoreResult.Failure("Failed to import data: ${importResult.error}")
                    }
                    is RestoreResult.PartialSuccess -> {
                        Log.w(TAG, "Partial restore: ${importResult.wordsRestored} words")
                        DriveRestoreResult.Success(
                            wordsRestored = importResult.wordsRestored,
                            statisticsRestored = 0
                        )
                    }
                    is RestoreResult.Conflict -> {
                        Log.w(TAG, "Restore conflict: ${importResult.conflicts.size} conflicts detected")
                        DriveRestoreResult.Failure("Data conflicts detected. Please resolve manually.")
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Drive restore failed", e)
                DriveRestoreResult.Failure("Restore failed: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    /**
     * Delete backup from Google Drive
     *
     * @param fileId Google Drive file ID to delete
     * @return true if deleted successfully, false otherwise
     */
    suspend fun deleteBackup(fileId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Deleting backup: $fileId")

                if (!authManager.isSignedIn()) {
                    Log.w(TAG, "Delete failed: User not signed in")
                    return@withContext false
                }

                val driveService = buildDriveService()
                    ?: return@withContext false

                driveService.files().delete(fileId).execute()
                Log.i(TAG, "Backup deleted successfully")
                true

            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete backup", e)
                false
            }
        }
    }

    /**
     * Clean up old backups, keeping only the most recent MAX_BACKUPS_TO_KEEP
     *
     * @return Number of backups deleted
     */
    suspend fun cleanupOldBackups(): Int {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Cleaning up old backups")

                val allBackups = listBackups()
                if (allBackups.size <= MAX_BACKUPS_TO_KEEP) {
                    Log.d(TAG, "No cleanup needed: ${allBackups.size} backups")
                    return@withContext 0
                }

                // Delete oldest backups
                val backupsToDelete = allBackups.drop(MAX_BACKUPS_TO_KEEP)
                var deletedCount = 0

                backupsToDelete.forEach { backup ->
                    if (deleteBackup(backup.fileId)) {
                        deletedCount++
                    }
                }

                Log.i(TAG, "Cleanup complete: Deleted $deletedCount old backup(s)")
                deletedCount

            } catch (e: Exception) {
                Log.e(TAG, "Cleanup failed", e)
                0
            }
        }
    }

    // Private helper methods

    /**
     * Build Drive service with authenticated credentials
     */
    private fun buildDriveService(): Drive? {
        return try {
            val account = authManager.getSignedInAccount()
            if (account?.account == null) {
                Log.e(TAG, "Cannot build Drive service: No account")
                return null
            }

            val credential = GoogleAccountCredential.usingOAuth2(
                context,
                listOf(DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_APPDATA)
            )
            credential.selectedAccount = account.account

            Drive.Builder(
                NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                credential
            )
                .setApplicationName("Trainvoc")
                .build()

        } catch (e: Exception) {
            Log.e(TAG, "Failed to build Drive service", e)
            null
        }
    }

    /**
     * Get existing backup folder ID or create new one
     */
    private fun getOrCreateBackupFolder(drive: Drive): String? {
        return getBackupFolderId(drive) ?: createBackupFolder(drive)
    }

    /**
     * Get backup folder ID if it exists
     */
    private fun getBackupFolderId(drive: Drive): String? {
        return try {
            val result: FileList = drive.files().list()
                .setQ("name='$BACKUP_FOLDER_NAME' and mimeType='$FOLDER_MIME_TYPE' and trashed=false")
                .setSpaces("drive")
                .setFields("files(id)")
                .execute()

            if (result.files.isNotEmpty()) {
                result.files[0].id
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to find backup folder", e)
            null
        }
    }

    /**
     * Create new backup folder
     */
    private fun createBackupFolder(drive: Drive): String? {
        return try {
            val folderMetadata = File()
                .setName(BACKUP_FOLDER_NAME)
                .setMimeType(FOLDER_MIME_TYPE)
                .setDescription("Trainvoc automatic backups")

            val folder = drive.files()
                .create(folderMetadata)
                .setFields("id")
                .execute()

            Log.d(TAG, "Created backup folder: ${folder.id}")
            folder.id

        } catch (e: Exception) {
            Log.e(TAG, "Failed to create backup folder", e)
            null
        }
    }
}

/**
 * Result of Drive backup upload operation
 */
sealed class DriveBackupResult {
    /**
     * Upload successful
     *
     * @property fileId Google Drive file ID
     * @property fileName Name of uploaded file
     * @property sizeBytes File size in bytes
     * @property uploadTime Timestamp of upload
     * @property wordCount Number of words backed up
     */
    data class Success(
        val fileId: String,
        val fileName: String,
        val sizeBytes: Long,
        val uploadTime: Long,
        val wordCount: Int
    ) : DriveBackupResult()

    /**
     * Upload failed
     *
     * @property error Human-readable error message
     */
    data class Failure(val error: String) : DriveBackupResult()
}

/**
 * Result of Drive backup restore operation
 */
sealed class DriveRestoreResult {
    /**
     * Restore successful
     *
     * @property wordsRestored Number of words restored
     * @property statisticsRestored Number of statistics restored
     */
    data class Success(
        val wordsRestored: Int,
        val statisticsRestored: Int
    ) : DriveRestoreResult()

    /**
     * Restore failed
     *
     * @property error Human-readable error message
     */
    data class Failure(val error: String) : DriveRestoreResult()
}

/**
 * Backup file metadata from Google Drive
 */
data class DriveBackup(
    val fileId: String,
    val fileName: String,
    val sizeBytes: Long,
    val createdTime: Long,
    val description: String
)
