package com.gultekinahmetabdullah.trainvoc.ui.screen.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.cloud.DriveBackup
import com.gultekinahmetabdullah.trainvoc.sync.ConflictStrategy
import com.gultekinahmetabdullah.trainvoc.viewmodel.CloudAuthState
import com.gultekinahmetabdullah.trainvoc.viewmodel.CloudBackupViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Cloud Backup Settings Screen
 *
 * Features:
 * - Google Drive sign-in/out
 * - Manual backup upload
 * - Browse backup history
 * - Restore from backup
 * - Delete backups
 * - Enable/disable auto-backup
 *
 * This screen allows users to manage their cloud backups and ensure their
 * vocabulary data is safely backed up to Google Drive.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudBackupScreen(
    viewModel: CloudBackupViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val backups by viewModel.backups.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val autoBackupEnabled by viewModel.autoBackupEnabled.collectAsState()
    val message by viewModel.message.collectAsState()

    // Sign-in launcher
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleSignInResult(result.data)
    }

    // Show snackbar for messages
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cloud Backup") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (authState is CloudAuthState.SignedIn) {
                        IconButton(onClick = { viewModel.refreshBackups() }) {
                            Icon(Icons.Default.Refresh, "Refresh")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = authState) {
                is CloudAuthState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is CloudAuthState.SignedOut -> {
                    SignedOutContent(
                        onSignIn = {
                            val intent = viewModel.getSignInIntent()
                            signInLauncher.launch(intent)
                        }
                    )
                }
                is CloudAuthState.SignedIn -> {
                    SignedInContent(
                        email = state.email,
                        displayName = state.displayName,
                        backups = backups,
                        isLoading = isLoading,
                        autoBackupEnabled = autoBackupEnabled,
                        onSignOut = { viewModel.signOut() },
                        onBackupNow = { viewModel.uploadBackup() },
                        onRestoreBackup = { fileId ->
                            viewModel.restoreBackup(fileId, ConflictStrategy.MERGE_PREFER_REMOTE)
                        },
                        onDeleteBackup = { fileId ->
                            viewModel.deleteBackup(fileId)
                        },
                        onAutoBackupToggle = { enabled ->
                            viewModel.setAutoBackup(enabled)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Content shown when user is not signed in
 */
@Composable
private fun SignedOutContent(
    onSignIn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CloudOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Cloud Backup",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Safely backup your vocabulary data to Google Drive",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Features:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                FeatureItem("✓ Automatic daily backups")
                FeatureItem("✓ Cross-device sync")
                FeatureItem("✓ Encrypted and secure")
                FeatureItem("✓ Easy restore")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSignIn,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Login, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign in with Google")
        }
    }
}

/**
 * Feature item for sign-out screen
 */
@Composable
private fun FeatureItem(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

/**
 * Content shown when user is signed in
 */
@Composable
private fun SignedInContent(
    email: String,
    displayName: String,
    backups: List<DriveBackup>,
    isLoading: Boolean,
    autoBackupEnabled: Boolean,
    onSignOut: () -> Unit,
    onBackupNow: () -> Unit,
    onRestoreBackup: (String) -> Unit,
    onDeleteBackup: (String) -> Unit,
    onAutoBackupToggle: (Boolean) -> Unit
) {
    var showSignOutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Account info card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(onClick = { showSignOutDialog = true }) {
                    Text("Sign Out")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Auto-backup toggle
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Auto Backup",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Daily automatic backup (WiFi only)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = autoBackupEnabled,
                    onCheckedChange = onAutoBackupToggle
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Manual backup button
        Button(
            onClick = onBackupNow,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Backup Now")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Backup history header
        Text(
            text = "Backup History",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Backup list
        if (isLoading && backups.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (backups.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudQueue,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No backups yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(backups) { backup ->
                    BackupListItem(
                        backup = backup,
                        onRestore = { onRestoreBackup(backup.fileId) },
                        onDelete = { onDeleteBackup(backup.fileId) }
                    )
                }
            }
        }
    }

    // Sign out confirmation dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out from Google Drive? Auto-backup will be disabled.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        onSignOut()
                    }
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Individual backup list item
 */
@Composable
private fun BackupListItem(
    backup: DriveBackup,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatBackupDate(backup.createdTime),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatBackupSize(backup.sizeBytes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (backup.description.isNotBlank()) {
                    Text(
                        text = backup.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = { showRestoreDialog = true }) {
                    Icon(Icons.Default.CloudDownload, "Restore")
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, "Delete")
                }
            }
        }
    }

    // Restore confirmation dialog
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Restore Backup?") },
            text = { Text("This will merge the backup data with your current data. Conflicts will be resolved by preferring the backup data.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRestoreDialog = false
                        onRestore()
                    }
                ) {
                    Text("Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Backup?") },
            text = { Text("This action cannot be undone. The backup will be permanently deleted from Google Drive.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Format backup timestamp to human-readable date
 */
private fun formatBackupDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * Format file size in bytes to human-readable format
 */
private fun formatBackupSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "${bytes / (1024 * 1024)} MB"
    }
}
