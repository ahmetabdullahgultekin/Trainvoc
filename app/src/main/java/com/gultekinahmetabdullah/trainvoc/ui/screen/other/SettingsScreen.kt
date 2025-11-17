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
import com.gultekinahmetabdullah.trainvoc.classes.enums.LanguagePreference
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.classes.enums.ThemePreference
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val theme by viewModel.theme.collectAsState()
    val language by viewModel.language.collectAsState()
    val configuration = LocalConfiguration.current // Compose context

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

        // Theme Selection
        val themeOptions = listOf(
            ThemePreference.SYSTEM,
            ThemePreference.LIGHT,
            ThemePreference.DARK
        )
        val themeLabels = listOf(
            stringResource(id = R.string.system_default),
            stringResource(id = R.string.light),
            stringResource(id = R.string.dark)
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

        // Notifications Toggle
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

        // Language Selection
        val languageOptions = listOf(LanguagePreference.ENGLISH, LanguagePreference.TURKISH)
        val languageLabels =
            listOf(stringResource(id = R.string.english), stringResource(id = R.string.turkish))
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
            onClick = { navController.navigate(Route.MANAGEMENT) },
            shape = RoundedCornerShape(CornerRadius.medium),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.manage_words))
        }

        // Reset Progress
        Button(
            onClick = {
                viewModel.resetProgress()
                showToast(context, context.getString(R.string.progress_reset))
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(CornerRadius.medium),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(id = R.string.reset_progress),
                color = MaterialTheme.colorScheme.onError
            )
        }

        // Logout
        Button(
            onClick = {
                viewModel.logout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(CornerRadius.medium),
            modifier = Modifier.fillMaxWidth()
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}

// Toast function
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
