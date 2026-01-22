package com.gultekinahmetabdullah.trainvoc.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.ui.theme.TrainvocTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings

/**
 * Reusable card wrapper for settings sections.
 * Provides consistent styling with icon, title, and content area.
 *
 * Features:
 * - Circular icon background with primary container color
 * - Bold title with primary color
 * - Horizontal divider for visual separation
 * - Flexible content area
 * - Consistent elevation and corner radius
 *
 * @param icon Leading icon for the section
 * @param title Section title text
 * @param modifier Optional modifier for the card
 * @param content Content to display in the section
 *
 * Example usage:
 * ```
 * SettingSectionCard(
 *     icon = Icons.Default.Palette,
 *     title = "Appearance"
 * ) {
 *     Text("Theme settings go here")
 *     Switch(...)
 * }
 * ```
 */
@Composable
fun SettingSectionCard(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Spacing.medium)) {
            // Header with icon and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = Spacing.small)
            ) {
                // Circular icon background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Divider for visual separation
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                modifier = Modifier.padding(bottom = Spacing.small)
            )

            // Content area
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingSectionCardPreview() {
    TrainvocTheme {
        SettingSectionCard(
            icon = Icons.Default.Settings,
            title = "Example Section"
        ) {
            Text(
                text = "This is example content inside the section card.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.padding(vertical = 4.dp))
            Text(
                text = "Multiple items can be placed here.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingSectionCardDarkPreview() {
    TrainvocTheme(darkTheme = true) {
        SettingSectionCard(
            icon = Icons.Default.Settings,
            title = "Dark Mode Example"
        ) {
            Text(
                text = "This shows how the card looks in dark mode.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
