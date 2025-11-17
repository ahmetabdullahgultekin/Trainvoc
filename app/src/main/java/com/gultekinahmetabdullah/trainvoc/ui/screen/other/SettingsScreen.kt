package com.gultekinahmetabdullah.trainvoc.ui.screen.other

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.ColorPalettePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.LanguagePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.classes.enums.ThemePreference
import com.gultekinahmetabdullah.trainvoc.notification.NotificationPreferences
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import android.os.Build
import com.gultekinahmetabdullah.trainvoc.ui.animations.rememberHapticPerformer
import com.gultekinahmetabdullah.trainvoc.ui.animations.pressClickable
import com.gultekinahmetabdullah.trainvoc.ui.animations.bounceIn

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val haptic = rememberHapticPerformer()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val theme by viewModel.theme.collectAsState()
    val colorPalette by viewModel.colorPalette.collectAsState()
    val language by viewModel.language.collectAsState()
    val configuration = LocalConfiguration.current // Compose context

    // Notification preferences
    val notificationPrefs = remember { NotificationPreferences.getInstance(context) }
    var dailyRemindersEnabled by remember { mutableStateOf(notificationPrefs.dailyRemindersEnabled) }
    var streakAlertsEnabled by remember { mutableStateOf(notificationPrefs.streakAlertsEnabled) }
    var wordOfDayEnabled by remember { mutableStateOf(notificationPrefs.wordOfDayEnabled) }

    // Listen for language changes and recreate activity to apply new locale
    LaunchedEffect(configuration) {
        viewModel.languageChanged.collectLatest {
            val activity = context as? Activity
            val localeCode = language.code
            val locale = Locale(localeCode)
            Locale.setDefault(locale)
            // Activity recreation will automatically apply the new configuration
            activity?.recreate()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.mediumLarge),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        Text(stringResource(id = R.string.settings), style = MaterialTheme.typography.headlineSmall)

        // Theme Customization Section Header
        Text(
            text = "Theme Customization",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        // Theme Mode Selection (System, Light, Dark, AMOLED)
        val themeOptions = listOf(
            ThemePreference.SYSTEM,
            ThemePreference.LIGHT,
            ThemePreference.DARK,
            ThemePreference.AMOLED
        )
        val themeLabels = listOf(
            stringResource(id = R.string.system_default),
            stringResource(id = R.string.light),
            stringResource(id = R.string.dark),
            "AMOLED"
        )
        val selectedThemeIndex = themeOptions.indexOf(theme)
        SettingDropdown(
            title = stringResource(id = R.string.theme),
            options = themeLabels,
            selectedOption = themeLabels.getOrElse(selectedThemeIndex) { themeLabels[0] },
            onOptionSelected = { label ->
                val index = themeLabels.indexOf(label)
                viewModel.setTheme(themeOptions.getOrElse(index) {
                    ThemePreference.SYSTEM
                })
            }
        )

        // Color Palette Selection
        Text(
            text = "Color Palette",
            style = MaterialTheme.typography.bodyLarge
        )

        // Color palette preview cards
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(ColorPalettePreference.entries.toList()) { palette ->
                // Skip Dynamic on older Android versions
                if (palette == ColorPalettePreference.DYNAMIC && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    return@items
                }

                ColorPaletteCard(
                    palette = palette,
                    isSelected = palette == colorPalette,
                    onClick = { viewModel.setColorPalette(palette) }
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.small))

        // Notification Settings Section
        Text(
            text = stringResource(id = R.string.notification_settings),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        // Master Notifications Toggle
        SettingSwitch(
            title = stringResource(id = R.string.enable_notifications),
            isChecked = notificationsEnabled,
            onCheckedChange = { isChecked ->
                viewModel.setNotificationsEnabled(isChecked)
                if (isChecked) {
                    showToast(context, context.getString(R.string.notifications_enabled))
                } else {
                    showToast(context, context.getString(R.string.notifications_disabled))
                }
            }
        )

        // Individual notification type controls (only visible when notifications are enabled)
        if (notificationsEnabled) {
            // Daily Reminders
            SettingSwitchWithDescription(
                title = stringResource(id = R.string.enable_daily_reminders),
                description = stringResource(id = R.string.daily_reminders_desc),
                isChecked = dailyRemindersEnabled,
                onCheckedChange = { isChecked ->
                    dailyRemindersEnabled = isChecked
                    notificationPrefs.dailyRemindersEnabled = isChecked
                }
            )

            // Streak Alerts
            SettingSwitchWithDescription(
                title = stringResource(id = R.string.enable_streak_alerts),
                description = stringResource(id = R.string.streak_alerts_desc),
                isChecked = streakAlertsEnabled,
                onCheckedChange = { isChecked ->
                    streakAlertsEnabled = isChecked
                    notificationPrefs.streakAlertsEnabled = isChecked
                }
            )

            // Word of the Day
            SettingSwitchWithDescription(
                title = stringResource(id = R.string.enable_word_of_day),
                description = stringResource(id = R.string.word_of_day_desc),
                isChecked = wordOfDayEnabled,
                onCheckedChange = { isChecked ->
                    wordOfDayEnabled = isChecked
                    notificationPrefs.wordOfDayEnabled = isChecked
                }
            )
        }

        // Language Selection - All 6 supported languages
        val languageOptions = listOf(
            LanguagePreference.ENGLISH,
            LanguagePreference.TURKISH,
            LanguagePreference.SPANISH,
            LanguagePreference.GERMAN,
            LanguagePreference.FRENCH,
            LanguagePreference.ARABIC
        )
        val languageLabels = listOf(
            stringResource(id = R.string.english),
            stringResource(id = R.string.turkish),
            stringResource(id = R.string.spanish),
            stringResource(id = R.string.german),
            stringResource(id = R.string.french),
            stringResource(id = R.string.arabic)
        )
        val selectedLanguageIndex = languageOptions.indexOf(language)
        SettingDropdown(
            title = stringResource(id = R.string.language),
            options = languageLabels,
            selectedOption = languageLabels.getOrElse(selectedLanguageIndex) { languageLabels[0] },
            onOptionSelected = { label ->
                val index = languageLabels.indexOf(label)
                viewModel.setLanguage(languageOptions.getOrElse(index) { LanguagePreference.ENGLISH })
            }
        )

        // Manage Account
        Button(
            onClick = {
                haptic.click()
                navController.navigate(Route.MANAGEMENT)
            },
            shape = RoundedCornerShape(CornerRadius.medium),
            modifier = Modifier
                .fillMaxWidth()
                .pressClickable { }
        ) {
            Text(stringResource(id = R.string.manage_words))
        }

        // Reset Progress
        Button(
            onClick = {
                haptic.longPress()
                viewModel.resetProgress()
                showToast(context, context.getString(R.string.progress_reset))
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(CornerRadius.medium),
            modifier = Modifier
                .fillMaxWidth()
                .pressClickable { }
        ) {
            Text(
                stringResource(id = R.string.reset_progress),
                color = MaterialTheme.colorScheme.onError
            )
        }

        // Logout
        Button(
            onClick = {
                haptic.click()
                viewModel.logout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(CornerRadius.medium),
            modifier = Modifier
                .fillMaxWidth()
                .pressClickable { }
        ) {
            Text(stringResource(id = R.string.logout), color = MaterialTheme.colorScheme.onError)
        }
    }
}

// Custom Dropdown for Theme & Language Selection
@Composable
fun SettingDropdown(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { expanded = true },
                shape = RoundedCornerShape(CornerRadius.medium),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedOption)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

// Custom Switch for Notifications
@Composable
fun SettingSwitch(title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val haptic = rememberHapticPerformer()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked,
            onCheckedChange = {
                haptic.click()
                onCheckedChange(it)
            }
        )
    }
}

// Custom Switch with Description for Notification Types
@Composable
fun SettingSwitchWithDescription(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val haptic = rememberHapticPerformer()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = {
                haptic.click()
                onCheckedChange(it)
            }
        )
    }
}

