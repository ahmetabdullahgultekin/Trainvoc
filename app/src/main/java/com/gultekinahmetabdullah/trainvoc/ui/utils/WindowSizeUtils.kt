package com.gultekinahmetabdullah.trainvoc.ui.utils

import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/**
 * Window Size Utilities
 *
 * Provides utilities for responsive design based on screen size.
 * Supports tablets, foldables, and different device orientations.
 */

/**
 * Calculate window size class
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val activity = LocalContext.current as? Activity
    return if (activity != null) {
        calculateWindowSizeClass(activity)
    } else {
        // Fallback to manual calculation
        val configuration = LocalConfiguration.current
        val size = DpSize(
            width = configuration.screenWidthDp.dp,
            height = configuration.screenHeightDp.dp
        )
        WindowSizeClass.calculateFromSize(size)
    }
}

/**
 * Check if device is tablet (based on width)
 */
@Composable
fun isTablet(): Boolean {
    val windowSize = rememberWindowSizeClass()
    return windowSize.widthSizeClass >= WindowWidthSizeClass.Medium
}

/**
 * Check if device is in compact mode (phone)
 */
@Composable
fun isCompact(): Boolean {
    val windowSize = rememberWindowSizeClass()
    return windowSize.widthSizeClass == WindowWidthSizeClass.Compact
}

/**
 * Check if device is in expanded mode (large tablet/desktop)
 */
@Composable
fun isExpanded(): Boolean {
    val windowSize = rememberWindowSizeClass()
    return windowSize.widthSizeClass == WindowWidthSizeClass.Expanded
}

/**
 * Get number of columns for grid based on window size
 */
@Composable
fun getAdaptiveColumnCount(
    compact: Int = 2,
    medium: Int = 3,
    expanded: Int = 4
): Int {
    val windowSize = rememberWindowSizeClass()
    return when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> compact
        WindowWidthSizeClass.Medium -> medium
        WindowWidthSizeClass.Expanded -> expanded
        else -> compact
    }
}

/**
 * Get adaptive padding based on window size
 */
@Composable
fun getAdaptivePadding(
    compact: androidx.compose.ui.unit.Dp = 16.dp,
    medium: androidx.compose.ui.unit.Dp = 24.dp,
    expanded: androidx.compose.ui.unit.Dp = 32.dp
): androidx.compose.ui.unit.Dp {
    val windowSize = rememberWindowSizeClass()
    return when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> compact
        WindowWidthSizeClass.Medium -> medium
        WindowWidthSizeClass.Expanded -> expanded
        else -> compact
    }
}

/**
 * Check if device should use two-pane layout
 */
@Composable
fun shouldUseTwoPaneLayout(): Boolean {
    val windowSize = rememberWindowSizeClass()
    return windowSize.widthSizeClass >= WindowWidthSizeClass.Medium
}

/**
 * Check if device is in landscape orientation
 */
@Composable
fun isLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp > configuration.screenHeightDp
}

/**
 * Get adaptive font scale
 */
@Composable
fun getAdaptiveFontScale(): Float {
    val configuration = LocalConfiguration.current
    val windowSize = rememberWindowSizeClass()

    // Check if user has large text enabled
    val userFontScale = configuration.fontScale

    // Adjust for window size
    val windowScale = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Expanded -> 1.1f
        WindowWidthSizeClass.Medium -> 1.05f
        else -> 1f
    }

    return remember(userFontScale, windowScale) {
        (userFontScale * windowScale).coerceIn(0.8f, 2f)
    }
}

/**
 * Responsive Layout Type
 */
enum class LayoutType {
    COMPACT,        // Phone portrait
    MEDIUM,         // Phone landscape, small tablet
    EXPANDED,       // Large tablet, desktop
    TWO_PANE       // Tablet/desktop with two-pane layout
}

/**
 * Get current layout type
 */
@Composable
fun getLayoutType(): LayoutType {
    val windowSize = rememberWindowSizeClass()

    return when {
        windowSize.widthSizeClass == WindowWidthSizeClass.Expanded -> LayoutType.EXPANDED
        windowSize.widthSizeClass == WindowWidthSizeClass.Medium &&
            windowSize.heightSizeClass != WindowHeightSizeClass.Compact -> LayoutType.TWO_PANE
        windowSize.widthSizeClass == WindowWidthSizeClass.Medium -> LayoutType.MEDIUM
        else -> LayoutType.COMPACT
    }
}

/**
 * Get maximum content width for better readability on large screens
 */
@Composable
fun getMaxContentWidth(): androidx.compose.ui.unit.Dp {
    val windowSize = rememberWindowSizeClass()
    return when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Expanded -> 840.dp // Material Design recommendation
        WindowWidthSizeClass.Medium -> 720.dp
        else -> androidx.compose.ui.unit.Dp.Infinity
    }
}
