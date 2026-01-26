package com.gultekinahmetabdullah.trainvoc.ui.screen.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.QuizScreenExitHandler
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import kotlinx.coroutines.launch

/**
 * Main app top bar with navigation and menu actions.
 * Extracted from MainScreen for better maintainability.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navBackStackEntry: NavBackStackEntry?,
    navController: NavController,
    onMenuClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier.padding(start = Spacing.extraSmall)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_generating_tokens_24),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                        MaterialTheme.colorScheme.primary
                    ),
                    contentDescription = stringResource(id = R.string.app_name) + " logo",
                    modifier = Modifier
                        .height(36.dp)
                        .padding(end = 10.dp)
                )
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.secondary
        ),
        navigationIcon = {
            val currentRoute = navBackStackEntry?.destination?.route

            // Routes that need back navigation instead of menu
            val needsBackNavigation = currentRoute in listOf(
                Route.QUIZ,
                Route.QUIZ_MENU,
                Route.QUIZ_EXAM_MENU,
                Route.STORY,
                Route.WORD_DETAIL
            ) || currentRoute?.startsWith("word_detail/") == true

            if (needsBackNavigation) {
                // Show back arrow for nested screens
                IconButton(onClick = {
                    if (currentRoute == Route.QUIZ) {
                        QuizScreenExitHandler.triggerExit()
                    } else {
                        navController.popBackStack()
                    }
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                // Show hamburger menu for drawer access (standard Material Design)
                IconButton(onClick = onMenuClick) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Open menu",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        actions = {
            // Keep actions area clean - can add search/notifications later
        }
    )
}
