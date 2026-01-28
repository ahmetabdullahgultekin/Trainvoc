package com.gultekinahmetabdullah.trainvoc.ui.screen.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gultekinahmetabdullah.trainvoc.BuildConfig
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.QuizScreenExitHandler
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.viewmodel.GamificationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * App navigation drawer with polished design and user profile.
 * Features: User streak display, section headers, and comprehensive menu.
 */
@Composable
fun AppNavigationDrawerContent(
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    navController: NavController,
    currentRoute: String?,
    gamificationViewModel: GamificationViewModel = hiltViewModel()
) {
    val streakData by gamificationViewModel.streakData.collectAsState()
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("trainvoc_prefs", android.content.Context.MODE_PRIVATE) }
    val username = remember { prefs.getString("username", null) ?: "User" }
    val userAvatar = remember { prefs.getString("avatar", null) ?: "🦊" }

    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // Header with gradient and user info (fixes #195)
            DrawerHeader(
                username = username,
                avatar = userAvatar,
                currentStreak = streakData?.currentStreak ?: 0,
                onClose = { coroutineScope.launch { drawerState.close() } }
            )

            Spacer(modifier = Modifier.height(Spacing.small))

            // Debug banner - only in debug builds
            if (BuildConfig.DEBUG) {
                Surface(
                    modifier = Modifier
                        .padding(horizontal = Spacing.medium)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Debug Mode",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(Spacing.small)
                    )
                }
                Spacer(modifier = Modifier.height(Spacing.small))
            }

            // Learning Section
            DrawerSectionHeader(title = stringResource(R.string.learning))
            DrawerMenuItem(
                text = stringResource(R.string.home),
                icon = Icons.Default.Home,
                route = Route.HOME,
                currentRoute = currentRoute,
                navController = navController,
                drawerState = drawerState,
                coroutineScope = coroutineScope
            )
            DrawerMenuItem(
                text = stringResource(R.string.story_mode),
                icon = Icons.Default.Star,
                route = Route.STORY,
                currentRoute = currentRoute,
                navController = navController,
                drawerState = drawerState,
                coroutineScope = coroutineScope
            )
            DrawerMenuItem(
                text = stringResource(R.string.games),
                icon = Icons.Default.Gamepad,
                route = Route.GAMES_MENU,
                currentRoute = currentRoute,
                navController = navController,
                drawerState = drawerState,
                coroutineScope = coroutineScope
            )
            DrawerMenuItem(
                text = stringResource(R.string.search_word),
                icon = Icons.Default.Search,
                route = Route.DICTIONARY,
                currentRoute = currentRoute,
                navController = navController,
                drawerState = drawerState,
                coroutineScope = coroutineScope
            )

            DrawerDivider()

            // Progress Section
            DrawerSectionHeader(title = stringResource(R.string.progress))
            DrawerMenuItem(
                text = stringResource(R.string.stats),
                icon = Icons.Default.Timeline,
                route = Route.STATS,
                currentRoute = currentRoute,
                navController = navController,
                drawerState = drawerState,
                coroutineScope = coroutineScope
            )
            DrawerMenuItem(
                text = stringResource(R.string.achievements),
                icon = Icons.Default.EmojiEvents,
                route = Route.ACHIEVEMENTS,
                currentRoute = currentRoute,
                navController = navController,
                drawerState = drawerState,
                coroutineScope = coroutineScope
            )
            DrawerMenuItem(
                text = stringResource(R.string.daily_goals),
                icon = Icons.Default.TrackChanges,
                route = Route.DAILY_GOALS,
                currentRoute = currentRoute,
                navController = navController,
                drawerState = drawerState,
                coroutineScope = coroutineScope
            )
            DrawerMenuItem(
                text = stringResource(R.string.leaderboard),
                icon = Icons.Default.Leaderboard,
                route = Route.LEADERBOARD,
                currentRoute = currentRoute,
                navController = navController,
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                badge = stringResource(R.string.coming_soon)
            )

            DrawerDivider()

            // Social Section
            DrawerSectionHeader(title = stringResource(R.string.social))
            DrawerMenuItem(
                text = stringResource(R.string.multiplayer),
                icon = Icons.Default.Groups,
                route = Route.MULTIPLAYER_HOME,
                currentRoute = currentRoute,
                navController = navController,
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                badge = stringResource(R.string.coming_soon)
            )

            DrawerDivider()

            // Management Section
            DrawerSectionHeader(title = stringResource(R.string.management))
            DrawerMenuItem(
                text = stringResource(R.string.manage_words),
                icon = Icons.Default.Folder,
                route = Route.MANAGEMENT,
                currentRoute = currentRoute,
                navController = navController,
                drawerState = drawerState,
                coroutineScope = coroutineScope
            )
            DrawerMenuItem(
                text = stringResource(R.string.settings),
                icon = Icons.Default.Settings,
                route = Route.SETTINGS,
                currentRoute = currentRoute,
                navController = navController,
                drawerState = drawerState,
                coroutineScope = coroutineScope
            )

            DrawerDivider()

            // Help Section
            DrawerSectionHeader(title = stringResource(R.string.support))
            DrawerMenuItem(
                text = stringResource(R.string.help),
                icon = Icons.AutoMirrored.Filled.Help,
                route = Route.HELP,
                currentRoute = currentRoute,
                navController = navController,
                drawerState = drawerState,
                coroutineScope = coroutineScope
            )
            DrawerMenuItem(
                text = stringResource(R.string.about),
                icon = Icons.Default.Info,
                route = Route.ABOUT,
                currentRoute = currentRoute,
                navController = navController,
                drawerState = drawerState,
                coroutineScope = coroutineScope
            )

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            DrawerFooter()
        }
    }
}

@Composable
private fun DrawerHeader(
    username: String,
    avatar: String,
    currentStreak: Int,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    )
                )
            )
            .padding(Spacing.medium)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // App name and branding
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = stringResource(R.string.app_tagline),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            // User profile section (fixes #195)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = avatar,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.width(Spacing.small))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = username,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // Streak display inline
                if (currentStreak > 0) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = Spacing.small,
                                vertical = 2.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.streak_days, currentStreak),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(
            start = Spacing.large,
            end = Spacing.medium,
            top = Spacing.medium,
            bottom = Spacing.extraSmall
        )
    )
}

@Composable
private fun DrawerMenuItem(
    text: String,
    icon: ImageVector,
    route: String,
    currentRoute: String?,
    navController: NavController,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    badge: String? = null
) {
    val selected = currentRoute == route

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
        },
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (badge != null) {
                    Spacer(modifier = Modifier.width(Spacing.small))
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(
                                horizontal = Spacing.small,
                                vertical = 2.dp
                            )
                        )
                    }
                }
            }
        },
        selected = selected,
        onClick = {
            coroutineScope.launch {
                drawerState.close()
            }.invokeOnCompletion {
                // Exit handler for quiz screen
                if (navController.currentBackStackEntry?.destination?.route == Route.QUIZ) {
                    QuizScreenExitHandler.triggerExit()
                } else if (route != currentRoute) {
                    navController.navigate(route) {
                        popUpTo(Route.HOME) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun DrawerDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(
            horizontal = Spacing.medium,
            vertical = Spacing.extraSmall
        ),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

@Composable
private fun DrawerFooter() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = Spacing.medium),
        color = MaterialTheme.colorScheme.outlineVariant
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "v${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
