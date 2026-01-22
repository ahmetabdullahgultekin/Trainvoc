package com.gultekinahmetabdullah.trainvoc.ui.components.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.model.UpdateHighlight
import com.gultekinahmetabdullah.trainvoc.model.UpdateNotes
import com.gultekinahmetabdullah.trainvoc.model.UpdateType
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.ui.theme.TrainvocTheme

/**
 * Card component for displaying app update notes
 *
 * Features:
 * - Expandable/collapsible design
 * - Shows version, release date, and highlights
 * - Lists upcoming features
 * - Links to full changelog
 * - Dismissible with callback
 *
 * @param updateNotes The update notes data to display
 * @param onViewChangelog Callback when "View Full Changelog" is clicked
 * @param onDismiss Callback when dismissed
 * @param modifier Optional modifier
 */
@Composable
fun UpdateNotesCard(
    updateNotes: UpdateNotes,
    onViewChangelog: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Chevron rotation animation
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "chevronRotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Spacing.medium)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.NewReleases,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "What's New in ${updateNotes.currentVersion}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Released ${updateNotes.releaseDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }

                Row {
                    // Expand/Collapse button
                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.rotate(rotationAngle)
                        )
                    }

                    // Dismiss button
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Expanded content
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                    Spacer(Modifier.height(12.dp))

                    // Highlights
                    updateNotes.highlights.forEach { highlight ->
                        UpdateHighlightItem(highlight)
                        Spacer(Modifier.height(8.dp))
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // Coming Soon section
                    Text(
                        text = "Coming Soon",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(8.dp))

                    updateNotes.upcomingFeatures.forEach { feature ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = feature,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = onViewChangelog) {
                            Text(
                                text = "View Full Changelog",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = "Dismiss",
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual update highlight item
 */
@Composable
private fun UpdateHighlightItem(highlight: UpdateHighlight) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = highlight.getIcon(),
            contentDescription = null,
            tint = when (highlight.type) {
                UpdateType.NEW -> MaterialTheme.colorScheme.tertiary
                UpdateType.IMPROVED -> MaterialTheme.colorScheme.primary
                UpdateType.FIXED -> MaterialTheme.colorScheme.secondary
            },
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val badge = when (highlight.type) {
                    UpdateType.NEW -> "NEW"
                    UpdateType.IMPROVED -> "IMPROVED"
                    UpdateType.FIXED -> "FIXED"
                }
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = when (highlight.type) {
                        UpdateType.NEW -> MaterialTheme.colorScheme.tertiary
                        UpdateType.IMPROVED -> MaterialTheme.colorScheme.primary
                        UpdateType.FIXED -> MaterialTheme.colorScheme.secondary
                    },
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = highlight.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                text = highlight.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UpdateNotesCardPreview() {
    TrainvocTheme {
        UpdateNotesCard(
            updateNotes = UpdateNotes(
                currentVersion = "1.0.0",
                versionCode = 1,
                releaseDate = "January 22, 2026",
                highlights = listOf(
                    UpdateHighlight(
                        type = UpdateType.NEW,
                        title = "Modern Material 3 Design",
                        description = "Complete UI overhaul with beautiful components"
                    ),
                    UpdateHighlight(
                        type = UpdateType.IMPROVED,
                        title = "Enhanced Settings",
                        description = "Better organization and visual hierarchy"
                    ),
                    UpdateHighlight(
                        type = UpdateType.FIXED,
                        title = "Dark Mode Polish",
                        description = "Fixed hardcoded colors"
                    )
                ),
                upcomingFeatures = listOf(
                    "Backend sync",
                    "Cloud backup",
                    "TTS integration"
                )
            ),
            onViewChangelog = {},
            onDismiss = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
