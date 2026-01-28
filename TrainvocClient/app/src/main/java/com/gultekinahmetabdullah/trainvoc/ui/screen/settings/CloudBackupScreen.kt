package com.gultekinahmetabdullah.trainvoc.ui.screen.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.cloud.DriveBackup
import com.gultekinahmetabdullah.trainvoc.ui.components.RollingCatLoaderWithText
import com.gultekinahmetabdullah.trainvoc.ui.components.LoaderSize
import com.gultekinahmetabdullah.trainvoc.ui.components.ButtonLoader
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
    val operationError by viewModel.operationError.collectAsState()

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
                title = { Text(stringResource(id = R.string.cloud_backup)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.back_to_previous_screen))
                    }
                },
                actions = {
                    if (authState is CloudAuthState.SignedIn) {
                        IconButton(onClick = { viewModel.refreshBackups() }) {
                            Icon(Icons.Default.Refresh, stringResource(id = R.string.refresh_backup_list))
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
                    RollingCatLoaderWithText(
                        message = stringResource(id = R.string.loading),
                        size = LoaderSize.medium,
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
                        operationError = operationError,
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
                        },
                        onRetry = { viewModel.retryOperation() }
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
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Responsive icon size: scales with screen but clamped
        val iconSize = (minOf(maxWidth, maxHeight) * 0.15f).coerceIn(64.dp, 96.dp)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = stringResource(id = R.string.content_desc_cloud_backup_disabled),
                modifier = Modifier.size(iconSize),
                tint = MaterialTheme.colorScheme.primary
            )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.cloud_backup),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.backup_to_google_drive),
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
                    text = stringResource(id = R.string.coming_soon_header),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                FeatureItem(stringResource(id = R.string.secure_cloud_backup))
                FeatureItem(stringResource(id = R.string.cross_device_sync))
                FeatureItem(stringResource(id = R.string.automatic_daily_backups))

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.currently_available),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                FeatureItem(stringResource(id = R.string.local_backup_available))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onSignIn,
            modifier = Modifier.fillMaxWidth(),
            enabled = false  // Disabled until cloud backup is implemented
        ) {
            Icon(Icons.AutoMirrored.Filled.Login, contentDescription = stringResource(id = R.string.content_desc_sign_in_google))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(id = R.string.sign_in_google_coming_soon))
        }
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
    operationError: String? = null,
    onSignOut: () -> Unit,
    onBackupNow: () -> Unit,
    onRestoreBackup: (String) -> Unit,
    onDeleteBackup: (String) -> Unit,
    onAutoBackupToggle: (Boolean) -> Unit,
    onRetry: () -> Unit = {}
) {
    var showSignOutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Show error state if there's an operation error
        if (operationError != null && !isLoading) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = operationError,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(id = R.string.content_desc_retry))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = R.string.retry))
                    }
                }
            }
        }

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
                    Text(stringResource(id = R.string.sign_out))
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
                        text = stringResource(id = R.string.auto_backup),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(id = R.string.auto_backup_desc),
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
                ButtonLoader(modifier = Modifier.size(20.dp))
            } else {
                Icon(Icons.Default.CloudUpload, contentDescription = stringResource(id = R.string.content_desc_upload_backup))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(id = R.string.backup_now))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Backup history header
        Text(
            text = stringResource(id = R.string.backup_history),
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
                RollingCatLoaderWithText(
                    message = stringResource(id = R.string.loading_backups),
                    size = LoaderSize.medium
                )
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
                        contentDescription = stringResource(id = R.string.content_desc_no_backups),
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.no_backups_yet),
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
                items(backups, key = { it.fileId }) { backup ->
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
            title = { Text(stringResource(id = R.string.sign_out_title)) },
            text = { Text(stringResource(id = R.string.sign_out_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        onSignOut()
                    }
                ) {
                    Text(stringResource(id = R.string.sign_out))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text(stringResource(id = R.string.cancel))
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
                    Icon(Icons.Default.CloudDownload, stringResource(id = R.string.restore))
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, stringResource(id = R.string.delete))
                }
            }
        }
    }

    // Restore confirmation dialog
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text(stringResource(id = R.string.restore_backup_title)) },
            text = { Text(stringResource(id = R.string.restore_backup_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRestoreDialog = false
                        onRestore()
                    }
                ) {
                    Text(stringResource(id = R.string.restore))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(id = R.string.delete_backup_title)) },
            text = { Text(stringResource(id = R.string.delete_backup_message)) },
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
                    Text(stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(id = R.string.cancel))
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
