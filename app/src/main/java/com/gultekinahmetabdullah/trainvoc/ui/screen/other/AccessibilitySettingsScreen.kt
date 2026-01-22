package com.gultekinahmetabdullah.trainvoc.ui.screen.other

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.ui.animations.rememberHapticPerformer
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.viewmodel.AccessibilityViewModel

/**
 * Accessibility Settings Screen
 *
 * Provides accessibility options for users with different needs:
 * - High contrast mode
 * - Color blind friendly palette
 * - Text size adjustment
 * - Haptic feedback toggle
 * - Reduce motion option
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilitySettingsScreen(
    onBackClick: () -> Unit = {},
    viewModel: AccessibilityViewModel = hiltViewModel()
) {
    val haptic = rememberHapticPerformer()

    // Settings state from ViewModel
    val highContrastEnabled by viewModel.highContrastEnabled.collectAsState()
    val colorBlindModeEnabled by viewModel.colorBlindModeEnabled.collectAsState()
    val hapticFeedbackEnabled by viewModel.hapticFeedbackEnabled.collectAsState()
    val reduceMotionEnabled by viewModel.reduceMotionEnabled.collectAsState()
    val textSizeScale by viewModel.textSizeScale.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accessibility") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
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
            // Visual Settings Section
            item {
                SectionHeader(title = "Visual")
            }

            item {
                SettingsCard(
                    icon = Icons.Default.Contrast,
                    title = "High Contrast Mode",
                    subtitle = "Increase contrast for better visibility",
                    trailing = {
                        Switch(
                            checked = highContrastEnabled,
                            onCheckedChange = {
                                haptic.click()
                                viewModel.setHighContrastEnabled(it)
                            }
                        )
                    }
                )
            }

            item {
                SettingsCard(
                    icon = Icons.Default.Palette,
                    title = "Color Blind Friendly",
                    subtitle = "Use colors optimized for color vision deficiency",
                    trailing = {
                        Switch(
                            checked = colorBlindModeEnabled,
                            onCheckedChange = {
                                haptic.click()
                                viewModel.setColorBlindModeEnabled(it)
                            }
                        )
                    }
                )
            }

            // Color Blind Mode Options
            if (colorBlindModeEnabled) {
                item {
                    ColorBlindModeSelector(viewModel)
                }
            }

            item {
                SettingsCard(
                    icon = Icons.Default.FormatSize,
                    title = "Text Size",
                    subtitle = "Adjust text size: ${(textSizeScale * 100).toInt()}%"
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Slider(
                            value = textSizeScale,
                            onValueChange = { viewModel.setTextSizeScale(it) },
                            valueRange = 0.8f..1.5f,
                            steps = 6,
                            modifier = Modifier.padding(top = Spacing.small)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Aa", style = MaterialTheme.typography.bodySmall)
                            Text("Aa", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }

            // Interaction Settings Section
            item {
                Spacer(modifier = Modifier.height(Spacing.medium))
                SectionHeader(title = "Interaction")
            }

            item {
                SettingsCard(
                    icon = Icons.Default.Vibration,
                    title = "Haptic Feedback",
                    subtitle = "Vibration feedback for interactions",
                    trailing = {
                        Switch(
                            checked = hapticFeedbackEnabled,
                            onCheckedChange = {
                                if (it) haptic.click()
                                viewModel.setHapticFeedbackEnabled(it)
                            }
                        )
                    }
                )
            }

            item {
                SettingsCard(
                    icon = Icons.Default.Contrast,
                    title = "Reduce Motion",
                    subtitle = "Minimize animations and transitions",
                    trailing = {
                        Switch(
                            checked = reduceMotionEnabled,
                            onCheckedChange = {
                                haptic.click()
                                viewModel.setReduceMotionEnabled(it)
                            }
                        )
                    }
                )
            }

            // Preview Section
            item {
                Spacer(modifier = Modifier.height(Spacing.medium))
                SectionHeader(title = "Preview")
            }

            item {
                AccessibilityPreviewCard(
                    highContrast = highContrastEnabled,
                    textScale = textSizeScale
                )
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.large))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = Spacing.small)
    )
}

@Composable
private fun SettingsCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.medium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Accessibility icon",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(Spacing.medium))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                trailing?.invoke()
            }

            content?.invoke()
        }
    }
}

@Composable
private fun ColorBlindModeSelector(viewModel: AccessibilityViewModel) {
    val selectedMode by viewModel.colorBlindMode.collectAsState()

    val modes = listOf(
        "deuteranopia" to "Deuteranopia (Green-weak)",
        "protanopia" to "Protanopia (Red-weak)",
        "tritanopia" to "Tritanopia (Blue-weak)"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 56.dp), // Indent to align with settings cards
        shape = RoundedCornerShape(CornerRadius.medium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            modes.forEach { (key, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { viewModel.setColorBlindMode(key) }
                        .background(
                            if (selectedMode == key)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            else
                                Color.Transparent
                        )
                        .padding(Spacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .border(
                                2.dp,
                                if (selectedMode == key)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedMode == key) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        CircleShape
                                    )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(Spacing.medium))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun AccessibilityPreviewCard(
    highContrast: Boolean,
    textScale: Float
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (highContrast)
            Color.Black
        else
            MaterialTheme.colorScheme.surfaceVariant,
        label = "bg"
    )

    val textColor by animateColorAsState(
        targetValue = if (highContrast)
            Color.White
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        label = "text"
    )

    val accentColor by animateColorAsState(
        targetValue = if (highContrast)
            Color.Yellow
        else
            MaterialTheme.colorScheme.primary,
        label = "accent"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.medium),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium)
        ) {
            Text(
                text = "Preview",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = MaterialTheme.typography.titleSmall.fontSize * textScale
                ),
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(Spacing.small))
            Text(
                text = "This is how text will appear with your current accessibility settings. The contrast and size adjustments help ensure readability for all users.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize * textScale
                ),
                color = textColor
            )
            Spacer(modifier = Modifier.height(Spacing.medium))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(accentColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Accessibility icon",
                        tint = if (highContrast) Color.Black else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "Button Example",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = MaterialTheme.typography.labelLarge.fontSize * textScale
                    ),
                    color = textColor,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}
