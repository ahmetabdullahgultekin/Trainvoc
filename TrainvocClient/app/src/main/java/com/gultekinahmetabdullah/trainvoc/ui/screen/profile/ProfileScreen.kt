package com.gultekinahmetabdullah.trainvoc.ui.screen.profile

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.gamification.Achievement
import com.gultekinahmetabdullah.trainvoc.ui.components.AchievementBadge
import com.gultekinahmetabdullah.trainvoc.ui.components.StatsCard
import com.gultekinahmetabdullah.trainvoc.ui.components.CircularProgressIndicator
import com.gultekinahmetabdullah.trainvoc.ui.animations.ShimmerBox
import com.gultekinahmetabdullah.trainvoc.ui.screen.main.HomeViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.AuthViewModel
import com.gultekinahmetabdullah.trainvoc.repository.AuthState
import com.gultekinahmetabdullah.trainvoc.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * Profile Screen - Enhanced User Profile with Statistics and Achievements
 *
 * Features (Updated according to UI/UX Improvement Plan):
 * - Hero section with large avatar, username, level badge, and XP progress
 * - 2x2 Stats Grid: Words Learned, Quizzes Taken, Study Time, Accuracy
 * - Achievements section with horizontal scrolling badges
 * - Actions/Settings section: Edit Profile, View Leaderboard, Settings
 * - Pull-to-refresh support
 * - Animations: Staggered stats entry, animated XP progress, count-up numbers
 * - Additional info: Member since, total XP, streaks
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onSignIn: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onViewAchievements: () -> Unit = {},
    onViewLeaderboard: () -> Unit = {},
    onSettings: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Use AuthViewModel for authentication state (fixes #173 - auth state consistency)
    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Responsive design: Determine grid columns and padding based on screen width
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val gridColumns = when {
        screenWidthDp >= 840 -> 4  // Large tablets/desktops
        screenWidthDp >= 600 -> 3  // Small tablets/landscape
        else -> 2                  // Phones
    }

    // Responsive horizontal padding for ultra-wide displays
    val horizontalPadding = when {
        screenWidthDp >= 1200 -> 120.dp  // Ultra-wide desktops
        screenWidthDp >= 840 -> 64.dp    // Large tablets
        screenWidthDp >= 600 -> 32.dp    // Small tablets
        else -> 0.dp                     // Phones (sections have their own padding)
    }

    // Get username and avatar from SharedPreferences (for display)
    val prefs = context.getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
    val username = currentUser?.displayName ?: prefs.getString("username", null) ?: "User"
    val accountCreatedDate = prefs.getLong("account_created", System.currentTimeMillis())
    // Use AuthViewModel for login state (consistent with auth system)
    val isLoggedIn = authState is AuthState.Authenticated || authState is AuthState.AuthenticatedOffline
    var currentAvatar by remember {
        mutableStateOf(prefs.getString("avatar", com.gultekinahmetabdullah.trainvoc.constants.Avatars.getRandomAvatar()) ?: "\uD83E\uDD8A")
    }

    val showEditDialog = remember { mutableStateOf(false) }
    val showAvatarDialog = remember { mutableStateOf(false) }
    val showLogoutConfirmDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Check auth state when screen loads
    LaunchedEffect(Unit) {
        authViewModel.checkAuthState()
    }

    // Logout confirmation dialog (fixes #210)
    if (showLogoutConfirmDialog.value) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog.value = false },
            icon = { Icon(Icons.Default.Logout, contentDescription = null) },
            title = { Text(stringResource(id = R.string.sign_out)) },
            text = { Text(stringResource(id = R.string.logout_confirmation_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirmDialog.value = false
                        authViewModel.logout()
                        onSignOut()
                    }
                ) {
                    Text(
                        stringResource(id = R.string.sign_out),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog.value = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    // Pull to refresh state
    var isRefreshing by remember { mutableStateOf(false) }
    val onRefresh: () -> Unit = {
        isRefreshing = true
        viewModel.refresh()
        coroutineScope.launch {
            kotlinx.coroutines.delay(1000)
            isRefreshing = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.profile)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, stringResource(id = R.string.back_to_previous_screen))
                    }
                }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hero Section
                item {
                    ProfileHeroSection(
                        username = username,
                        avatar = currentAvatar,
                        onAvatarClick = { showAvatarDialog.value = true },
                        level = uiState.level,
                        xpCurrent = uiState.xpCurrent,
                        xpForNextLevel = uiState.xpForNextLevel,
                        totalXP = uiState.totalScore
                    )
                }

                item { Spacer(modifier = Modifier.height(Spacing.lg)) }

                // Stats Cards Grid (2x2)
                item {
                    // Use total study time if today's time is 0
                    val studyTime = if ((uiState.dailyGoal?.timeTodayMinutes ?: 0) > 0) {
                        uiState.dailyGoal?.timeTodayMinutes ?: 0
                    } else {
                        uiState.totalStudyTimeMinutes
                    }

                    StatsGridSection(
                        learnedWords = uiState.learnedWords,
                        totalWords = uiState.totalWords,
                        quizzesCompleted = uiState.quizzesCompleted,
                        studyTimeMinutes = studyTime,
                        correctAnswers = uiState.learnedWords,  // Words learned = mastery progress
                        totalAnswers = if (uiState.totalWords > 0) uiState.totalWords else 1,  // Total words in dictionary
                        isLoading = uiState.isLoading,
                        modifier = Modifier.padding(horizontal = Spacing.md)
                    )
                }

                item { Spacer(modifier = Modifier.height(Spacing.lg)) }

                // Additional Info Section
                item {
                    AdditionalInfoSection(
                        currentStreak = uiState.currentStreak,
                        longestStreak = uiState.longestStreak,
                        memberSince = accountCreatedDate,
                        totalXP = uiState.totalScore,
                        modifier = Modifier.padding(horizontal = Spacing.md)
                    )
                }

                item { Spacer(modifier = Modifier.height(Spacing.lg)) }

                // Achievements Section
                item {
                    AchievementsSection(
                        achievements = uiState.unlockedAchievements,
                        onViewAll = onViewAchievements,
                        modifier = Modifier.padding(horizontal = Spacing.md)
                    )
                }

                item { Spacer(modifier = Modifier.height(Spacing.lg)) }

                // Actions/Settings Section
                item {
                    ActionsSection(
                        onEditProfile = { showEditDialog.value = true },
                        onViewLeaderboard = onViewLeaderboard,
                        onSettings = onSettings,
                        onSignIn = onSignIn,
                        onSignOut = {
                            // Show confirmation dialog instead of immediate logout (fixes #210)
                            showLogoutConfirmDialog.value = true
                        },
                        isLoggedIn = isLoggedIn,
                        modifier = Modifier.padding(horizontal = Spacing.md)
                    )
                }

                item { Spacer(modifier = Modifier.height(Spacing.lg)) }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditDialog.value) {
        EditProfileDialog(
            currentUsername = username,
            currentAvatar = currentAvatar,
            onDismiss = { showEditDialog.value = false },
            onSave = { newUsername, newAvatar ->
                // Save new username and avatar to SharedPreferences
                prefs.edit()
                    .putString("username", newUsername)
                    .putString("avatar", newAvatar)
                    .apply()
                currentAvatar = newAvatar
                showEditDialog.value = false
                onEditProfile()
            }
        )
    }

    // Avatar Selection Dialog
    if (showAvatarDialog.value) {
        AvatarSelectionDialog(
            currentAvatar = currentAvatar,
            onDismiss = { showAvatarDialog.value = false },
            onSelect = { newAvatar ->
                currentAvatar = newAvatar
                prefs.edit().putString("avatar", newAvatar).apply()
                showAvatarDialog.value = false
            }
        )
    }
}

