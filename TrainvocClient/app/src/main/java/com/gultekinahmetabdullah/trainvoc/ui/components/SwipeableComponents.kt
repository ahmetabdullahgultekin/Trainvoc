package com.gultekinahmetabdullah.trainvoc.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.gultekinahmetabdullah.trainvoc.R
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.ui.animations.rememberHapticPerformer
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing

/**
 * Swipeable Components
 *
 * Modern swipe gesture components for enhanced UX.
 */

// =============================================================================
// SWIPE TO DELETE / ACTION
// =============================================================================

/**
 * SwipeToDeleteCard - Card that can be swiped to delete
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteCard(
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val haptic = rememberHapticPerformer()
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                haptic.error()
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = enabled,
        backgroundContent = {
            SwipeBackground(
                dismissState = dismissState,
                icon = Icons.Default.Delete,
                backgroundColor = MaterialTheme.colorScheme.errorContainer,
                iconTint = MaterialTheme.colorScheme.error
            )
        },
        content = { content() }
    )
}

/**
 * SwipeToFavoriteCard - Card that can be swiped to favorite/unfavorite
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToFavoriteCard(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val haptic = rememberHapticPerformer()
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd) {
                haptic.success()
                onToggleFavorite()
                false // Don't dismiss, just toggle
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = enabled,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            SwipeFavoriteBackground(
                dismissState = dismissState,
                isFavorite = isFavorite
            )
        },
        content = { content() }
    )
}

/**
 * SwipeBackground for delete action
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(
    dismissState: SwipeToDismissBoxState,
    icon: ImageVector,
    backgroundColor: Color,
    iconTint: Color
) {
    val scale by animateFloatAsState(
        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.2f else 0.8f,
        animationSpec = tween(200),
        label = "icon_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(CornerRadius.medium))
            .background(backgroundColor)
            .padding(horizontal = Spacing.large),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(id = R.string.content_desc_delete),
            tint = iconTint,
            modifier = Modifier.scale(scale)
        )
    }
}

/**
 * SwipeBackground for favorite action
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeFavoriteBackground(
    dismissState: SwipeToDismissBoxState,
    isFavorite: Boolean
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isFavorite)
            MaterialTheme.colorScheme.surfaceVariant
        else
            MaterialTheme.colorScheme.primaryContainer,
        label = "bg_color"
    )

    val scale by animateFloatAsState(
        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) 1.2f else 0.8f,
        animationSpec = tween(200),
        label = "icon_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(CornerRadius.medium))
            .background(backgroundColor)
            .padding(horizontal = Spacing.large),
        contentAlignment = Alignment.CenterStart
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.FavoriteBorder else Icons.Default.Favorite,
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            tint = if (isFavorite)
                MaterialTheme.colorScheme.onSurfaceVariant
            else
                MaterialTheme.colorScheme.primary,
            modifier = Modifier.scale(scale)
        )
    }
}

// =============================================================================
// PULL TO REFRESH
// =============================================================================

/**
 * PullToRefreshContainer - Container with pull-to-refresh functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val haptic = rememberHapticPerformer()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            haptic.click()
            onRefresh()
        },
        modifier = modifier,
        content = content
    )
}
