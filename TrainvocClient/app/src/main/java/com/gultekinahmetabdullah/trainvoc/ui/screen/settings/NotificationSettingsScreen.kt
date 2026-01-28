package com.gultekinahmetabdullah.trainvoc.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.viewmodel.NotificationSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NotificationSettingsScreen(
    navController: NavController,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val wordQuizEnabled by viewModel.wordQuizEnabled.collectAsState()
    val wordQuizInterval by viewModel.wordQuizInterval.collectAsState()
    val enabledLevels by viewModel.enabledLevels.collectAsState()
    val enabledExams by viewModel.enabledExams.collectAsState()
    val includeLearnedWords by viewModel.includeLearnedWords.collectAsState()
    val includeLowAccuracyWords by viewModel.includeLowAccuracyWords.collectAsState()
    val quietHoursEnabled by viewModel.quietHoursEnabled.collectAsState()
    val quietHoursStart by viewModel.quietHoursStart.collectAsState()
    val quietHoursEnd by viewModel.quietHoursEnd.collectAsState()
    val dailyRemindersEnabled by viewModel.dailyRemindersEnabled.collectAsState()
    val streakAlertsEnabled by viewModel.streakAlertsEnabled.collectAsState()
    val wordOfDayEnabled by viewModel.wordOfDayEnabled.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.notification_settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Word Quiz Notifications Section
            SectionCard(title = stringResource(R.string.word_quiz_notifications)) {
                // Master toggle
                SettingRow(
                    title = stringResource(R.string.enable_word_quiz),
                    subtitle = stringResource(R.string.enable_word_quiz_desc)
                ) {
                    Switch(
                        checked = wordQuizEnabled,
                        onCheckedChange = { viewModel.setWordQuizEnabled(it) }
                    )
                }

                if (wordQuizEnabled) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Frequency selector
                    Text(
                        text = stringResource(R.string.frequency),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )

                    FrequencySelector(
                        currentMinutes = wordQuizInterval,
                        onFrequencyChanged = { viewModel.setWordQuizInterval(it) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Test notification button
                    Button(
                        onClick = { viewModel.sendTestNotification() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = stringResource(R.string.content_desc_send_test_notification))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.send_test_notification))
                    }
                }
            }

            // Word Filters Section
            if (wordQuizEnabled) {
                SectionCard(title = stringResource(R.string.word_filters)) {
                    // Levels
                    Text(
                        text = stringResource(R.string.include_levels),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        NotificationSettingsViewModel.LEVELS.forEach { level ->
                            FilterChip(
                                selected = level in enabledLevels,
                                onClick = {
                                    viewModel.toggleLevel(level, level !in enabledLevels)
                                },
                                label = { Text(level) }
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Exams
                    Text(
                        text = stringResource(R.string.include_exam_categories),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        NotificationSettingsViewModel.EXAMS.forEach { exam ->
                            FilterChip(
                                selected = exam in enabledExams,
                                onClick = {
                                    viewModel.toggleExam(exam, exam !in enabledExams)
                                },
                                label = { Text(exam) }
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Learning status filters
                    SettingRow(
                        title = stringResource(R.string.include_learned_words),
                        subtitle = stringResource(R.string.include_learned_words_desc)
                    ) {
                        Switch(
                            checked = includeLearnedWords,
                            onCheckedChange = { viewModel.setIncludeLearnedWords(it) }
                        )
                    }

                    SettingRow(
                        title = stringResource(R.string.prioritize_low_accuracy),
                        subtitle = stringResource(R.string.prioritize_low_accuracy_desc)
                    ) {
                        Switch(
                            checked = includeLowAccuracyWords,
                            onCheckedChange = { viewModel.setIncludeLowAccuracyWords(it) }
                        )
                    }
                }

                // Quiet Hours Section
                SectionCard(title = stringResource(R.string.quiet_hours)) {
                    SettingRow(
                        title = stringResource(R.string.enable_quiet_hours),
                        subtitle = stringResource(R.string.enable_quiet_hours_desc)
                    ) {
                        Switch(
                            checked = quietHoursEnabled,
                            onCheckedChange = { viewModel.setQuietHoursEnabled(it) }
                        )
                    }

                    if (quietHoursEnabled) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        val fromLabel = stringResource(R.string.from)
                        val toLabel = stringResource(R.string.to)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TimeSelector(
                                label = fromLabel,
                                hour = quietHoursStart,
                                onHourChanged = { viewModel.setQuietHoursStart(it) },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            TimeSelector(
                                label = toLabel,
                                hour = quietHoursEnd,
                                onHourChanged = { viewModel.setQuietHoursEnd(it) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Other Notifications Section
            SectionCard(title = stringResource(R.string.other_notifications)) {
                SettingRow(
                    title = stringResource(R.string.enable_daily_reminders),
                    subtitle = stringResource(R.string.daily_reminders_desc)
                ) {
                    Switch(
                        checked = dailyRemindersEnabled,
                        onCheckedChange = { viewModel.setDailyRemindersEnabled(it) }
                    )
                }

                SettingRow(
                    title = stringResource(R.string.enable_streak_alerts),
                    subtitle = stringResource(R.string.streak_alerts_desc)
                ) {
                    Switch(
                        checked = streakAlertsEnabled,
                        onCheckedChange = { viewModel.setStreakAlertsEnabled(it) }
                    )
                }

                SettingRow(
                    title = stringResource(R.string.enable_word_of_day),
                    subtitle = stringResource(R.string.word_of_day_desc)
                ) {
                    Switch(
                        checked = wordOfDayEnabled,
                        onCheckedChange = { viewModel.setWordOfDayEnabled(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String? = null,
    action: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        action()
    }
}

@Composable
private fun FrequencySelector(
    currentMinutes: Int,
    onFrequencyChanged: (Int) -> Unit
) {
    val presets = NotificationSettingsViewModel.FREQUENCY_PRESETS
    val currentIndex = presets.indexOfFirst { it.first == currentMinutes }
        .takeIf { it >= 0 } ?: 2 // Default to 1 hour

    var sliderPosition by remember { mutableFloatStateOf(currentIndex.toFloat()) }

    Column {
        Text(
            text = presets.getOrNull(sliderPosition.toInt())?.second ?: "1 hour",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = {
                val selectedPreset = presets.getOrNull(sliderPosition.toInt())
                if (selectedPreset != null) {
                    onFrequencyChanged(selectedPreset.first)
                }
            },
            valueRange = 0f..(presets.size - 1).toFloat(),
            steps = presets.size - 2
        )
    }
}

@Composable
private fun TimeSelector(
    label: String,
    hour: Int,
    onHourChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(8.dp)
                )
                .clickable {
                    // In a full implementation, this would open a time picker dialog
                    // For now, cycle through common hours
                    val newHour = (hour + 1) % 24
                    onHourChanged(newHour)
                }
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format("%02d:00", hour),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
