package com.gultekinahmetabdullah.trainvoc.ui.screen.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.QuizScreenExitHandler
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * App navigation bottom sheet with menu options.
 * Extracted from MainScreen for better organization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheet(
    sheetState: SheetState,
    coroutineScope: CoroutineScope,
    showBottomSheet: MutableState<Boolean>,
    navController: NavController
) {
    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(
            topStart = CornerRadius.round,
            topEnd = CornerRadius.round
        ),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.small, vertical = Spacing.small)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.small, vertical = Spacing.small),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.more_options),
                        style = MaterialTheme.typography.headlineSmall.copy(letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet.value = false
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.close),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.extraSmall))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(Spacing.extraSmall))

                // Menu items list
                val menuItems = getMenuItems()

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Text(
                            text = "Test Mode - For development use only",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .padding(Spacing.small)
                                .background(
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(Spacing.medium),
                            fontSize = 18.sp
                        )
                    }
                    items(
                        count = menuItems.size,
                        key = { index -> menuItems[index].third }
                    ) { index ->
                        val (text, icon, route) = menuItems[index]
                        ListItem(
                            headlineContent = {
                                Text(text, style = MaterialTheme.typography.bodyLarge)
                            },
                            leadingContent = {
                                Icon(
                                    icon,
                                    contentDescription = text,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showBottomSheet.value = false
                                            // Exit handler for quiz screen
                                            if (navController.currentBackStackEntry?.destination?.route == Route.QUIZ) {
                                                QuizScreenExitHandler.triggerExit()
                                            } else {
                                                navController.navigate(route)
                                            }
                                        }
                                    }
                                }
                                .padding(vertical = 2.dp, horizontal = 2.dp)
                        )
                        if (index < menuItems.size - 1) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                thickness = 1.dp,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))
                Button(
                    onClick = {
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet.value = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .padding(horizontal = Spacing.small, vertical = Spacing.extraSmall)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(CornerRadius.large),
                            ambientColor = MaterialTheme.colorScheme.error,
                            spotColor = MaterialTheme.colorScheme.error,
                            clip = false
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.close),
                        tint = Color.White,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.close), color = Color.White
                    )
                }
            }
        },
        onDismissRequest = {
            coroutineScope.launch {
                showBottomSheet.value = false
            }
        }
    )
}

/**
 * Menu items configuration for the bottom sheet.
 */
@Composable
private fun getMenuItems(): List<Triple<String, ImageVector, String>> {
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
