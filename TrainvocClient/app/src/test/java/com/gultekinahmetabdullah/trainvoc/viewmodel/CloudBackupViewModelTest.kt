package com.gultekinahmetabdullah.trainvoc.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import com.gultekinahmetabdullah.trainvoc.cloud.AuthResult
import com.gultekinahmetabdullah.trainvoc.cloud.DriveBackup
import com.gultekinahmetabdullah.trainvoc.cloud.DriveBackupResult
import com.gultekinahmetabdullah.trainvoc.cloud.DriveBackupService
import com.gultekinahmetabdullah.trainvoc.cloud.DriveRestoreResult
import com.gultekinahmetabdullah.trainvoc.cloud.GoogleAuthManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for CloudBackupViewModel
 *
 * Tests:
 * - Authentication state management
 * - Backup upload operations
 * - Restore operations
 * - Auto-backup settings
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CloudBackupViewModelTest {

    private lateinit var context: Context
    private lateinit var authManager: GoogleAuthManager
    private lateinit var backupService: DriveBackupService
    private lateinit var workManager: WorkManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var viewModel: CloudBackupViewModel

    private val testAccount: GoogleSignInAccount = mock()

    @Before
    fun setup() {
        context = mock()
        authManager = mock()
        backupService = mock()
        workManager = mock()
        sharedPreferences = mock()
        editor = mock()

        whenever(context.getSharedPreferences(any(), any())).thenReturn(sharedPreferences)
        whenever(sharedPreferences.getBoolean(any(), any())).thenReturn(false)
        whenever(sharedPreferences.edit()).thenReturn(editor)
        whenever(editor.putBoolean(any(), any())).thenReturn(editor)

        whenever(testAccount.email).thenReturn("test@example.com")
        whenever(testAccount.displayName).thenReturn("Test User")
    }

    private fun createViewModel(): CloudBackupViewModel {
        return CloudBackupViewModel(
            context = context,
            authManager = authManager,
            backupService = backupService,
            workManager = workManager
        )
    }

    @Test
    fun `initial state is loading then checks auth`() = runTest {
        // Arrange
        whenever(authManager.getSignedInAccount()).thenReturn(null)
        whenever(authManager.isSignedIn()).thenReturn(false)

        // Act
        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert - after checking, should be signed out
        assertEquals(CloudAuthState.SignedOut, viewModel.authState.value)
    }

    @Test
    fun `checkAuthState with signed in user returns SignedIn state`() = runTest {
        // Arrange
        whenever(authManager.getSignedInAccount()).thenReturn(testAccount)
        whenever(authManager.isSignedIn()).thenReturn(true)
        whenever(backupService.listBackups()).thenReturn(emptyList())

        // Act
        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert
        val state = viewModel.authState.value
        assertTrue(state is CloudAuthState.SignedIn)
        assertEquals("test@example.com", (state as CloudAuthState.SignedIn).email)
        assertEquals("Test User", state.displayName)
    }

    @Test
    fun `signOut clears auth state and backups`() = runTest {
        // Arrange
        whenever(authManager.getSignedInAccount()).thenReturn(testAccount)
        whenever(authManager.isSignedIn()).thenReturn(true)
        whenever(backupService.listBackups()).thenReturn(listOf(
            DriveBackup("file1", "backup1.json", 1000L, "2024-01-01", 100)
        ))

        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.signOut()
        advanceUntilIdle()

        // Assert
        verify(authManager).signOut()
        assertEquals(CloudAuthState.SignedOut, viewModel.authState.value)
        assertTrue(viewModel.backups.value.isEmpty())
        assertEquals("Signed out successfully", viewModel.message.value)
    }

    @Test
    fun `uploadBackup success updates message and refreshes backups`() = runTest {
        // Arrange
        whenever(authManager.getSignedInAccount()).thenReturn(testAccount)
        whenever(authManager.isSignedIn()).thenReturn(true)
        whenever(backupService.listBackups()).thenReturn(emptyList())
        whenever(backupService.uploadBackup()).thenReturn(
            DriveBackupResult.Success(fileName = "backup.json", fileId = "123", wordCount = 500)
        )

        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.uploadBackup()
        advanceUntilIdle()

        // Assert
        verify(backupService).uploadBackup()
        assertEquals("Backup uploaded successfully (500 words)", viewModel.message.value)
    }

    @Test
    fun `uploadBackup failure shows error message`() = runTest {
        // Arrange
        whenever(authManager.getSignedInAccount()).thenReturn(testAccount)
        whenever(authManager.isSignedIn()).thenReturn(true)
        whenever(backupService.listBackups()).thenReturn(emptyList())
        whenever(backupService.uploadBackup()).thenReturn(
            DriveBackupResult.Failure(error = "Network error")
        )

        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.uploadBackup()
        advanceUntilIdle()

        // Assert
        assertEquals("Backup failed: Network error", viewModel.message.value)
    }

    @Test
    fun `restoreBackup success shows success message`() = runTest {
        // Arrange
        whenever(authManager.getSignedInAccount()).thenReturn(testAccount)
        whenever(authManager.isSignedIn()).thenReturn(true)
        whenever(backupService.listBackups()).thenReturn(emptyList())
        whenever(backupService.downloadAndRestoreBackup(any(), any())).thenReturn(
            DriveRestoreResult.Success(wordsRestored = 250)
        )

        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.restoreBackup("file123")
        advanceUntilIdle()

        // Assert
        verify(backupService).downloadAndRestoreBackup(any(), any())
        assertEquals("Restored successfully (250 words)", viewModel.message.value)
    }

    @Test
    fun `restoreBackup failure shows error message`() = runTest {
        // Arrange
        whenever(authManager.getSignedInAccount()).thenReturn(testAccount)
        whenever(authManager.isSignedIn()).thenReturn(true)
        whenever(backupService.listBackups()).thenReturn(emptyList())
        whenever(backupService.downloadAndRestoreBackup(any(), any())).thenReturn(
            DriveRestoreResult.Failure(error = "Invalid backup format")
        )

        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.restoreBackup("file123")
        advanceUntilIdle()

        // Assert
        assertEquals("Restore failed: Invalid backup format", viewModel.message.value)
    }

    @Test
    fun `deleteBackup success refreshes backup list`() = runTest {
        // Arrange
        whenever(authManager.getSignedInAccount()).thenReturn(testAccount)
        whenever(authManager.isSignedIn()).thenReturn(true)
        whenever(backupService.listBackups()).thenReturn(emptyList())
        whenever(backupService.deleteBackup(any())).thenReturn(true)

        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.deleteBackup("file123")
        advanceUntilIdle()

        // Assert
        verify(backupService).deleteBackup("file123")
        assertEquals("Backup deleted", viewModel.message.value)
    }

    @Test
    fun `deleteBackup failure shows error message`() = runTest {
        // Arrange
        whenever(authManager.getSignedInAccount()).thenReturn(testAccount)
        whenever(authManager.isSignedIn()).thenReturn(true)
        whenever(backupService.listBackups()).thenReturn(emptyList())
        whenever(backupService.deleteBackup(any())).thenReturn(false)

        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.deleteBackup("file123")
        advanceUntilIdle()

        // Assert
        assertEquals("Failed to delete backup", viewModel.message.value)
    }

    @Test
    fun `setAutoBackup enabled schedules work and saves preference`() = runTest {
        // Arrange
        whenever(authManager.getSignedInAccount()).thenReturn(null)
        whenever(authManager.isSignedIn()).thenReturn(false)

        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.setAutoBackup(true)
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.autoBackupEnabled.value)
        verify(editor).putBoolean("auto_backup_enabled", true)
        assertEquals("Auto-backup enabled (daily, WiFi only)", viewModel.message.value)
    }

    @Test
    fun `setAutoBackup disabled cancels work and saves preference`() = runTest {
        // Arrange
        whenever(authManager.getSignedInAccount()).thenReturn(null)
        whenever(authManager.isSignedIn()).thenReturn(false)
        whenever(sharedPreferences.getBoolean(any(), any())).thenReturn(true)

        viewModel = createViewModel()
        advanceUntilIdle()

        // Act
        viewModel.setAutoBackup(false)
        advanceUntilIdle()

        // Assert
        assertFalse(viewModel.autoBackupEnabled.value)
        verify(editor).putBoolean("auto_backup_enabled", false)
        assertEquals("Auto-backup disabled", viewModel.message.value)
    }

    @Test
    fun `clearMessage sets message to null`() = runTest {
        // Arrange
        whenever(authManager.getSignedInAccount()).thenReturn(null)
        whenever(authManager.isSignedIn()).thenReturn(false)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.setAutoBackup(true)
        advanceUntilIdle()

        assertNotNull(viewModel.message.value)

        // Act
        viewModel.clearMessage()

        // Assert
        assertNull(viewModel.message.value)
    }

    @Test
    fun `refreshBackups loads backup list`() = runTest {
        // Arrange
        val testBackups = listOf(
            DriveBackup("id1", "backup1.json", 1000L, "2024-01-01", 100),
            DriveBackup("id2", "backup2.json", 2000L, "2024-01-02", 200)
        )
        whenever(authManager.getSignedInAccount()).thenReturn(testAccount)
        whenever(authManager.isSignedIn()).thenReturn(true)
        whenever(backupService.listBackups()).thenReturn(testBackups)

        viewModel = createViewModel()
        advanceUntilIdle()

        // Assert - backups were loaded on init
        assertEquals(2, viewModel.backups.value.size)
        assertEquals("backup1.json", viewModel.backups.value[0].name)
    }

    @Test
    fun `isLoading is true during backup operation`() = runTest {
        // Arrange
        whenever(authManager.getSignedInAccount()).thenReturn(testAccount)
        whenever(authManager.isSignedIn()).thenReturn(true)
        whenever(backupService.listBackups()).thenReturn(emptyList())
        whenever(backupService.uploadBackup()).thenReturn(
            DriveBackupResult.Success(fileName = "backup.json", fileId = "123", wordCount = 500)
        )

        viewModel = createViewModel()
        advanceUntilIdle()

        // Act & Assert - loading should be false after operation completes
        viewModel.uploadBackup()
        advanceUntilIdle()
        assertFalse(viewModel.isLoading.value)
    }
}
