package com.gultekinahmetabdullah.trainvoc.ui.backup

import androidx.compose.animation.*
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
import com.gultekinahmetabdullah.trainvoc.sync.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Backup & Restore Screen
 *
 * Features:
 * - Local backup/restore (JSON, CSV)
 * - Cloud backup/restore
 * - Automatic sync configuration
 * - Backup file management
 * - Conflict resolution
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    viewModel: BackupViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val availableBackups by viewModel.availableBackups.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    val autoBackupEnabled by viewModel.autoBackupEnabled.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showConflictDialog by remember { mutableStateOf(false) }
    var conflictData by remember { mutableStateOf<Pair<List<DataConflict>, BackupData>?>(null) }

    // Handle conflict detection
    LaunchedEffect(uiState) {
        if (uiState is BackupUiState.ConflictDetected) {
            val state = uiState as BackupUiState.ConflictDetected
            conflictData = Pair(state.conflicts, state.backupData)
            showConflictDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Backup & Restore") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Local") },
                    icon = { Icon(Icons.Default.Folder, "Local") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Cloud") },
                    icon = { Icon(Icons.Default.Cloud, "Cloud") }
                )
            }

            // Content
            when (selectedTab) {
                0 -> LocalBackupTab(
                    viewModel = viewModel,
                    uiState = uiState,
                    progress = progress,
                    availableBackups = availableBackups
                )
                1 -> CloudBackupTab(
                    viewModel = viewModel,
                    uiState = uiState,
                    progress = progress,
                    syncState = syncState,
                    autoBackupEnabled = autoBackupEnabled
                )
            }
        }

        // Conflict Resolution Dialog
        if (showConflictDialog && conflictData != null) {
            ConflictResolutionDialog(
                conflicts = conflictData!!.first,
                onStrategySelected = { strategy ->
                    viewModel.resolveConflicts(conflictData!!.second, strategy)
                    showConflictDialog = false
                },
                onDismiss = {
                    showConflictDialog = false
                    viewModel.resetState()
                }
            )
        }

        // Snackbar for results
        if (uiState is BackupUiState.ExportSuccess ||
            uiState is BackupUiState.ImportSuccess ||
            uiState is BackupUiState.Error) {
            LaunchedEffect(uiState) {
                kotlinx.coroutines.delay(3000)
                viewModel.resetState()
            }
        }
    }
}

