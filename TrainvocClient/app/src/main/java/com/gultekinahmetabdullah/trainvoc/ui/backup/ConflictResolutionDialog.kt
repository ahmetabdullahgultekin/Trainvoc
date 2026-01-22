package com.gultekinahmetabdullah.trainvoc.ui.backup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gultekinahmetabdullah.trainvoc.sync.ConflictStrategy
import com.gultekinahmetabdullah.trainvoc.sync.DataConflict

/**
 * Dialog for resolving data conflicts
 *
 * Shows:
 * - Number and types of conflicts
 * - Detailed conflict list
 * - Resolution strategy options
 * - Preview of what each strategy does
 */
@Composable
fun ConflictResolutionDialog(
    conflicts: List<DataConflict>,
    onStrategySelected: (ConflictStrategy) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedStrategy by remember { mutableStateOf(ConflictStrategy.MERGE_SMART) }
    var showDetails by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Conflict resolution option",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(32.dp)
                        )
                        Column {
                            Text(
                                "Conflicts Detected",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                "${conflicts.size} conflict${if (conflicts.size > 1) "s" else ""} found",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                // Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Explanation
                    item {
                        Text(
                            "The backup contains data that conflicts with your local data. " +
                                    "Choose how to resolve these conflicts:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Strategy options
                    item {
                        Text(
                            "Resolution Strategy",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        StrategyOption(
                            strategy = ConflictStrategy.MERGE_SMART,
                            selected = selectedStrategy == ConflictStrategy.MERGE_SMART,
                            onSelect = { selectedStrategy = ConflictStrategy.MERGE_SMART },
                            title = "Smart Merge (Recommended)",
                            description = "Automatically chooses the best version based on learning progress, " +
                                    "completeness, and timestamps.",
                            icon = Icons.Default.AutoAwesome
                        )
                    }

                    item {
                        StrategyOption(
                            strategy = ConflictStrategy.MERGE_PREFER_REMOTE,
                            selected = selectedStrategy == ConflictStrategy.MERGE_PREFER_REMOTE,
                            onSelect = { selectedStrategy = ConflictStrategy.MERGE_PREFER_REMOTE },
                            title = "Prefer Backup Data",
                            description = "Use backup version for conflicts, keep local-only items.",
                            icon = Icons.Default.CloudDownload
                        )
                    }

                    item {
                        StrategyOption(
                            strategy = ConflictStrategy.MERGE_PREFER_LOCAL,
                            selected = selectedStrategy == ConflictStrategy.MERGE_PREFER_LOCAL,
                            onSelect = { selectedStrategy = ConflictStrategy.MERGE_PREFER_LOCAL },
                            title = "Prefer Local Data",
                            description = "Keep local version for conflicts, add backup-only items.",
                            icon = Icons.Default.PhoneAndroid
                        )
                    }

                    item {
                        StrategyOption(
                            strategy = ConflictStrategy.REPLACE_ALL,
                            selected = selectedStrategy == ConflictStrategy.REPLACE_ALL,
                            onSelect = { selectedStrategy = ConflictStrategy.REPLACE_ALL },
                            title = "Replace All",
                            description = "Replace all local data with backup data (destructive).",
                            icon = Icons.Default.DeleteSweep
                        )
                    }

                    // Conflict details toggle
                    item {
                        TextButton(
                            onClick = { showDetails = !showDetails },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                if (showDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Conflict resolution option"
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (showDetails) "Hide Conflict Details" else "Show Conflict Details"
                            )
                        }
                    }

                    // Conflict details
                    if (showDetails) {
                        items(conflicts, key = { conflict ->
                            when(conflict) {
                                is DataConflict.WordConflict -> "word_${conflict.word}"
                                is DataConflict.StatisticConflict -> "stat_${conflict.word}"
                            }
                        }) { conflict ->
                            ConflictDetailItem(conflict)
                        }
                    }
                }

                // Actions
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onStrategySelected(selectedStrategy) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Check, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Resolve")
                    }
                }
            }
        }
    }
}

@Composable
fun StrategyOption(
    strategy: ConflictStrategy,
    selected: Boolean,
    onSelect: () -> Unit,
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onSelect,
                role = Role.RadioButton
            ),
        colors = if (selected) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = "Conflict resolution option",
                tint = if (selected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.size(24.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            RadioButton(
                selected = selected,
                onClick = null
            )
        }
    }
}

@Composable
fun ConflictDetailItem(conflict: DataConflict) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (conflict) {
                is DataConflict.WordConflict -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Conflict resolution option",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Word: ${conflict.word}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider()

                    // Local version
                    ConflictVersionInfo(
                        label = "Local",
                        word = conflict.localVersion.word,
                        meaning = conflict.localVersion.meaning,
                        level = conflict.localVersion.level ?: "Unknown",
                        isLocal = true
                    )

                    // Remote version
                    ConflictVersionInfo(
                        label = "Backup",
                        word = conflict.remoteVersion.word,
                        meaning = conflict.remoteVersion.meaning,
                        level = conflict.remoteVersion.level ?: "Unknown",
                        isLocal = false
                    )
                }

                is DataConflict.StatisticConflict -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = "Conflict resolution option",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Statistics: ${conflict.word}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider()

                    // Local stats
                    StatisticVersionInfo(
                        label = "Local",
                        learned = conflict.localVersion.learned,
                        correct = conflict.localVersion.correctCount,
                        wrong = conflict.localVersion.wrongCount,
                        isLocal = true
                    )

                    // Remote stats
                    StatisticVersionInfo(
                        label = "Backup",
                        learned = conflict.remoteVersion.learned,
                        correct = conflict.remoteVersion.correctCount,
                        wrong = conflict.remoteVersion.wrongCount,
                        isLocal = false
                    )
                }
            }
        }
    }
}

@Composable
fun ConflictVersionInfo(
    label: String,
    word: String,
    meaning: String,
    level: String,
    isLocal: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (isLocal) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        )
        Text(
            "Meaning: $meaning",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            "Level: $level",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun StatisticVersionInfo(
    label: String,
    learned: Boolean,
    correct: Int,
    wrong: Int,
    isLocal: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (isLocal) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Learned: ${if (learned) "Yes" else "No"}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Correct: $correct",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Wrong: $wrong",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
