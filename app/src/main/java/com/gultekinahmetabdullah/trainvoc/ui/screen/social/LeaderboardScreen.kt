package com.gultekinahmetabdullah.trainvoc.ui.screen.social

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.gultekinahmetabdullah.trainvoc.ui.components.EmptyState
import com.gultekinahmetabdullah.trainvoc.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Leaderboard Screen - UI/UX Improvement Plan Implementation
 *
 * Features:
 * - 3D Podium design for top 3 users
 * - Time period tabs (Weekly, Monthly, All-Time)
 * - Category tabs (Total XP, Words Learned, Quiz Streak, Quiz Accuracy)
 * - Ranked list for positions 4+
 * - Current user sticky header
 * - Animations (podium rise, trophy bounce, list fade-in)
 * - Pull to refresh
 * - Empty states
 * - User profile tap
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun LeaderboardScreen(
    onBackClick: () -> Unit = {},
    onUserClick: (String) -> Unit = {},
    onChallengeClick: (String) -> Unit = {}
) {
    var selectedTimePeriod by remember { mutableStateOf(TimePeriod.ALL_TIME) }
    var selectedCategory by remember { mutableStateOf(LeaderboardCategory.TOTAL_XP) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // TODO: Load from ViewModel
    // Current: Mock data for demonstration
    val leaderboardData = remember(selectedTimePeriod, selectedCategory) {
        generateMockLeaderboardData()
    }
    val currentUserId = "current_user" // TODO: Get from auth
    val currentUserRank = remember(leaderboardData) {
        leaderboardData.indexOfFirst { it.userId == currentUserId } + 1
    }

    // Pull to refresh
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                isRefreshing = true
                delay(1500) // Simulate API call
                isRefreshing = false
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leaderboard") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Time Period Tabs (Weekly, Monthly, All-Time)
                TabRow(
                    selectedTabIndex = selectedTimePeriod.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    TimePeriod.entries.forEach { period ->
                        Tab(
                            selected = selectedTimePeriod == period,
                            onClick = { selectedTimePeriod = period },
                            text = {
                                Text(
                                    text = period.displayName,
                                    fontWeight = if (selectedTimePeriod == period)
                                        FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                // Category Tabs (Horizontal scrollable chips)
                CategoryTabs(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )

                // Content
                if (leaderboardData.isEmpty()) {
                    EmptyState(
                        message = "No data yet",
                        description = "Complete quizzes to join the leaderboard!",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Top 3 Podium
                        item {
                            PodiumSection(
                                topUsers = leaderboardData.take(3),
                                category = selectedCategory,
                                onUserClick = onUserClick
                            )
                        }

                        // Current User Sticky Header (if not in top 3)
                        if (currentUserRank > 3) {
                            item {
                                CurrentUserStickyHeader(
                                    rank = currentUserRank,
                                    entry = leaderboardData.find { it.userId == currentUserId },
                                    category = selectedCategory
                                )
                            }
                        }

                        // Ranked List (positions 4+)
                        itemsIndexed(
                            items = leaderboardData.drop(3),
                            key = { _, entry -> entry.userId }
                        ) { index, entry ->
                            val rank = index + 4
                            val isCurrentUser = entry.userId == currentUserId

                            RankedListItem(
                                rank = rank,
                                entry = entry,
                                isCurrentUser = isCurrentUser,
                                category = selectedCategory,
                                animationDelay = index * StaggerDelay.extraShort,
                                onUserClick = { onUserClick(entry.userId) },
                                onChallengeClick = { onChallengeClick(entry.userId) }
                            )
                        }

                        // Footer
                        item {
                            Text(
                                text = "Showing top ${leaderboardData.size} users",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.md)
                            )
                        }
                    }
                }
            }

            // Pull to refresh indicator
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Category Tabs - Horizontal scrollable chips
 */
@Composable
private fun CategoryTabs(
    selectedCategory: LeaderboardCategory,
    onCategorySelected: (LeaderboardCategory) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        LeaderboardCategory.entries.forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category.displayName) },
                leadingIcon = {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

/**
 * 3D Podium Section for Top 3 Users
 */
@Composable
private fun PodiumSection(
    topUsers: List<LeaderboardEntry>,
    category: LeaderboardCategory,
    onUserClick: (String) -> Unit
) {
    // Animation states
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(Spacing.lg)
    ) {
        Text(
            text = "Top Champions",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = Spacing.md)
        )

        // Podium display
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            // 2nd place (left)
            if (topUsers.size >= 2) {
                PodiumPlace(
                    rank = 2,
                    entry = topUsers[1],
                    category = category,
                    podiumHeight = 140.dp,
                    podiumColor = Color(0xFFC0C0C0),
                    trophy = "🥈",
                    animationDelay = 0,
                    isVisible = isVisible,
                    onUserClick = onUserClick
                )
            }

            Spacer(Modifier.width(Spacing.sm))

            // 1st place (center, highest)
            if (topUsers.isNotEmpty()) {
                PodiumPlace(
                    rank = 1,
                    entry = topUsers[0],
                    category = category,
                    podiumHeight = 180.dp,
                    podiumColor = Color(0xFFFFD700),
                    trophy = "🥇",
                    animationDelay = 400,
                    isVisible = isVisible,
                    onUserClick = onUserClick,
                    showConfetti = true
                )
            }

            Spacer(Modifier.width(Spacing.sm))

            // 3rd place (right)
            if (topUsers.size >= 3) {
                PodiumPlace(
                    rank = 3,
                    entry = topUsers[2],
                    category = category,
                    podiumHeight = 120.dp,
                    podiumColor = Color(0xFFCD7F32),
                    trophy = "🥉",
                    animationDelay = 200,
                    isVisible = isVisible,
                    onUserClick = onUserClick
                )
            }
        }
    }
}

/**
 * Individual Podium Place
 */
@Composable
private fun RowScope.PodiumPlace(
    rank: Int,
    entry: LeaderboardEntry,
    category: LeaderboardCategory,
    podiumHeight: Dp,
    podiumColor: Color,
    trophy: String,
    animationDelay: Int,
    isVisible: Boolean,
    onUserClick: (String) -> Unit,
    showConfetti: Boolean = false
) {
    // Podium rise animation
    val podiumOffset by animateFloatAsState(
        targetValue = if (isVisible) 0f else 300f,
        animationSpec = tween(
            durationMillis = AppAnimationDuration.slow,
            delayMillis = animationDelay,
            easing = AppEasing.emphasized
        ),
        label = "podiumRise"
    )

    // Trophy bounce animation
    val trophyScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "trophyBounce"
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .offset(y = podiumOffset.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Trophy with bounce animation
        Text(
            text = trophy,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier
                .scale(trophyScale)
                .padding(bottom = Spacing.xs)
        )

        // Avatar
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(Elevation.level3, CircleShape)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            podiumColor,
                            podiumColor.copy(alpha = 0.7f)
                        )
                    )
                )
                .clickable { onUserClick(entry.userId) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.username.first().uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(Modifier.height(Spacing.xs))

        // Username
        Text(
            text = entry.username,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            textAlign = TextAlign.Center
        )

        // Score
        Text(
            text = getCategoryScore(entry, category),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(Spacing.sm))

        // Podium base
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(podiumHeight)
                .shadow(Elevation.level4, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            podiumColor,
                            podiumColor.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Current User Sticky Header
 */
@Composable
private fun CurrentUserStickyHeader(
    rank: Int,
    entry: LeaderboardEntry?,
    category: LeaderboardCategory
) {
    if (entry == null) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.level2),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Your Rank:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.width(Spacing.sm))
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {
                    Text(
                        text = "#$rank",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = getCategoryScore(entry, category),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Ranked List Item (for positions 4+)
 */
@Composable
private fun RankedListItem(
    rank: Int,
    entry: LeaderboardEntry,
    isCurrentUser: Boolean,
    category: LeaderboardCategory,
    animationDelay: Int,
    onUserClick: () -> Unit,
    onChallengeClick: () -> Unit
) {
    // Fade in animation with stagger
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = AppAnimationDuration.medium,
                easing = AppEasing.decelerate
            )
        ) + slideInVertically(
            animationSpec = tween(
                durationMillis = AppAnimationDuration.medium,
                easing = AppEasing.decelerate
            ),
            initialOffsetY = { it / 4 }
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = 4.dp)
                .clickable(onClick = onUserClick),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrentUser) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isCurrentUser) Elevation.level2 else Elevation.level1
            ),
            border = if (isCurrentUser) {
                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            } else null
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank number
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(48.dp)
                )

                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = entry.username.first().uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.width(Spacing.md))

                // User info
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entry.username,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Normal
                        )

                        if (isCurrentUser) {
                            Spacer(Modifier.width(Spacing.sm))
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(CornerRadius.extraSmall)
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

                    // Rank change indicator
                    RankChangeIndicator(change = entry.rankChange)
                }

                // Score
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = getCategoryScore(entry, category),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (!isCurrentUser) {
                        TextButton(
                            onClick = onChallengeClick,
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(
                                Icons.Default.SportsEsports,
                                "Challenge",
                                modifier = Modifier.size(14.dp)
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
}

/**
 * Rank Change Indicator (↑, ↓, —)
 */
@Composable
private fun RankChangeIndicator(change: Int) {
    val (icon, color, text) = when {
        change > 0 -> Triple("↑", Color(0xFF4CAF50), "+$change")
        change < 0 -> Triple("↓", Color(0xFFF44336), "$change")
        else -> Triple("—", Color.Gray, "—")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}

/**
 * Helper function to get category score
 */
private fun getCategoryScore(entry: LeaderboardEntry, category: LeaderboardCategory): String {
    return when (category) {
        LeaderboardCategory.TOTAL_XP -> "${entry.totalXP} XP"
        LeaderboardCategory.WORDS_LEARNED -> "${entry.wordsLearned} words"
        LeaderboardCategory.STREAK -> "${entry.currentStreak} days"
        LeaderboardCategory.ACCURACY -> "${entry.accuracy}%"
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