@Composable
fun LocalBackupTab(
    viewModel: BackupViewModel,
    uiState: BackupUiState,
    progress: Float,
    availableBackups: List<BackupFileInfo>
) {
    var selectedBackup by remember { mutableStateOf<BackupFileInfo?>(null) }
    var showExportOptions by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Export Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Export Data",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        "Create a backup of your vocabulary data",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Export buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.exportToJson() },
                            modifier = Modifier.weight(1f),
                            enabled = uiState !is BackupUiState.Exporting
                        ) {
                            Icon(Icons.Default.Description, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("JSON")
                        }

                        OutlinedButton(
                            onClick = { viewModel.exportToCsv() },
                            modifier = Modifier.weight(1f),
                            enabled = uiState !is BackupUiState.Exporting
                        ) {
                            Icon(Icons.Default.TableChart, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("CSV")
                        }
                    }

                    // Progress indicator
                    if (uiState is BackupUiState.Exporting) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                "Exporting... ${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // Success message
                    if (uiState is BackupUiState.ExportSuccess) {
                        ResultMessage(
                            message = "Backup created successfully!\n${uiState.wordCount} words backed up",
                            isError = false
                        )
                    }
                }
            }
        }

        // Import Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Import Data",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        "Restore from a backup file",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Progress indicator
                    if (uiState is BackupUiState.Importing) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                "Importing... ${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // Success message
                    if (uiState is BackupUiState.ImportSuccess) {
                        ResultMessage(
                            message = "Data restored successfully!\n" +
                                    "${uiState.wordsRestored} words restored\n" +
                                    "${uiState.conflictsResolved} conflicts resolved",
                            isError = false
                        )
                    }
                }
            }
        }

        // Available Backups
        item {
            Text(
                "Available Backups (${availableBackups.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (availableBackups.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.FolderOff,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "No backups found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else {
            items(availableBackups) { backup ->
                BackupFileItem(
                    backup = backup,
                    selected = backup == selectedBackup,
                    onSelect = { selectedBackup = backup },
                    onRestore = {
                        viewModel.importFromBackup(
                            filePath = backup.filePath,
                            conflictStrategy = ConflictStrategy.MERGE_SMART
                        )
                    }
                )
            }
        }

        // Cleanup button
        if (availableBackups.size > 5) {
            item {
                OutlinedButton(
                    onClick = { viewModel.cleanupOldBackups() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DeleteSweep, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Clean Up Old Backups")
                }
            }
        }

        // Error message
        if (uiState is BackupUiState.Error) {
            item {
                ResultMessage(
                    message = uiState.message,
                    isError = true
                )
            }
        }
    }
}

@Composable
fun CloudBackupTab(
    viewModel: BackupViewModel,
    uiState: BackupUiState,
    progress: Float,
    syncState: SyncState,
    autoBackupEnabled: Boolean
) {
    val isWiFiAvailable = viewModel.isWiFiAvailable()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sync Status Card
        item {
            SyncStatusCard(
                syncState = syncState,
                autoBackupEnabled = autoBackupEnabled,
                isWiFiAvailable = isWiFiAvailable
            )
        }

        // Auto Backup Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Automatic Backup",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Backup daily at 3 AM (WiFi only)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = autoBackupEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    viewModel.enableAutoBackup(
                                        intervalHours = 24,
                                        wifiOnly = true
                                    )
                                } else {
                                    viewModel.disableAutoBackup()
                                }
                            }
                        )
                    }
                }
            }
        }

        // Cloud Actions
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Manual Actions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Sync button
                    Button(
                        onClick = { viewModel.syncWithCloud() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState !is BackupUiState.Syncing && isWiFiAvailable
                    ) {
                        Icon(Icons.Default.Sync, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Sync Now")
                    }

                    // Backup to cloud
                    OutlinedButton(
                        onClick = { viewModel.backupToCloud() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState !is BackupUiState.UploadingToCloud && isWiFiAvailable
                    ) {
                        Icon(Icons.Default.CloudUpload, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Upload to Cloud")
                    }

                    // Restore from cloud
                    OutlinedButton(
                        onClick = { viewModel.restoreFromCloud() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState !is BackupUiState.DownloadingFromCloud && isWiFiAvailable
                    ) {
                        Icon(Icons.Default.CloudDownload, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Download from Cloud")
                    }

                    // WiFi warning
                    if (!isWiFiAvailable) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                "WiFi not available. Cloud operations disabled.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        // Progress indicator
        if (uiState is BackupUiState.Syncing ||
            uiState is BackupUiState.UploadingToCloud ||
            uiState is BackupUiState.DownloadingFromCloud) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            when (uiState) {
                                is BackupUiState.Syncing -> "Syncing... ${(progress * 100).toInt()}%"
                                is BackupUiState.UploadingToCloud -> "Uploading... ${(progress * 100).toInt()}%"
                                is BackupUiState.DownloadingFromCloud -> "Downloading... ${(progress * 100).toInt()}%"
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Success messages
        if (uiState is BackupUiState.SyncSuccess) {
            item {
                ResultMessage(
                    message = "Sync completed successfully!\n" +
                            "Uploaded: ${if (uiState.uploaded) "Yes" else "No"}\n" +
                            "Downloaded: ${if (uiState.downloaded) "Yes" else "No"}",
                    isError = false
                )
            }
        }

        if (uiState is BackupUiState.CloudBackupSuccess) {
            item {
                ResultMessage(
                    message = "Backup uploaded to cloud successfully!",
                    isError = false
                )
            }
        }

        // Error message
        if (uiState is BackupUiState.Error) {
            item {
                ResultMessage(
                    message = uiState.message,
                    isError = true
                )
            }
        }

        // Info card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "Cloud Backup Note",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "Cloud backup integration is currently a placeholder. " +
                                    "Future versions will integrate with Google Drive, Dropbox, or custom backend.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BackupFileItem(
    backup: BackupFileInfo,
    selected: Boolean,
    onSelect: () -> Unit,
    onRestore: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (selected) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        backup.fileName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        dateFormat.format(Date(backup.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = onRestore,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Restore, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Restore")
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoChip(
                    icon = Icons.Default.Description,
                    label = "${backup.wordCount} words"
                )
                InfoChip(
                    icon = Icons.Default.Storage,
                    label = formatFileSize(backup.sizeBytes)
                )
            }
        }
    }
}

@Composable
fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ResultMessage(message: String, isError: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.tertiaryContainer
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                if (isError) Icons.Default.Error else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (isError) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onTertiaryContainer
                }
            )
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isError) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onTertiaryContainer
                }
            )
        }
    }
}

fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "${bytes / (1024 * 1024)} MB"
    }
}
