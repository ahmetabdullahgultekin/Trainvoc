package com.gultekinahmetabdullah.trainvoc.ui.backup

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.sync.SyncState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Sync Status Card
 * Shows current sync status, last sync time, and WiFi availability
 */
@Composable
fun SyncStatusCard(
    syncState: SyncState,
    autoBackupEnabled: Boolean,
    isWiFiAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = when (syncState) {
            is SyncState.Synced -> CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )

            is SyncState.Error -> CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )

            is SyncState.ConflictDetected -> CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )

            else -> CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sync status row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SyncStateIcon(syncState)
                    Column {
                        Text(
                            getSyncStateTitle(syncState),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            getSyncStateDescription(syncState),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            HorizontalDivider()

            // Status indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatusIndicatorChip(
                    icon = if (autoBackupEnabled) Icons.Default.CloudDone else Icons.Default.CloudOff,
                    label = if (autoBackupEnabled) "Auto backup ON" else "Auto backup OFF",
                    color = if (autoBackupEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                StatusIndicatorChip(
                    icon = if (isWiFiAvailable) Icons.Default.Wifi else Icons.Default.WifiOff,
                    label = if (isWiFiAvailable) "WiFi" else "No WiFi",
                    color = if (isWiFiAvailable) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
        }
    }
}

/**
 * Sync State Icon with animation
 */
@Composable
fun SyncStateIcon(syncState: SyncState) {
    when (syncState) {
        is SyncState.Idle -> {
            Icon(
                Icons.Default.CloudQueue,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
        }

        is SyncState.Syncing,
        is SyncState.Uploading,
        is SyncState.Downloading -> {
            // Animated rotating sync icon
            val infiniteTransition = rememberInfiniteTransition(label = "sync")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotation"
            )

            Icon(
                Icons.Default.Sync,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(32.dp)
                    .rotate(rotation)
            )
        }

        is SyncState.Synced -> {
            Icon(
                Icons.Default.CloudDone,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(32.dp)
            )
        }

        is SyncState.Error -> {
            Icon(
                Icons.Default.CloudOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )
        }

        is SyncState.ConflictDetected -> {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * Status Indicator Chip
 */
@Composable
fun StatusIndicatorChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.height(28.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

/**
 * Compact Sync Status Indicator (for Settings)
 */
@Composable
fun SyncStatusIndicator(
    syncState: SyncState,
    lastSyncTime: Long?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }

    Surface(
        modifier = modifier,
        onClick = onClick ?: {},
        enabled = onClick != null,
        shape = MaterialTheme.shapes.medium,
        color = when (syncState) {
            is SyncState.Synced -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
            is SyncState.Error -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
            else -> MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SyncStateIcon(syncState)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    getSyncStateTitle(syncState),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                lastSyncTime?.let {
                    Text(
                        "Last: ${dateFormat.format(Date(it))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (onClick != null) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Get sync state title
 */
fun getSyncStateTitle(syncState: SyncState): String {
    return when (syncState) {
        is SyncState.Idle -> "Not Synced"
        is SyncState.Syncing -> "Syncing..."
        is SyncState.Uploading -> "Uploading..."
        is SyncState.Downloading -> "Downloading..."
        is SyncState.Synced -> "Synced"
        is SyncState.Error -> "Sync Error"
        is SyncState.ConflictDetected -> "Conflicts Detected"
    }
}

/**
 * Get sync state description
 */
fun getSyncStateDescription(syncState: SyncState): String {
    return when (syncState) {
        is SyncState.Idle -> "Tap Sync to backup to cloud"
        is SyncState.Syncing -> "Synchronizing with cloud"
        is SyncState.Uploading -> "Uploading to cloud"
        is SyncState.Downloading -> "Downloading from cloud"
        is SyncState.Synced -> "All data synced with cloud"
        is SyncState.Error -> syncState.message
        is SyncState.ConflictDetected -> "Manual resolution required"
    }
}

/**
 * Animated Sync Progress Indicator
 */
@Composable
fun SyncProgressIndicator(
    progress: Float,
    syncState: SyncState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                getSyncStateTitle(syncState),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Last Sync Time Display
 */
@Composable
fun LastSyncTime(
    timestamp: Long?,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Schedule,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            if (timestamp != null) {
                "Last synced: ${dateFormat.format(Date(timestamp))}"
            } else {
                "Never synced"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
