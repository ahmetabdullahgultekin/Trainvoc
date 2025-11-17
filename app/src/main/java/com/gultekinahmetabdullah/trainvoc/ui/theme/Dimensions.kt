package com.gultekinahmetabdullah.trainvoc.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Design token system for consistent spacing throughout the application.
 * Following Material Design 3 spacing scale (4dp base unit).
 */
object Spacing {
    val none: Dp = 0.dp
    val extraSmall: Dp = 4.dp
    val small: Dp = 8.dp
    val medium: Dp = 12.dp
    val mediumLarge: Dp = 16.dp
    val large: Dp = 24.dp
    val extraLarge: Dp = 32.dp
    val huge: Dp = 48.dp
}

/**
 * Corner radius tokens for consistent rounded corners.
 */
object CornerRadius {
    val extraSmall: Dp = 4.dp
    val small: Dp = 8.dp
    val medium: Dp = 12.dp
    val large: Dp = 16.dp
    val extraLarge: Dp = 24.dp
    val round: Dp = 28.dp
}

/**
 * Alpha (transparency) values following Material Design standards.
 */
object Alpha {
    const val disabled = 0.38f        // Material Design standard for disabled states
    const val medium = 0.60f          // Semi-transparent overlays
    const val high = 0.87f            // Material Design standard for high emphasis text
    const val full = 1.0f             // Completely opaque

    // Surface variants
    const val surfaceVariant = 0.08f  // Subtle surface tints
    const val surfaceLight = 0.12f    // Light surface overlays
    const val surfaceMedium = 0.18f   // Medium surface overlays
}

/**
 * Icon and component size tokens.
 */
object IconSize {
    val small: Dp = 16.dp
    val medium: Dp = 24.dp
    val large: Dp = 32.dp
    val extraLarge: Dp = 48.dp
}

/**
 * Button and component size tokens.
 */
object ComponentSize {
    val buttonHeight: Dp = 48.dp
    val buttonWidth: Dp = 110.dp
    val cardElevation: Dp = 4.dp
    val minTouchTarget: Dp = 48.dp    // Material Design minimum touch target
}