/**
 * Hero Section - Profile header with avatar, username, level, and XP progress
 * Features gradient background and circular XP indicator
 */
@Composable
fun ProfileHeroSection(
    username: String,
    avatar: String,
    onAvatarClick: () -> Unit,
    level: Int,
    xpCurrent: Int,
    xpForNextLevel: Int,
    totalXP: Int,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    // Calculate XP progress for next level
    val xpForCurrentLevel = if (level <= 1) 0 else ((level - 1) * (level - 1) * 100)
    val xpInCurrentLevel = xpCurrent - xpForCurrentLevel
    val xpNeededForLevel = xpForNextLevel - xpForCurrentLevel
    val progress = if (xpNeededForLevel > 0) {
        (xpInCurrentLevel.toFloat() / xpNeededForLevel).coerceIn(0f, 1f)
    } else 0f

    // Animated progress
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = AnimationDuration.countUp,
            easing = AppEasing.emphasized
        ),
        label = "xpProgress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.1f),
                        secondaryColor.copy(alpha = 0.05f),
                        Color.Transparent
                    )
                )
            )
            .padding(Spacing.lg)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Large Avatar - Emoji based, clickable to change
            Box(
                modifier = Modifier
                    .size(ComponentSize.avatarLarge)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable(onClick = onAvatarClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatar,
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.padding(8.dp)
                )
                // Edit indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Change avatar",
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Username
            Text(
                text = username,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Level Badge
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(CornerRadius.large),
                shadowElevation = Elevation.level1
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "User level $level",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Level $level",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Circular XP Progress with animated count-up
            CircularProgressIndicator(
                progress = animatedProgress,
                percentage = "${(animatedProgress * 100).roundToInt()}%",
                subtitle = "$xpInCurrentLevel / $xpNeededForLevel XP",
                size = 160.dp,
                strokeWidth = 12.dp,
                animate = true
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Total XP earned
            Text(
                text = "Total XP: $totalXP",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Stats Grid Section - 2x2 grid of statistics cards with staggered animations
 * Shows: Words Learned, Quizzes Taken, Study Time, Accuracy
 */
@Composable
fun StatsGridSection(
    learnedWords: Int,
    totalWords: Int,
    quizzesCompleted: Int,
    studyTimeMinutes: Int,
    correctAnswers: Int,
    totalAnswers: Int,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Calculate responsive grid columns based on screen width
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val gridColumns = when {
        screenWidthDp >= 840 -> 4  // Large tablets/desktops
        screenWidthDp >= 600 -> 3  // Small tablets/landscape
        else -> 2                  // Phones
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = Spacing.md)
        )

        // Show loading skeleton when data is being fetched
        if (isLoading) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridColumns),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(if (gridColumns == 4) 200.dp else 350.dp),
                userScrollEnabled = false
            ) {
                items(4) {
                    StatCardSkeleton()
                }
            }
            return@Column
        }

        // Get colors in composable context
        val statsCorrectColor = MaterialTheme.colorScheme.statsCorrect
        val statsTimeColor = MaterialTheme.colorScheme.statsTime
        val statsCategoryColor = MaterialTheme.colorScheme.statsCategory
        val statsAverageColor = MaterialTheme.colorScheme.statsAverage

        // Stats data list (created with colors from composable context)
        val statsData = remember(learnedWords, quizzesCompleted, studyTimeMinutes, correctAnswers, totalAnswers,
            statsCorrectColor, statsTimeColor, statsCategoryColor, statsAverageColor) {
            listOf(
                StatData(
                    icon = Icons.Default.Book,
                    value = "$learnedWords",
                    label = "Words",
                    iconTint = statsCorrectColor
                ),
                StatData(
                    icon = Icons.Default.Quiz,
                    value = "$quizzesCompleted",
                    label = "Quizzes",
                    iconTint = statsTimeColor
                ),
                StatData(
                    icon = Icons.Default.Timer,
                    value = formatStudyTime(studyTimeMinutes),
                    label = "Study Time",
                    iconTint = statsCategoryColor
                ),
                StatData(
                    icon = Icons.Default.TrendingUp,
                    value = "${calculateAccuracy(correctAnswers, totalAnswers)}%",
                    label = "Mastery",
                    iconTint = statsAverageColor
                )
            )
        }

        // Responsive Grid with LazyVerticalGrid (2/3/4 columns based on screen size)
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridColumns),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(if (gridColumns == 4) 200.dp else 350.dp), // Adjust height for 1 or 2 rows
            userScrollEnabled = false
        ) {
            // Words Learned
            itemsIndexed(statsData) { index, stat ->
                // Staggered animation delay
                val delay = StaggerDelay.short * index
                var isVisible by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(delay.toLong())
                    isVisible = true
                }

                val offsetY by animateDpAsState(
                    targetValue = if (isVisible) 0.dp else 40.dp,
                    animationSpec = tween(
                        durationMillis = AnimationDuration.medium,
                        easing = AppEasing.emphasized
                    ),
                    label = "statsCardSlide"
                )

                val alpha by animateFloatAsState(
                    targetValue = if (isVisible) 1f else 0f,
                    animationSpec = tween(
                        durationMillis = AnimationDuration.medium,
                        easing = AppEasing.standard
                    ),
                    label = "statsCardAlpha"
                )

                Box(
                    modifier = Modifier
                        .offset(y = offsetY)
                        .graphicsLayer { this.alpha = alpha }
                ) {
                    StatsCard(
                        icon = stat.icon,
                        value = stat.value,
                        label = stat.label,
                        iconTint = stat.iconTint
                    )
                }
            }
        }
    }
}

