package com.gultekinahmetabdullah.trainvoc.ui.components.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.ui.theme.TrainvocTheme

/**
 * Generic information display card with icon and title.
 * Used for displaying informational content in a visually consistent manner.
 *
 * Features:
 * - Leading icon with customizable color
 * - Bold title text
 * - Flexible content area
 * - Customizable container and content colors
 * - Consistent elevation
 *
 * @param icon Leading icon for the card
 * @param title Card title text
 * @param modifier Optional modifier for the card
 * @param containerColor Background color of the card (defaults to primaryContainer)
 * @param contentColor Color for text and icon (defaults to onPrimaryContainer)
 * @param content Content to display in the card
 *
 * Example usage:
 * ```
 * InfoCard(
 *     icon = Icons.Default.Info,
 *     title = "About This App"
 * ) {
 *     Text("Version 1.0.0")
 *     Text("Your vocabulary training companion")
 * }
 * ```
 */
@Composable
fun InfoCard(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Spacing.medium)) {
            // Header with icon and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = Spacing.small)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )
            }

            // Content area
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InfoCardPreview() {
    TrainvocTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            InfoCard(
                icon = Icons.Default.Info,
                title = "Information"
            ) {
                Text(
                    text = "This is an example of informational content.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.padding(vertical = 4.dp))
                Text(
                    text = "Multiple lines of text can be displayed here.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InfoCardDarkPreview() {
    TrainvocTheme(darkTheme = true) {
        InfoCard(
            icon = Icons.Default.Info,
            title = "Dark Mode Example",
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "This shows how the InfoCard appears in dark mode.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InfoCardCustomColorsPreview() {
    TrainvocTheme {
        InfoCard(
            icon = Icons.Default.Info,
            title = "Custom Colors",
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "This card uses custom container and content colors.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}
