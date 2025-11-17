package com.gultekinahmetabdullah.trainvoc.ui.screen.main.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route

/**
 * Bottom navigation bar for main navigation.
 * Extracted from MainScreen for better reusability.
 */
@Composable
fun AppBottomBar(navController: NavController) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = stringResource(id = R.string.home)
                )
            },
            label = { Text(stringResource(id = R.string.home)) },
            selected = navController.currentDestination?.route == Route.HOME,
            onClick = { navController.navigate(Route.HOME) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Build,
                    contentDescription = stringResource(id = R.string.quiz)
                )
            },
            label = { Text(stringResource(id = R.string.quiz)) },
            selected = navController.currentDestination?.route == Route.QUIZ,
            onClick = { navController.navigate(Route.QUIZ) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Star,
                    contentDescription = stringResource(id = R.string.stats)
                )
            },
            label = { Text(stringResource(id = R.string.stats)) },
            selected = navController.currentDestination?.route == Route.STATS,
            onClick = { navController.navigate(Route.STATS) }
        )
    }
}
