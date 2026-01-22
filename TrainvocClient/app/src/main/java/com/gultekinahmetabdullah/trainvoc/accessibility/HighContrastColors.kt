package com.gultekinahmetabdullah.trainvoc.accessibility

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * High Contrast Colors
 *
 * Provides high contrast color schemes for users with visual impairments.
 * Follows WCAG AAA accessibility guidelines with contrast ratios of at least 7:1.
 */

object HighContrastColors {
    // High contrast light theme colors
    val lightPrimary = Color(0xFF000000)           // Pure black
    val lightOnPrimary = Color(0xFFFFFFFF)         // Pure white
    val lightSecondary = Color(0xFF0066CC)         // Strong blue
    val lightOnSecondary = Color(0xFFFFFFFF)       // Pure white
    val lightBackground = Color(0xFFFFFFFF)        // Pure white
    val lightOnBackground = Color(0xFF000000)      // Pure black
    val lightSurface = Color(0xFFF5F5F5)           // Light gray
    val lightOnSurface = Color(0xFF000000)         // Pure black
    val lightError = Color(0xFFCC0000)             // Strong red
    val lightOnError = Color(0xFFFFFFFF)           // Pure white

    // High contrast dark theme colors
    val darkPrimary = Color(0xFFFFFFFF)            // Pure white
    val darkOnPrimary = Color(0xFF000000)          // Pure black
    val darkSecondary = Color(0xFF66B2FF)          // Light blue
    val darkOnSecondary = Color(0xFF000000)        // Pure black
    val darkBackground = Color(0xFF000000)         // Pure black
    val darkOnBackground = Color(0xFFFFFFFF)       // Pure white
    val darkSurface = Color(0xFF1A1A1A)            // Dark gray
    val darkOnSurface = Color(0xFFFFFFFF)          // Pure white
    val darkError = Color(0xFFFF6666)              // Light red
    val darkOnError = Color(0xFF000000)            // Pure black
}

/**
 * High contrast light color scheme
 */
val HighContrastLightColorScheme = lightColorScheme(
    primary = HighContrastColors.lightPrimary,
    onPrimary = HighContrastColors.lightOnPrimary,
    secondary = HighContrastColors.lightSecondary,
    onSecondary = HighContrastColors.lightOnSecondary,
    tertiary = HighContrastColors.lightSecondary,
    onTertiary = HighContrastColors.lightOnSecondary,
    background = HighContrastColors.lightBackground,
    onBackground = HighContrastColors.lightOnBackground,
    surface = HighContrastColors.lightSurface,
    onSurface = HighContrastColors.lightOnSurface,
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = HighContrastColors.lightOnSurface,
    error = HighContrastColors.lightError,
    onError = HighContrastColors.lightOnError,
    outline = Color(0xFF000000),
    outlineVariant = Color(0xFF666666)
)

/**
 * High contrast dark color scheme
 */
val HighContrastDarkColorScheme = darkColorScheme(
    primary = HighContrastColors.darkPrimary,
    onPrimary = HighContrastColors.darkOnPrimary,
    secondary = HighContrastColors.darkSecondary,
    onSecondary = HighContrastColors.darkOnSecondary,
    tertiary = HighContrastColors.darkSecondary,
    onTertiary = HighContrastColors.darkOnSecondary,
    background = HighContrastColors.darkBackground,
    onBackground = HighContrastColors.darkOnBackground,
    surface = HighContrastColors.darkSurface,
    onSurface = HighContrastColors.darkOnSurface,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = HighContrastColors.darkOnSurface,
    error = HighContrastColors.darkError,
    onError = HighContrastColors.darkOnError,
    outline = Color(0xFFFFFFFF),
    outlineVariant = Color(0xFF999999)
)

/**
 * Get high contrast color scheme based on dark theme preference
 */
fun getHighContrastColorScheme(darkTheme: Boolean): ColorScheme {
    return if (darkTheme) {
        HighContrastDarkColorScheme
    } else {
        HighContrastLightColorScheme
    }
}

/**
 * Color blind friendly palette
 * Uses colors that are distinguishable for most types of color blindness
 */
object ColorBlindFriendlyColors {
    // These colors work well for protanopia, deuteranopia, and tritanopia
    val blue = Color(0xFF0173B2)          // Safe blue
    val orange = Color(0xFFDE8F05)        // Safe orange
    val green = Color(0xFF029E73)         // Safe green (blue-green)
    val yellow = Color(0xFFECE133)        // Safe yellow
    val red = Color(0xFFCC78BC)           // Safe pink/magenta
    val brown = Color(0xFFCA9161)         // Safe brown
    val purple = Color(0xFF949494)        // Safe gray/purple
    val black = Color(0xFF000000)         // Black
    val white = Color(0xFFFFFFFF)         // White
}

/**
 * Apply color blind friendly colors to stats
 */
fun getColorBlindFriendlyStatsColors(): Map<String, Color> {
    return mapOf(
        "correct" to ColorBlindFriendlyColors.green,
        "incorrect" to ColorBlindFriendlyColors.red,
        "skipped" to ColorBlindFriendlyColors.yellow,
        "achievement" to ColorBlindFriendlyColors.blue,
        "time" to ColorBlindFriendlyColors.orange,
        "average" to ColorBlindFriendlyColors.purple,
        "quiz" to ColorBlindFriendlyColors.brown
    )
}
