package com.gultekinahmetabdullah.trainvoc.features.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
 * Admin Dashboard for Feature Flag Management
 * Shows all features with global controls, cost monitoring, and analytics
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFeatureFlagScreen(
    onBack: () -> Unit,
    viewModel: FeatureFlagViewModel = hiltViewModel()
) {
    val globalFlags by viewModel.globalFlags.collectAsState()
    val costToday by viewModel.costToday.collectAsState()
    val costThisMonth by viewModel.costThisMonth.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filteredFeatures by viewModel.filteredFeatures.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Feature Flags (Admin)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Quick actions
                    IconButton(onClick = { viewModel.refreshCostData() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "More")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Enable All Features") },
                            onClick = {
                                viewModel.enableAllFeatures()
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Disable Expensive Features") },
                            onClick = {
                                viewModel.disableExpensiveFeatures()
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Reset Daily Usage") },
                            onClick = {
                                viewModel.resetAllDailyUsage()
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
            // Cost Summary Card
            item {
                CostSummaryCard(
                    costToday = costToday,
                    costThisMonth = costThisMonth
                )
            }

            // Loading indicator
            if (isLoading) {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
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

            // Features list
            items(
                items = filteredFeatures,
                key = { it.key }
            ) { feature ->
                val globalFlag = globalFlags.find { it.featureKey == feature.key }
                AdminFeatureFlagCard(
                    feature = feature,
                    globalFlag = globalFlag,
                    onToggle = { viewModel.toggleGlobalFeature(feature) },
                    onRolloutChange = { percentage ->
                        viewModel.setRolloutPercentage(feature, percentage)
                    },
                    onDailyLimitChange = { limit ->
                        viewModel.setDailyLimit(feature, limit)
                    }
                )
            }
        }
    }
}

@Composable
fun CostSummaryCard(
    costToday: Double,
    costThisMonth: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Cost Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "$${"%.2f".format(costToday)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (costToday > 10.0) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "This Month",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "$${"%.2f".format(costThisMonth)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (costThisMonth > 500.0) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterChips(
    selectedCategory: FeatureCategory?,
    onCategorySelected: (FeatureCategory?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("All") },
                leadingIcon = if (selectedCategory == null) {
                    { Icon(Icons.Default.Check, "Selected", modifier = Modifier.size(18.dp)) }
                } else null
            )
        }
        items(FeatureCategory.values().toList()) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category.displayName) },
                leadingIcon = if (selectedCategory == category) {
                    { Icon(Icons.Default.Check, "Selected", modifier = Modifier.size(18.dp)) }
                } else null
            )
        }
    }
}

@Composable
fun AdminFeatureFlagCard(
    feature: FeatureFlag,
    globalFlag: com.gultekinahmetabdullah.trainvoc.features.database.GlobalFeatureFlag?,
    onToggle: () -> Unit,
    onRolloutChange: (Int) -> Unit,
    onDailyLimitChange: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val isEnabled = globalFlag?.enabled ?: feature.defaultEnabled

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = feature.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
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
                                modifier = Modifier.height(24.dp)
                            )
                        }
                        if (feature.hasCost) {
                            AssistChip(
                                onClick = {},
                                label = { Text("💰 Cost", style = MaterialTheme.typography.labelSmall) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                modifier = Modifier.height(24.dp)
                            )
                        }
                        if (feature.adminOnly) {
                            AssistChip(
                                onClick = {},
                                label = { Text("🔒 Admin", style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    }
                }

                Switch(
                    checked = isEnabled,
                    onCheckedChange = { onToggle() }
                )
            }

            // Stats Row (when enabled)
            AnimatedVisibility(visible = isEnabled && globalFlag != null) {
                globalFlag?.let { flag ->
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Rollout: ${flag.rolloutPercentage}%",
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (feature.hasCost) {
                                Text(
                                    text = "Usage: ${flag.currentDailyUsage}/${flag.maxDailyUsage ?: "∞"}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Cost: ${"%.2f".format(flag.totalCost)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        // Expand button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { expanded = !expanded }) {
                                Text(if (expanded) "Less" else "More")
                                Icon(
                                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }

            // Expanded controls
            AnimatedVisibility(visible = expanded && isEnabled && globalFlag != null) {
                globalFlag?.let { flag ->
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Rollout Percentage Slider
                        Text(
                            "Rollout Percentage: ${flag.rolloutPercentage}%",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Slider(
                            value = flag.rolloutPercentage.toFloat(),
                            onValueChange = { onRolloutChange(it.toInt()) },
                            valueRange = 0f..100f,
                            steps = 9  // 0, 10, 20, ..., 100
                        )

                        // Daily Limit (if has cost)
                        if (feature.hasCost) {
                            Text(
                                "Daily API Call Limit: ${flag.maxDailyUsage ?: "Unlimited"}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { onDailyLimitChange(1000) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("1K")
                                }
                                TextButton(
                                    onClick = { onDailyLimitChange(5000) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("5K")
                                }
                                TextButton(
                                    onClick = { onDailyLimitChange(10000) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("10K")
                                }
                                TextButton(
                                    onClick = { onDailyLimitChange(null) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("∞")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
