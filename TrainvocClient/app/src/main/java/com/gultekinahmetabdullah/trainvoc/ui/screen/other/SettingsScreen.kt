package com.gultekinahmetabdullah.trainvoc.ui.screen.other

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.ColorPalettePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.LanguagePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.classes.enums.ThemePreference
import com.gultekinahmetabdullah.trainvoc.notification.NotificationPreferences
import com.gultekinahmetabdullah.trainvoc.ui.animations.pressClickable
import com.gultekinahmetabdullah.trainvoc.ui.animations.rememberHapticPerformer
import com.gultekinahmetabdullah.trainvoc.ui.components.cards.NavigationCard
import com.gultekinahmetabdullah.trainvoc.ui.components.cards.SettingSectionCard
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val haptic = rememberHapticPerformer()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val theme by viewModel.theme.collectAsState()
    val colorPalette by viewModel.colorPalette.collectAsState()
    val language by viewModel.language.collectAsState()
    val configuration = LocalConfiguration.current // Compose context
    val pendingSyncCount by viewModel.pendingSyncCount.collectAsState(initial = 0)
    val isSyncEnabled = remember { viewModel.isSyncEnabled() }

    // Notification preferences
    val notificationPrefs = remember { NotificationPreferences.getInstance(context) }
    var dailyRemindersEnabled by remember { mutableStateOf(notificationPrefs.dailyRemindersEnabled) }
    var streakAlertsEnabled by remember { mutableStateOf(notificationPrefs.streakAlertsEnabled) }
    var wordOfDayEnabled by remember { mutableStateOf(notificationPrefs.wordOfDayEnabled) }

    // Listen for language changes and recreate activity to apply new locale
    LaunchedEffect(Unit) {
        viewModel.languageChanged.collectLatest {
            val activity = context as? Activity
            // Activity recreation will automatically apply the new configuration
            activity?.recreate()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.content_desc_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            // User Profile Card (fixes #196)
            item {
                val prefs = remember { context.getSharedPreferences("trainvoc_prefs", android.content.Context.MODE_PRIVATE) }
                val username = remember { prefs.getString("username", null) ?: "User" }
                val userAvatar = remember { prefs.getString("avatar", null) ?: "🦊" }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.navigate(Route.PROFILE) },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userAvatar,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                        Spacer(modifier = Modifier.width(Spacing.medium))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = username,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "View Profile",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .size(20.dp)
                                .graphicsLayer { rotationZ = 180f }
                        )
                    }
                }
            }

            // Theme Customization Section
            item {
                SettingSectionCard(
                    icon = Icons.Default.Palette,
                    title = stringResource(id = R.string.theme_customization)
                ) {
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
                        stringResource(id = R.string.amoled)
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

                    Spacer(modifier = Modifier.height(Spacing.small))

                    // Color Palette Selection
                    Text(
                        text = stringResource(id = R.string.color_palette),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(Spacing.extraSmall))

                    // Color palette preview cards
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(ColorPalettePreference.entries.toList(), key = { it }) { palette ->
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
                }
            }

            // Notification Settings Section
            item {
                SettingSectionCard(
                    icon = Icons.Default.Notifications,
                    title = stringResource(id = R.string.notification_settings)
                ) {
                    // Master Notifications Toggle
                    SettingSwitch(
                        title = stringResource(id = R.string.enable_notifications),
                        isChecked = notificationsEnabled,
                        onCheckedChange = { isChecked ->
                            viewModel.setNotificationsEnabled(isChecked)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = context.getString(
                                        if (isChecked) R.string.notifications_enabled
                                        else R.string.notifications_disabled
                                    ),
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )

                    // Individual notification type controls (only visible when notifications are enabled)
                    if (notificationsEnabled) {
                        Spacer(modifier = Modifier.height(Spacing.small))

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

                        Spacer(modifier = Modifier.height(Spacing.small))

                        // Advanced Notification Settings navigation
                        NavigationCard(
                            icon = Icons.Default.Settings,
                            title = stringResource(id = R.string.advanced_notification_settings),
                            onClick = {
                                haptic.click()
                                navController.navigate(Route.NOTIFICATION_SETTINGS)
                            }
                        )
                    }
                }
            }

            // Account Section
            item {
                SettingSectionCard(
                    icon = Icons.Default.Person,
                    title = stringResource(id = R.string.account)
                ) {
                    NavigationCard(
                        icon = Icons.Default.Person,
                        title = stringResource(id = R.string.profile),
                        subtitle = stringResource(id = R.string.profile_subtitle),
                        onClick = {
                            haptic.click()
                            navController.navigate(Route.PROFILE)
                        }
                    )
                }
            }

            // Learning Section
            item {
                SettingSectionCard(
                    icon = Icons.Default.Timeline,
                    title = stringResource(id = R.string.learning)
                ) {
                    NavigationCard(
                        icon = Icons.Default.Timeline,
                        title = stringResource(id = R.string.daily_goals),
                        subtitle = stringResource(id = R.string.daily_goals_subtitle),
                        onClick = {
                            haptic.click()
                            navController.navigate(Route.DAILY_GOALS)
                        }
                    )
                }
            }

            // Social & Achievements Section
            item {
                SettingSectionCard(
                    icon = Icons.Default.EmojiEvents,
                    title = stringResource(id = R.string.social_achievements)
                ) {
                    NavigationCard(
                        icon = Icons.Default.EmojiEvents,
                        title = stringResource(id = R.string.achievements),
                        subtitle = stringResource(id = R.string.achievements_subtitle),
                        onClick = {
                            haptic.click()
                            navController.navigate(Route.ACHIEVEMENTS)
                        }
                    )

                    Spacer(modifier = Modifier.height(Spacing.extraSmall))

                    NavigationCard(
                        icon = Icons.Default.Leaderboard,
                        title = stringResource(id = R.string.leaderboard),
                        subtitle = stringResource(id = R.string.leaderboard_subtitle),
                        onClick = {
                            haptic.click()
                            navController.navigate(Route.LEADERBOARD)
                        }
                    )
                }
            }

            // Progress Section
            item {
                SettingSectionCard(
                    icon = Icons.Default.Timeline,
                    title = stringResource(id = R.string.progress)
                ) {
                    NavigationCard(
                        icon = Icons.Default.Timeline,
                        title = stringResource(id = R.string.word_progress),
                        subtitle = stringResource(id = R.string.word_progress_subtitle),
                        onClick = {
                            haptic.click()
                            navController.navigate(Route.WORD_PROGRESS)
                        }
                    )
                }
            }

            // Backup & Sync Section
            item {
                val syncSubtitle = if (isSyncEnabled) {
                    if (pendingSyncCount > 0) {
                        stringResource(id = R.string.items_pending_sync, pendingSyncCount)
                    } else {
                        stringResource(id = R.string.all_data_synced)
                    }
                } else {
                    stringResource(id = R.string.login_to_enable_sync)
                }
                val syncStartedMsg = stringResource(id = R.string.sync_started)
                val loginToSyncMsg = stringResource(id = R.string.please_login_to_sync)

                SettingSectionCard(
                    icon = Icons.Default.CloudQueue,
                    title = stringResource(id = R.string.backup_sync)
                ) {
                    NavigationCard(
                        icon = Icons.Default.CloudQueue,
                        title = stringResource(id = R.string.backup_restore),
                        subtitle = stringResource(id = R.string.backup_restore_subtitle),
                        onClick = {
                            haptic.click()
                            navController.navigate(Route.BACKUP)
                        }
                    )

                    Spacer(modifier = Modifier.height(Spacing.small))

                    // Sync Now Button
                    NavigationCard(
                        icon = Icons.Default.CloudSync,
                        title = stringResource(id = R.string.sync_now),
                        subtitle = syncSubtitle,
                        onClick = {
                            if (isSyncEnabled) {
                                haptic.click()
                                viewModel.syncNow()
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = syncStartedMsg,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = loginToSyncMsg,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }
                    )
                }
            }

            // Accessibility Section
            item {
                SettingSectionCard(
                    icon = Icons.Default.Accessibility,
                    title = stringResource(id = R.string.accessibility)
                ) {
                    NavigationCard(
                        icon = Icons.Default.Accessibility,
                        title = stringResource(id = R.string.accessibility_settings),
                        subtitle = stringResource(id = R.string.accessibility_subtitle),
                        onClick = {
                            haptic.click()
                            navController.navigate(Route.ACCESSIBILITY_SETTINGS)
                        }
                    )
                }
            }

            // Audio & Pronunciation Section
            item {
                SettingSectionCard(
                    icon = Icons.Default.RecordVoiceOver,
                    title = stringResource(id = R.string.audio_pronunciation)
                ) {
                    // TTS Enable Toggle (always available - Android TTS is free)
                    SettingSwitch(
                        title = stringResource(id = R.string.enable_pronunciation),
                        isChecked = true, // TTS is always enabled since it's free
                        onCheckedChange = { /* No-op, TTS is always available */ }
                    )

                    Text(
                        text = stringResource(id = R.string.pronunciation_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = Spacing.extraSmall)
                    )
                }
            }

            // Language Selection Section
            item {
                SettingSectionCard(
                    icon = Icons.Default.Language,
                    title = stringResource(id = R.string.language)
                ) {
                    // Language Toggle Switch - English / Turkish
                    LanguageToggleSwitch(
                        currentLanguage = language,
                        onLanguageChanged = { newLanguage ->
                            viewModel.setLanguage(newLanguage)
                        }
                    )
                }
            }

            // Other Actions Section
            item {
                SettingSectionCard(
                    icon = Icons.Default.Settings,
                    title = stringResource(id = R.string.other_actions)
                ) {
                    // Manage Words
                    NavigationCard(
                        icon = Icons.Default.Settings,
                        title = stringResource(id = R.string.manage_words),
                        subtitle = stringResource(id = R.string.manage_words_subtitle),
                        onClick = {
                            haptic.click()
                            navController.navigate(Route.MANAGEMENT)
                        }
                    )

                    Spacer(modifier = Modifier.height(Spacing.small))

                    // Reset Progress Button (Danger action)
                    Button(
                        onClick = {
                            haptic.longPress()
                            viewModel.resetProgress()
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.progress_reset),
                                    duration = SnackbarDuration.Short
                                )
                            }
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

                    Spacer(modifier = Modifier.height(Spacing.extraSmall))

                    // Logout Button (Danger action)
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

            // Bottom spacing
            item { Spacer(modifier = Modifier.height(Spacing.medium)) }
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

/**
 * Language Toggle Switch Component
 *
 * A toggle switch for selecting between English and Turkish languages.
 * Shows both language options with visual indication of the current selection.
 */
@Composable
fun LanguageToggleSwitch(
    currentLanguage: LanguagePreference,
    onLanguageChanged: (LanguagePreference) -> Unit
) {
    val haptic = rememberHapticPerformer()
    val isEnglish = currentLanguage == LanguagePreference.ENGLISH

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.medium))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(Spacing.extraSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // English option
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(CornerRadius.small))
                .background(
                    if (isEnglish) MaterialTheme.colorScheme.primary
                    else Color.Transparent
                )
                .pressClickable {
                    if (!isEnglish) {
                        haptic.click()
                        onLanguageChanged(LanguagePreference.ENGLISH)
                    }
                }
                .padding(vertical = Spacing.small),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "English",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isEnglish)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Turkish option
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(CornerRadius.small))
                .background(
                    if (!isEnglish) MaterialTheme.colorScheme.primary
                    else Color.Transparent
                )
                .pressClickable {
                    if (isEnglish) {
                        haptic.click()
                        onLanguageChanged(LanguagePreference.TURKISH)
                    }
                }
                .padding(vertical = Spacing.small),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Türkçe",
                style = MaterialTheme.typography.bodyLarge,
                color = if (!isEnglish)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
