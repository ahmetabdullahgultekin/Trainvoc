package com.gultekinahmetabdullah.trainvoc.ui.screen.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing

/**
 * Leaderboard Screen
 *
 * Shows competitive rankings with:
 * - Tabs: Global, Friends, Weekly, Monthly
 * - Categories: Total XP, Words Learned, Streak, Accuracy
 * - Top 100 rankings
 * - Current user highlight
 * - Leaderboard tiers (Bronze, Silver, Gold, Diamond)
 * - Pull to refresh
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    onBackClick: () -> Unit = {},
    onChallengeClick: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(LeaderboardTab.GLOBAL) }
    var selectedCategory by remember { mutableStateOf(LeaderboardCategory.TOTAL_XP) }
    var isRefreshing by remember { mutableStateOf(false) }

    // TODO: Load from ViewModel
    // Current: Mock data for demonstration
    val leaderboardData = remember { generateMockLeaderboardData() }
    val currentUserId = "current_user" // TODO: Get from auth

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leaderboard") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Category selector
                    var showCategoryMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showCategoryMenu = true }) {
                        Icon(Icons.Default.FilterList, "Category")
                    }

                    DropdownMenu(
                        expanded = showCategoryMenu,
                        onDismissRequest = { showCategoryMenu = false }
                    ) {
                        LeaderboardCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.displayName) },
                                onClick = {
                                    selectedCategory = category
                                    showCategoryMenu = false
                                },
                                leadingIcon = {
                                    Icon(category.icon, category.displayName)
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth()
            ) {
                LeaderboardTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab.displayName) },
                        icon = { Icon(tab.icon, tab.displayName) }
                    )
                }
            }

            // Current tier info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.medium),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Your Current League",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = getTierForScore(2500).displayName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = getTierForScore(2500).color
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        modifier = Modifier.size(48.dp),
                        tint = getTierForScore(2500).color
                    )
                }
            }

            // Leaderboard list
            // TODO: Add pull-to-refresh when implementing backend
            LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = Spacing.medium,
                        vertical = Spacing.small
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(leaderboardData) { index, entry ->
                        LeaderboardEntryCard(
                            rank = index + 1,
                            entry = entry,
                            isCurrentUser = entry.userId == currentUserId,
                            category = selectedCategory,
                            onChallengeClick = { onChallengeClick(entry.userId) }
                        )
                    }

                    // Footer
                    item {
                        Text(
                            text = "Showing top ${leaderboardData.size} users",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
        }
    }
}

@Composable
private fun LeaderboardEntryCard(
    rank: Int,
    entry: LeaderboardEntry,
    isCurrentUser: Boolean,
    category: LeaderboardCategory,
    onChallengeClick: () -> Unit
) {
    val tier = getTierForScore(entry.totalXP)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (isCurrentUser) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank badge
            Surface(
                color = if (rank <= 3) {
                    when (rank) {
                        1 -> Color(0xFFFFD700) // Gold
                        2 -> Color(0xFFC0C0C0) // Silver
                        3 -> Color(0xFFCD7F32) // Bronze
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    }
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                },
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = if (rank <= 999) "#$rank" else "999+",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (rank <= 3) Color.White else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // User info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.username,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Normal
                    )

                    if (isCurrentUser) {
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "YOU",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = tier.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = tier.color
                )
            }

            // Score
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = when (category) {
                        LeaderboardCategory.TOTAL_XP -> "${entry.totalXP} XP"
                        LeaderboardCategory.WORDS_LEARNED -> "${entry.wordsLearned} words"
                        LeaderboardCategory.STREAK -> "${entry.currentStreak} days"
                        LeaderboardCategory.ACCURACY -> "${entry.accuracy}%"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (!isCurrentUser) {
                    TextButton(
                        onClick = onChallengeClick,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            Icons.Default.SportsEsports,
                            "Challenge",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Challenge",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

// Data Classes
data class LeaderboardEntry(
    val userId: String,
    val username: String,
    val totalXP: Int,
    val wordsLearned: Int,
    val currentStreak: Int,
    val accuracy: Int
)

enum class LeaderboardTab(
    val displayName: String,
    val icon: ImageVector
) {
    GLOBAL("Global", Icons.Default.Public),
    FRIENDS("Friends", Icons.Default.People),
    WEEKLY("Weekly", Icons.Default.CalendarMonth),
    MONTHLY("Monthly", Icons.Default.CalendarToday)
}

enum class LeaderboardCategory(
    val displayName: String,
    val icon: ImageVector
) {
    TOTAL_XP("Total XP", Icons.Default.Star),
    WORDS_LEARNED("Words Learned", Icons.Default.Book),
    STREAK("Current Streak", Icons.Default.LocalFireDepartment),
    ACCURACY("Quiz Accuracy", Icons.Default.CheckCircle)
}

enum class LeaderboardTier(
    val displayName: String,
    val minXP: Int,
    val color: Color
) {
    BRONZE("Bronze League", 0, Color(0xFFCD7F32)),
    SILVER("Silver League", 1000, Color(0xFFC0C0C0)),
    GOLD("Gold League", 3000, Color(0xFFFFD700)),
    DIAMOND("Diamond League", 7000, Color(0xFF00D4FF))
}

fun getTierForScore(xp: Int): LeaderboardTier {
    return when {
        xp >= 7000 -> LeaderboardTier.DIAMOND
        xp >= 3000 -> LeaderboardTier.GOLD
        xp >= 1000 -> LeaderboardTier.SILVER
        else -> LeaderboardTier.BRONZE
    }
}

// Mock data generator
private fun generateMockLeaderboardData(): List<LeaderboardEntry> {
    return listOf(
        LeaderboardEntry("user1", "EnglishMaster", 8500, 1250, 45, 95),
        LeaderboardEntry("user2", "VocabNinja", 7200, 980, 38, 92),
        LeaderboardEntry("current_user", "You", 2500, 425, 12, 78),
        LeaderboardEntry("user3", "WordWarrior", 5800, 750, 25, 88),
        LeaderboardEntry("user4", "LangLearner", 4200, 580, 18, 85),
        LeaderboardEntry("user5", "QuizPro", 3500, 520, 22, 90),
        LeaderboardEntry("user6", "StudyBuddy", 2800, 410, 15, 82),
        LeaderboardEntry("user7", "FlashCardFan", 1900, 320, 9, 76),
        LeaderboardEntry("user8", "MemoryChamp", 1500, 250, 11, 80),
        LeaderboardEntry("user9", "BookWorm", 1200, 200, 7, 74),
    )
}