// Toast function
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

/**
 * Color Palette Preview Card
 *
 * Displays a preview card for each color palette with:
 * - Palette name
 * - Color preview squares showing primary, secondary, and tertiary colors
 * - Selection indicator (border)
 */
@Composable
fun ColorPaletteCard(
    palette: ColorPalettePreference,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val haptic = rememberHapticPerformer()
    val colors = getPreviewColors(palette)

    Column(
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(CornerRadius.medium))
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(CornerRadius.medium)
            )
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .pressClickable {
                haptic.click()
                onClick()
            }
            .padding(Spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)
    ) {
        // Color preview squares
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(CornerRadius.extraSmall))
                        .background(color)
                )
            }
        }

        // Palette name
        Text(
            text = palette.displayName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

/**
 * Get preview colors for each palette
 * Returns a list of 3 colors representing the palette's primary, secondary, and tertiary colors
 */
private fun getPreviewColors(palette: ColorPalettePreference): List<Color> {
    return when (palette) {
        ColorPalettePreference.DEFAULT -> listOf(
            Color(0xFFAAD7D9),  // Primary
            Color(0xFF92C7CF),  // Secondary
            Color(0xFF66BB6A)   // Accent (UnlockedLeaf)
        )
        ColorPalettePreference.OCEAN -> listOf(
            Color(0xFF0277BD),  // Deep ocean blue
            Color(0xFF00ACC1),  // Cyan teal
            Color(0xFF1976D2)   // Rich blue
        )
        ColorPalettePreference.FOREST -> listOf(
            Color(0xFF2E7D32),  // Forest green
            Color(0xFF558B2F),  // Olive green
            Color(0xFF689F38)   // Light green
        )
        ColorPalettePreference.SUNSET -> listOf(
            Color(0xFFE64A19),  // Deep orange
            Color(0xFF7B1FA2),  // Purple
            Color(0xFFFF6F00)   // Bright orange
        )
        ColorPalettePreference.LAVENDER -> listOf(
            Color(0xFF6A1B9A),  // Deep purple
            Color(0xFFAB47BC),  // Medium purple
            Color(0xFFD81B60)   // Pink
        )
        ColorPalettePreference.CRIMSON -> listOf(
            Color(0xFFC62828),  // Deep red
            Color(0xFFD84315),  // Red orange
            Color(0xFFAD1457)   // Pink red
        )
        ColorPalettePreference.MINT -> listOf(
            Color(0xFF00897B),  // Teal
            Color(0xFF26A69A),  // Light teal
            Color(0xFF00ACC1)   // Cyan
        )
        ColorPalettePreference.DYNAMIC -> listOf(
            Color(0xFF6750A4),  // Material You default primary
            Color(0xFF625B71),  // Material You default secondary
            Color(0xFF7D5260)   // Material You default tertiary
        )
    }
}
