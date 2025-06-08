package com.gultekinahmetabdullah.trainvoc.ui.screen.other

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val notificationsEnabled by remember { mutableStateOf(viewModel.isNotificationsEnabled()) }
    var selectedTheme by remember { mutableStateOf(viewModel.getTheme()) }
    var selectedLanguage by remember { mutableStateOf(viewModel.getLanguage()) }

    // Tema değişikliğini dinle
    LaunchedEffect(Unit) {
        viewModel.theme.collectLatest {
            selectedTheme = it
        }
    }

    // Dil değişikliğini dinle ve aktivite bağlamında yerel ayarı ayarla, ardından yeniden oluştur
    LaunchedEffect(Unit) {
        viewModel.languageChanged.collectLatest {
            val activity = context as? Activity
            val localeCode = when (selectedLanguage) {
                context.getString(R.string.turkish) -> "tr"
                context.getString(R.string.english) -> "en"
                else -> "en"
            }
            val locale = Locale(localeCode)
            Locale.setDefault(locale)
            val res = context.resources
            val config = Configuration(res.configuration)
            config.setLocale(locale)
            res.updateConfiguration(config, res.displayMetrics)
            activity?.recreate()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(id = R.string.settings), style = MaterialTheme.typography.headlineSmall)

        // Theme Selection
        Column(modifier = Modifier.fillMaxWidth()) {
            SettingDropdown(
                title = stringResource(id = R.string.theme),
                options = listOf(
                    stringResource(id = R.string.system_default),
                    stringResource(id = R.string.light),
                    stringResource(id = R.string.dark)
                ),
                selectedOption = selectedTheme,
                onOptionSelected = {
                    selectedTheme = it
                    viewModel.setTheme(it)
                }
            )
        }

        // Notifications Toggle
        SettingSwitch(
            title = stringResource(id = R.string.enable_notifications),
            isChecked = notificationsEnabled,
            onCheckedChange = { /* Disabled */ }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, bottom = 8.dp)
        ) {
            Text(
                stringResource(id = R.string.notifications_coming_soon),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Language Selection
        SettingDropdown(
            title = stringResource(id = R.string.language),
            options = listOf(
                stringResource(id = R.string.english),
                stringResource(id = R.string.turkish),
            ),
            selectedOption = selectedLanguage,
            onOptionSelected = { option ->
                viewModel.setLanguage(option)
                selectedLanguage = option
                Toast.makeText(context, "$option selected", Toast.LENGTH_SHORT).show()
            }
        )

        // Manage Account
        Button(
            onClick = { navController.navigate(Route.MANAGEMENT) },
            shape = RoundedCornerShape(12.dp),
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
            shape = RoundedCornerShape(12.dp),
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
            shape = RoundedCornerShape(12.dp),
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
                shape = RoundedCornerShape(12.dp),
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