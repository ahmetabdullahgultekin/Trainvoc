package com.gultekinahmetabdullah.trainvoc.ui.screen.extra

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    val context = LocalContext.current
    var notificationsEnabled by remember { mutableStateOf(viewModel.isNotificationsEnabled()) }
    var selectedTheme by remember { mutableStateOf(viewModel.getTheme()) }
    var selectedLanguage by remember { mutableStateOf(viewModel.getLanguage()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)

        // Theme Selection
        SettingDropdown(
            title = "Theme",
            options = listOf("System Default", "Light", "Dark"),
            selectedOption = selectedTheme,
            onOptionSelected = {
                selectedTheme = it
                viewModel.setTheme(it)
                showToast(context, "Theme updated: $it")
            }
        )

        // Notifications Toggle
        SettingSwitch(
            title = "Enable Notifications",
            isChecked = notificationsEnabled,
            onCheckedChange = {
                notificationsEnabled = it
                viewModel.setNotificationsEnabled(it)
                showToast(context, "Notifications: ${if (it) "Enabled" else "Disabled"}")
            }
        )

        // Language Selection
        SettingDropdown(
            title = "Language",
            options = listOf("English", "Turkish", "Spanish", "French"),
            selectedOption = selectedLanguage,
            onOptionSelected = {
                selectedLanguage = it
                viewModel.setLanguage(it)
                showToast(context, "Language updated: $it")
            }
        )

        // Manage Account
        Button(
            onClick = { navController.navigate(Route.MANAGEMENT.name) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Manage Words")
        }

        // Reset Progress
        Button(
            onClick = {
                viewModel.resetProgress()
                showToast(context, "Progress Reset")
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Progress", color = MaterialTheme.colorScheme.onError)
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
            Text("Logout", color = MaterialTheme.colorScheme.onError)
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
