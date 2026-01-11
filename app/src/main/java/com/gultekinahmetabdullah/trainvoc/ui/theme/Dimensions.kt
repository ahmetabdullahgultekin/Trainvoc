package com.gultekinahmetabdullah.trainvoc.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Design token system for consistent spacing throughout the application.
 * Following Material Design 3 spacing scale (4dp base unit) from UI/UX Plan.
 */
object Spacing {
    val none: Dp = 0.dp
    val xs: Dp = 4.dp       // Extra small - tight spacing within components
    val sm: Dp = 8.dp       // Small - gaps between related items
    val md: Dp = 16.dp      // Medium - standard spacing (most common)
    val lg: Dp = 24.dp      // Large - spacing between sections
    val xl: Dp = 32.dp      // Extra large - major sections
    val xxl: Dp = 48.dp     // Screen padding, major separations

    // Aliases for backward compatibility
    val extraSmall: Dp = xs
    val small: Dp = sm
    val medium: Dp = 12.dp
    val mediumLarge: Dp = md
    val large: Dp = lg
    val extraLarge: Dp = xl
    val huge: Dp = xxl
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
 * Following Material Design minimum touch target (48dp) for accessibility.
 */
object ComponentSize {
    val buttonHeight: Dp = 48.dp
    val buttonHeightSmall: Dp = 40.dp
    val buttonWidth: Dp = 110.dp
    val cardElevation: Dp = 4.dp
    val minTouchTarget: Dp = 48.dp    // Material Design minimum touch target

    // Additional component sizes from UI/UX Plan
    val progressBarHeight: Dp = 4.dp
    val circularProgressSize: Dp = 200.dp
    val circularProgressStroke: Dp = 16.dp
    val avatarSmall: Dp = 40.dp
    val avatarMedium: Dp = 56.dp
    val avatarLarge: Dp = 80.dp
    val wordCardHeight: Dp = 96.dp
    val gameCardHeight: Dp = 120.dp
    val imageCardHeight: Dp = 180.dp
}

/**
 * Animation duration tokens for consistent timing across the application.
 * All durations in milliseconds.
 * Following Material Motion specifications from UI/UX Plan.
 */
object AnimationDuration {
    // Material Motion durations (from plan)
    const val instant = 100           // State changes, toggles
    const val quick = 200             // Button presses, small transitions (was fast)
    const val medium = 300            // Screen transitions, card animations (was normal)
    const val slow = 500              // Large movements, page transitions (was progress)
    const val gentle = 700            // Celebration animations
    const val countUp = 1000          // Number count-up animations (was buttonPulse)

    // Specific use cases
    const val answerFeedback = 400    // Answer card color/scale animations
    const val statCard = 600          // Stat card entrance animations
    const val screenInit = 2000       // Initial screen setup animations
    const val backgroundSlow = 40000  // Slow background gradient animation
    const val backgroundOffset = 12000 // Offset for secondary background gradient

    // Aliases for backward compatibility
    const val fast = quick
    const val normal = medium
    const val progress = slow
    const val buttonPulse = countUp
}

/**
 * Elevation tokens for consistent shadow/depth throughout the application.
 * Following Material Design 3 elevation scale from UI/UX Plan.
 */
object Elevation {
    val level0: Dp = 0.dp    // No elevation (flush with surface)
    val level1: Dp = 1.dp    // Cards, chips (subtle)
    val level2: Dp = 3.dp    // FABs, cards on hover
    val level3: Dp = 6.dp    // Dialogs, menus, bottom sheets
    val level4: Dp = 8.dp    // Navigation drawer
    val level5: Dp = 12.dp   // Top app bar (elevated)

    // Aliases for backward compatibility
    val none: Dp = level0
    val extraLow: Dp = level1
    val low: Dp = level2
    val medium: Dp = 4.dp
    val high: Dp = level4
    val veryHigh: Dp = level5
    val extreme: Dp = 16.dp
}
