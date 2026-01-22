package com.gultekinahmetabdullah.trainvoc.ui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

/**
 * Clickable card for navigation to other screens.
 * Includes icon, title, optional subtitle, and chevron indicator.
 *
 * Features:
 * - Leading icon with primary color
 * - Bold title text
 * - Optional subtitle with muted color
 * - Trailing chevron (→) for navigation indication
 * - Ripple effect on click
 * - Consistent elevation
 *
 * @param icon Leading icon for the navigation item
 * @param title Main title text
 * @param subtitle Optional subtitle/description text
 * @param onClick Callback when the card is clicked
 * @param modifier Optional modifier for the card
 *
 * Example usage:
 * ```
 * NavigationCard(
 *     icon = Icons.Default.Notifications,
 *     title = "Notification Settings",
 *     subtitle = "Manage your notifications",
 *     onClick = { navController.navigate(Route.NOTIFICATION_SETTINGS) }
 * )
 * ```
 */
@Composable
fun NavigationCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Icon + Text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Right side: Chevron indicator
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NavigationCardPreview() {
    TrainvocTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NavigationCard(
                icon = Icons.Default.Settings,
                title = "Settings",
                subtitle = "Manage app preferences",
                onClick = {}
            )
            NavigationCard(
                icon = Icons.Default.Settings,
                title = "Without Subtitle",
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NavigationCardDarkPreview() {
    TrainvocTheme(darkTheme = true) {
        NavigationCard(
            icon = Icons.Default.Settings,
            title = "Dark Mode Example",
            subtitle = "This is how it looks in dark mode",
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
