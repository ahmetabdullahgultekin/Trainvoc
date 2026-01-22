package com.gultekinahmetabdullah.trainvoc.ui.screen.other

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gultekinahmetabdullah.trainvoc.data.UpdateNotesManager
import com.gultekinahmetabdullah.trainvoc.model.UpdateHighlight
import com.gultekinahmetabdullah.trainvoc.model.UpdateNotes
import com.gultekinahmetabdullah.trainvoc.model.UpdateType
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing

/**
 * ChangelogScreen - Full version history display
 *
 * Features:
 * - Displays all app versions chronologically
 * - Expandable/collapsible version cards
 * - Shows version highlights and upcoming features
 * - Material 3 design matching UpdateNotesCard
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangelogScreen(navController: NavController) {
    val context = LocalContext.current
    val updateNotesManager = remember { UpdateNotesManager.getInstance(context) }

    // In a real app, this would load all versions from a JSON file or API
    // For now, we'll display the current version
    val allVersions = remember {
        listOf(
            updateNotesManager.getUpdateNotes() ?: getDefaultChangelogEntry()
        )
    }

    Scaffold(
        topAppBar = {
            TopAppBar(
                title = { Text("Changelog") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            item {
                Spacer(modifier = Modifier.height(Spacing.small))
            }

            item {
                Text(
                    text = "Version History",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                Text(
                    text = "See what's new and improved in each version of Trainvoc",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.small))
            }

            // Display all versions
            items(items = allVersions, key = { it.versionCode }) { version ->
                VersionCard(updateNotes = version)
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.medium))
            }
        }
    }
}

/**
 * Individual version card with expandable content
 */
@Composable
private fun VersionCard(updateNotes: UpdateNotes) {
    var isExpanded by remember { mutableStateOf(false) }

    // Chevron rotation animation
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "chevronRotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(Spacing.medium)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Version ${updateNotes.currentVersion}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Released ${updateNotes.releaseDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.rotate(rotationAngle)
                    )
                }
            }

            // Expanded content
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                    Spacer(Modifier.height(12.dp))

                    // Highlights
                    Text(
                        text = "What's New",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))

                    updateNotes.highlights.forEach { highlight ->
                        VersionHighlightItem(highlight)
                        Spacer(Modifier.height(8.dp))
                    }

                    // Upcoming features if any
                    if (updateNotes.upcomingFeatures.isNotEmpty()) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        Text(
                            text = "Coming Soon",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual highlight item for changelog
 */
@Composable
private fun VersionHighlightItem(highlight: UpdateHighlight) {
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = highlight.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * Default changelog entry when JSON is not available
 */
private fun getDefaultChangelogEntry(): UpdateNotes {
    return UpdateNotes(
        currentVersion = "1.0.0",
        versionCode = 1,
        releaseDate = "January 22, 2026",
        highlights = listOf(
            UpdateHighlight(
                type = UpdateType.NEW,
                title = "Modern Material 3 Design",
                description = "Complete UI overhaul with beautiful Material 3 components, cards, and smooth animations throughout the app"
            ),
            UpdateHighlight(
                type = UpdateType.IMPROVED,
                title = "Enhanced Settings Screen",
                description = "Redesigned with organized sections, icons, and better visual hierarchy for easier navigation"
            ),
            UpdateHighlight(
                type = UpdateType.IMPROVED,
                title = "Better Help & Support",
                description = "Interactive FAQ with expandable cards, circular icons, and improved contact options"
            ),
            UpdateHighlight(
                type = UpdateType.IMPROVED,
                title = "Beautiful About Screen",
                description = "Professional presentation with cards, proper theming, and enhanced social links"
            ),
            UpdateHighlight(
                type = UpdateType.IMPROVED,
                title = "100% WCAG 2.1 AA Compliance",
                description = "Full accessibility support with proper contrast ratios and screen reader compatibility"
            ),
            UpdateHighlight(
                type = UpdateType.FIXED,
                title = "Dark Mode Polish",
                description = "Fixed hardcoded colors for perfect dark mode support across all themes"
            )
        ),
        upcomingFeatures = listOf(
            "Backend sync across devices",
            "Cloud backup with Google Drive",
            "Text-to-Speech integration",
            "11 memory games restoration",
            "Dictionary API integration",
            "Social sharing features"
        )
    )
}
