package com.gultekinahmetabdullah.trainvoc.offline

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow

/**
 * Composable that shows offline status banner
 * Displays when device is offline with pending sync count
 */
@Composable
fun OfflineIndicator(
    isOnline: Boolean,
    pendingCount: Int,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !isOnline,
        enter = slideInVertically(),
        exit = slideOutVertically(),
        modifier = modifier
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = "Offline",
                    tint = MaterialTheme.colorScheme.secondary
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Offline Mode",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (pendingCount > 0) {
                        Text(
                            text = "$pendingCount changes will sync when online",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    } else {
                        Text(
                            text = "Changes will sync when online",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                if (pendingCount > 0) {
                    Badge {
                        Text(pendingCount.toString())
                    }
                }
            }
        }
    }
}

/**
 * Composable for downloading offline data
 * Shows progress and controls for offline content download
 */
@Composable
fun OfflineDataDownloadCard(
    downloadProgress: DownloadProgress,
    storageUsage: StorageUsage?,
    onDownloadClick: () -> Unit,
    onCancelClick: () -> Unit,
    onClearCacheClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Offline Content",
                        style = MaterialTheme.typography.titleMedium
                    )

                    storageUsage?.let { usage ->
                        Text(
                            text = "Using ${String.format("%.1f", usage.totalMB)} MB",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Divider()

            // Download Progress
            when (downloadProgress) {
                is DownloadProgress.Idle -> {
                    Text(
                        text = "Download audio and images for offline use. Works best on WiFi.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onDownloadClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Download All")
                        }

                        if (storageUsage != null && storageUsage.totalBytes > 0) {
                            OutlinedButton(onClick = onClearCacheClick) {
                                Text("Clear Cache")
                            }
                        }
                    }
                }

                is DownloadProgress.InProgress -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = downloadProgress.message,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${downloadProgress.progress}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        LinearProgressIndicator(
                            progress = downloadProgress.progress / 100f,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedButton(
                            onClick = onCancelClick,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancel Download")
                        }
                    }
                }

                is DownloadProgress.Completed -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Downloaded ${downloadProgress.itemsDownloaded} items",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is DownloadProgress.Failed -> {
                    Text(
                        text = "Download failed: ${downloadProgress.error}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )

                    Button(
                        onClick = onDownloadClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Retry Download")
                    }
                }

                is DownloadProgress.Cancelled -> {
                    Text(
                        text = "Download cancelled",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Sync status indicator with manual sync button
 */
@Composable
fun SyncStatusIndicator(
    isOnline: Boolean,
    pendingCount: Int,
    onManualSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isOnline) Icons.Default.CloudQueue else Icons.Default.CloudOff,
                contentDescription = null,
                tint = if (isOnline) MaterialTheme.colorScheme.primary
                      else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isOnline) "Connected" else "Offline",
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = when {
                        !isOnline && pendingCount > 0 -> "$pendingCount changes pending"
                        !isOnline -> "Changes will sync when online"
                        pendingCount > 0 -> "Syncing $pendingCount changes..."
                        else -> "All changes synced"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isOnline && pendingCount > 0) {
                IconButton(onClick = onManualSyncClick) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Sync now"
                    )
                }
            }
        }
    }
}
