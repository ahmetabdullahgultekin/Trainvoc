package com.gultekinahmetabdullah.trainvoc.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.ui.screen.main.HomeViewModel
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import java.text.SimpleDateFormat
import java.util.*

/**
 * Profile Screen - User profile and account information
 *
 * Features:
 * - Display username, avatar, level, XP
 * - Show learning statistics
 * - Edit profile functionality
 * - Sign out option
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onSignOut: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Get username from SharedPreferences
    val prefs = context.getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
    val username = prefs.getString("username", null) ?: "User"
    val accountCreatedDate = prefs.getLong("account_created", System.currentTimeMillis())

    val showEditDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog.value = true }) {
                        Icon(Icons.Default.Edit, "Edit Profile")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header
            ProfileHeader(
                username = username,
                level = uiState.level,
                xp = uiState.xpCurrent,
                xpToNextLevel = uiState.xpForNextLevel
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Statistics Cards
            StatisticsSection(
                totalWords = uiState.totalWords,
                learnedWords = uiState.learnedWords,
                currentStreak = uiState.currentStreak,
                accountCreated = accountCreatedDate
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Account Actions
            AccountActionsSection(
                onEditProfile = { showEditDialog.value = true },
                onSignOut = onSignOut
            )
        }
    }

    // Edit Profile Dialog
    if (showEditDialog.value) {
        EditProfileDialog(
            currentUsername = username,
            onDismiss = { showEditDialog.value = false },
            onSave = { newUsername ->
                // Save new username to SharedPreferences
                prefs.edit().putString("username", newUsername).apply()
                showEditDialog.value = false
                onEditProfile()
            }
        )
    }
}

@Composable
fun ProfileHeader(
    username: String,
    level: Int,
    xp: Int,
    xpToNextLevel: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Avatar Circle
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = username.firstOrNull()?.uppercase() ?: "U",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Username
        Text(
            text = username,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Level Badge
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Level $level",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // XP Progress
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "XP",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$xp / $xpToNextLevel",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (xp.toFloat() / xpToNextLevel.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
        }
    }
}

@Composable
fun StatisticsSection(
    totalWords: Int,
    learnedWords: Int,
    currentStreak: Int,
    accountCreated: Long
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val createdDate = dateFormat.format(Date(accountCreated))

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // Statistics Cards Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                icon = Icons.Default.Book,
                title = "Learned",
                value = "$learnedWords / $totalWords",
                modifier = Modifier.weight(1f)
            )

            StatCard(
                icon = Icons.Default.LocalFireDepartment,
                title = "Streak",
                value = "$currentStreak days",
                modifier = Modifier.weight(1f)
            )
        }

        // Account Created Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Account Created",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column {
                        Text(
                            text = "Member Since",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = createdDate,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun AccountActionsSection(
    onEditProfile: () -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Account",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // Edit Profile Button
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onEditProfile)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Edit, "Edit Profile")
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Icon(Icons.Default.ChevronRight, "Go")
            }
        }

        // Sign Out Button
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSignOut)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Sign Out",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Sign Out",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Go",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    currentUsername: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newUsername by remember { mutableStateOf(currentUsername) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column {
                OutlinedTextField(
                    value = newUsername,
                    onValueChange = {
                        newUsername = it
                        error = when {
                            it.isBlank() -> "Username cannot be empty"
                            it.length < 3 -> "Username must be at least 3 characters"
                            it.length > 20 -> "Username must be at most 20 characters"
                            else -> null
                        }
                    },
                    label = { Text("Username") },
                    isError = error != null,
                    supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (error == null && newUsername.isNotBlank()) {
                        onSave(newUsername)
                    }
                },
                enabled = error == null && newUsername.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
