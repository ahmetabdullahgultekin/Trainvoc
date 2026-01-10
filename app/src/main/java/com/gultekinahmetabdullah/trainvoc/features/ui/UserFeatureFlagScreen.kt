package com.gultekinahmetabdullah.trainvoc.features.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.features.FeatureCategory
import com.gultekinahmetabdullah.trainvoc.features.FeatureFlag
import com.gultekinahmetabdullah.trainvoc.features.viewmodel.FeatureFlagViewModel

/**
 * User Feature Preferences Screen
 * Allows users to opt-in/opt-out of features they want to use
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFeatureFlagScreen(
    onBack: () -> Unit,
    viewModel: FeatureFlagViewModel = hiltViewModel()
) {
    val userFlags by viewModel.userFlags.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val error by viewModel.error.collectAsState()

    // Get only user-configurable features (not admin-only)
    val userConfigurableFeatures = remember {
        FeatureFlag.getAllUserConfigurable()
    }

    val filteredFeatures = remember(selectedCategory, userConfigurableFeatures) {
        if (selectedCategory == null) {
            userConfigurableFeatures
        } else {
            userConfigurableFeatures.filter { it.category == selectedCategory }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Feature Preferences") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "More")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Enable All") },
                            onClick = {
                                viewModel.enableAllUserFeatures()
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Disable All") },
                            onClick = {
                                viewModel.disableAllUserFeatures()
                                showMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Info card
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Control which features you want to use. Disabling features can save battery and data.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Error message
            error?.let { errorMsg ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = errorMsg,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.clearError() }) {
                                Icon(Icons.Default.Close, "Dismiss")
                            }
                        }
                    }
                }
            }

            // Category filter chips
            item {
                CategoryFilterChips(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) }
                )
            }

            // Features list grouped by category
            if (selectedCategory == null) {
                // Show all categories
                FeatureCategory.values().forEach { category ->
                    val categoryFeatures = userConfigurableFeatures.filter { it.category == category }
                    if (categoryFeatures.isNotEmpty()) {
                        item {
                            Text(
                                text = category.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        items(categoryFeatures) { feature ->
                            val userFlag = userFlags.find { it.featureKey == feature.key }
                            UserFeatureFlagCard(
                                feature = feature,
                                userFlag = userFlag,
                                onToggle = { viewModel.toggleUserFeature(feature) }
                            )
                        }
                    }
                }
            } else {
                // Show filtered category
                items(filteredFeatures) { feature ->
                    val userFlag = userFlags.find { it.featureKey == feature.key }
                    UserFeatureFlagCard(
                        feature = feature,
                        userFlag = userFlag,
                        onToggle = { viewModel.toggleUserFeature(feature) }
                    )
                }
            }
        }
    }
}

@Composable
fun UserFeatureFlagCard(
    feature: FeatureFlag,
    userFlag: com.gultekinahmetabdullah.trainvoc.features.database.UserFeatureFlag?,
    onToggle: () -> Unit
) {
    val isEnabled = userFlag?.userEnabled ?: true
    val hasUsed = userFlag?.hasUsedFeature ?: false
    val usageCount = userFlag?.usageCount ?: 0

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = feature.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // Show icon if feature has special properties
                    if (feature.hasCost) {
                        Icon(
                            Icons.Default.AttachMoney,
                            contentDescription = "Costs money",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Badges
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (feature.isPremium) {
                        AssistChip(
                            onClick = {},
                            label = { Text("Premium", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.height(24.dp),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        )
                    }
                    if (feature.hasCost) {
                        AssistChip(
                            onClick = {},
                            label = { Text("Uses Data", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }

                // Usage stats
                if (hasUsed && usageCount > 0) {
                    Text(
                        text = "Used $usageCount ${if (usageCount == 1) "time" else "times"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = { onToggle() }
            )
        }
    }
}