/**
 * Data class for stat card information
 */
private data class StatData(
    val icon: ImageVector,
    val value: String,
    val label: String,
    val iconTint: Color
)

/**
 * Format study time in minutes to readable format (hours and minutes)
 */
private fun formatStudyTime(minutes: Int): String {
    return if (minutes < 60) {
        "${minutes}m"
    } else {
        val hours = minutes / 60
        val mins = minutes % 60
        if (mins == 0) "${hours}h" else "${hours}h ${mins}m"
    }
}

/**
 * Calculate accuracy percentage
 */
private fun calculateAccuracy(correct: Int, total: Int): Int {
    return if (total > 0) {
        ((correct.toFloat() / total) * 100).roundToInt()
    } else 0
}

/**
 * Additional Info Section - Streaks, Member Since, Total XP
 */
@Composable
fun AdditionalInfoSection(
    currentStreak: Int,
    longestStreak: Int,
    memberSince: Long,
    totalXP: Int,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val createdDate = dateFormat.format(Date(memberSince))

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Text(
            text = "Additional Info",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = Spacing.sm)
        )

        // Streaks Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = Elevation.level1),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Current streak: $currentStreak days",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = "$currentStreak",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Current Streak",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                VerticalDivider(
                    modifier = Modifier.height(60.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Longest streak: $longestStreak days",
                        tint = MaterialTheme.colorScheme.statsGold,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = "$longestStreak",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Longest Streak",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Member Since Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = Elevation.level1),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Member since date",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
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

/**
 * Achievements Section - Horizontal scrolling list of achievement badges
 */
@Composable
fun AchievementsSection(
    achievements: List<com.gultekinahmetabdullah.trainvoc.gamification.UserAchievement>,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Achievements",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextButton(onClick = onViewAll) {
                Text("View All")
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View all achievements",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        if (achievements.isEmpty()) {
            // Empty state
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = Elevation.level1)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "No achievements unlocked yet",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text(
                        text = "No achievements yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Keep learning to unlock achievements!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Horizontal scrolling achievements
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = achievements.take(10),
                    key = { it.id }
                ) { userAchievement ->
                    val achievement = userAchievement.getAchievement()
                    if (achievement != null) {
                        AchievementBadge(
                            title = achievement.title,
                            description = achievement.description,
                            icon = getAchievementIcon(achievement.category.name),
                            isUnlocked = userAchievement.isUnlocked,
                            modifier = Modifier.width(120.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Actions Section - Edit Profile, View Leaderboard, Settings, Sign In/Sign Out
 */
@Composable
fun ActionsSection(
    onEditProfile: () -> Unit,
    onViewLeaderboard: () -> Unit,
    onSettings: () -> Unit,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    isLoggedIn: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Text(
            text = stringResource(id = R.string.actions),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = Spacing.sm)
        )

        // Edit Profile
        ActionCard(
            icon = Icons.Default.Edit,
            title = stringResource(id = R.string.edit_profile),
            onClick = onEditProfile
        )

        // View Leaderboard
        ActionCard(
            icon = Icons.Default.Leaderboard,
            title = stringResource(id = R.string.view_leaderboard),
            onClick = onViewLeaderboard
        )

        // Settings
        ActionCard(
            icon = Icons.Default.Settings,
            title = stringResource(id = R.string.settings),
            onClick = onSettings
        )

        // Sign In or Sign Out based on auth state
        if (isLoggedIn) {
            ActionCard(
                icon = Icons.Default.Logout,
                title = stringResource(id = R.string.sign_out),
                onClick = onSignOut,
                isDestructive = true
            )
        } else {
            ActionCard(
                icon = Icons.Default.Login,
                title = stringResource(id = R.string.sign_in),
                onClick = onSignIn,
                isPrimary = true
            )
        }
    }
}

/**
 * Action Card - Reusable card for action items
 */
@Composable
private fun ActionCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
    isPrimary: Boolean = false,
    modifier: Modifier = Modifier
) {
    val iconColor = when {
        isDestructive -> MaterialTheme.colorScheme.error
        isPrimary -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.primary
    }

    val textColor = when {
        isDestructive -> MaterialTheme.colorScheme.error
        isPrimary -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    val containerColor = when {
        isPrimary -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.level1),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go",
                tint = if (isDestructive) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

/**
 * Helper function to get achievement icon based on category
 */
private fun getAchievementIcon(category: String): ImageVector {
    return when (category) {
        "STREAK" -> Icons.Default.LocalFireDepartment
        "WORDS" -> Icons.Default.Book
        "QUIZ" -> Icons.Default.Quiz
        "PERFECT" -> Icons.Default.Star
        "GOALS" -> Icons.Default.Flag
        "REVIEW" -> Icons.Default.Replay
        "TIME" -> Icons.Default.Timer
        "SPECIAL" -> Icons.Default.EmojiEvents
        else -> Icons.Default.Star
    }
}

@Composable
fun EditProfileDialog(
    currentUsername: String,
    currentAvatar: String = "\uD83E\uDD8A",
    onDismiss: () -> Unit,
    onSave: (username: String, avatar: String) -> Unit
) {
    var newUsername by remember { mutableStateOf(currentUsername) }
    var newAvatar by remember { mutableStateOf(currentAvatar) }
    var error by remember { mutableStateOf<String?>(null) }
    var showAvatarPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar Section
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { showAvatarPicker = !showAvatarPicker },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = newAvatar,
                        style = MaterialTheme.typography.displaySmall
                    )
                    // Edit indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Change avatar",
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(
                    text = "Tap to change avatar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Avatar picker grid (collapsible)
                if (showAvatarPicker) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.height(180.dp)
                    ) {
                        items(com.gultekinahmetabdullah.trainvoc.constants.Avatars.AVATAR_LIST.size) { index ->
                            val avatar = com.gultekinahmetabdullah.trainvoc.constants.Avatars.AVATAR_LIST[index]
                            val isSelected = avatar == newAvatar

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable {
                                        newAvatar = avatar
                                        showAvatarPicker = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = avatar,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }
                }

                // Username Field
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
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    isError = error != null,
                    supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (error == null && newUsername.isNotBlank()) {
                        onSave(newUsername, newAvatar)
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

/**
 * Avatar Selection Dialog - Grid of emoji avatars to choose from
 */
@Composable
fun AvatarSelectionDialog(
    currentAvatar: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Avatar") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(280.dp)
            ) {
                items(com.gultekinahmetabdullah.trainvoc.constants.Avatars.AVATAR_LIST.size) { index ->
                    val avatar = com.gultekinahmetabdullah.trainvoc.constants.Avatars.AVATAR_LIST[index]
                    val isSelected = avatar == currentAvatar

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { onSelect(avatar) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = avatar,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

/**
 * Skeleton placeholder for stat cards during loading
 */
@Composable
private fun StatCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(CornerRadius.medium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon placeholder
            ShimmerBox(
                modifier = Modifier.size(40.dp),
                shape = CircleShape
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Value placeholder
            ShimmerBox(
                modifier = Modifier
                    .width(60.dp)
                    .height(28.dp),
                shape = RoundedCornerShape(4.dp)
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Label placeholder
            ShimmerBox(
                modifier = Modifier
                    .width(40.dp)
                    .height(14.dp),
                shape = RoundedCornerShape(4.dp)
            )
        }
    }
}
