package com.gultekinahmetabdullah.trainvoc.ui.backup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.sync.BackupData
import com.gultekinahmetabdullah.trainvoc.sync.BackupFileInfo
import com.gultekinahmetabdullah.trainvoc.sync.ConflictStrategy
import com.gultekinahmetabdullah.trainvoc.sync.DataConflict
import com.gultekinahmetabdullah.trainvoc.sync.SyncState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            uiState is BackupUiState.Error
        ) {
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
            items(availableBackups, key = { it.filePath }) { backup ->
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
        // HONEST DISCLAIMER - At the top, prominent
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Cloud Sync Not Available",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            "Full cloud backup is not yet implemented. Currently, only achievements sync via Google Play Games.\n\n" +
                                    "Use the Local tab to create manual backups (JSON/CSV) of your vocabulary data.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        // What Actually Works
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "What Works Now",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    Text(
                        "✓ Achievements sync via Google Play Games\n" +
                                "✓ Local backups (JSON/CSV export)\n" +
                                "✓ Manual data import/export\n" +
                                "✓ Local backup file management",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        // Coming Soon
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Coming in Future Updates",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        "• Full vocabulary cloud backup\n" +
                                "• Automatic sync across devices\n" +
                                "• Google Drive integration\n" +
                                "• Dropbox integration\n" +
                                "• Custom backend support",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Redirect to Local Backups
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Folder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Use Local Backups Instead",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Text(
                        "For now, please use the Local tab to:\n" +
                                "• Create backup files (JSON or CSV format)\n" +
                                "• Export to your device storage\n" +
                                "• Share via email, cloud services, etc.\n" +
                                "• Import from previous backups",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                    )

                    Text(
                        "💡 Tip: Regularly export your data to keep it safe!",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
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
