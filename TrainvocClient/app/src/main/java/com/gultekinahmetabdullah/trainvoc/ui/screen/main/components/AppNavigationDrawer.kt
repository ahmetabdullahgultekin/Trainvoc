package com.gultekinahmetabdullah.trainvoc.ui.screen.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gultekinahmetabdullah.trainvoc.BuildConfig
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.QuizScreenExitHandler
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * App navigation drawer with menu options.
 * Replaces the bottom sheet for better navigation UX.
 */
@Composable
fun AppNavigationDrawerContent(
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    navController: NavController,
    currentRoute: String?
) {
    ModalDrawerSheet(
        modifier = Modifier.width(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = Spacing.medium, vertical = Spacing.large),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall.copy(letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    coroutineScope.launch { drawerState.close() }
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.close),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.small))

            // Debug banner - only in debug builds
            if (BuildConfig.DEBUG) {
                Text(
                    text = "Test Mode - For development use only",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(horizontal = Spacing.medium, vertical = Spacing.small)
                        .background(
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(Spacing.small)
                )
                Spacer(modifier = Modifier.height(Spacing.small))
            }

            // Menu items
            val menuItems = getDrawerMenuItems()

            menuItems.forEachIndexed { index, (text, icon, route) ->
                val selected = currentRoute == route

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = text
                        )
                    },
                    label = {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge
                        )
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
                                    // Pop up to home to avoid building up a large back stack
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
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                // Add divider after certain sections
                if (index == 0 || index == 4) {
                    Spacer(modifier = Modifier.height(Spacing.extraSmall))
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = Spacing.medium),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Spacer(modifier = Modifier.height(Spacing.extraSmall))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer with app version
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = Spacing.medium),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Text(
                text = "v${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(Spacing.medium)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

/**
 * Menu items configuration for the navigation drawer.
 */
@Composable
private fun getDrawerMenuItems(): List<Triple<String, ImageVector, String>> {
    return listOf(
        Triple(stringResource(id = R.string.home), Icons.Default.Home, Route.HOME),
        Triple(
            stringResource(id = R.string.story_mode),
            Icons.Default.Star,
            Route.STORY
        ),
        Triple(
            stringResource(id = R.string.stats),
            Icons.Default.CheckCircle,
            Route.STATS
        ),
        Triple(
            stringResource(id = R.string.search_word),
            Icons.Default.Search,
            Route.DICTIONARY
        ),
        Triple(
            stringResource(id = R.string.manage_words),
            Icons.Default.Build,
            Route.MANAGEMENT
        ),
        Triple(
            stringResource(id = R.string.settings),
            Icons.Default.Settings,
            Route.SETTINGS
        ),
        Triple(stringResource(id = R.string.help), Icons.Default.Info, Route.HELP),
        Triple(
            stringResource(id = R.string.about),
            Icons.Default.AccountCircle,
            Route.ABOUT
        ),
    )
}
