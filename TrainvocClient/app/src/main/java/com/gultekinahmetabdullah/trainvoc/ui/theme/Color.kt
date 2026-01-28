package com.gultekinahmetabdullah.trainvoc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// ============================================================
// Material 3 Design System Colors (Primary Theme)
// Following the UI/UX Improvement Plan
// ============================================================

// Primary Colors (Brand Identity - Learning & Growth)
val Primary = Color(0xFF6750A4)          // Deep purple - intelligence, learning
val OnPrimary = Color(0xFFFFFFFF)        // White text on primary
val PrimaryContainer = Color(0xFFEADDFF) // Light purple - highlights
val OnPrimaryContainer = Color(0xFF21005D)

// Secondary Colors (Engagement & Energy)
val Secondary = Color(0xFF625B71)        // Muted purple-grey
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFE8DEF8)
val OnSecondaryContainer = Color(0xFF1D192B)

// Tertiary Colors (Success & Achievement)
val Tertiary = Color(0xFF7D5260)         // Warm accent
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFFFFD8E4)
val OnTertiaryContainer = Color(0xFF31111D)

// Semantic Colors
val Success = Color(0xFF4CAF50)          // Green - correct answers, achievements
val OnSuccess = Color(0xFFFFFFFF)
val Error = Color(0xFFB3261E)            // Red - incorrect answers, warnings
val OnError = Color(0xFFFFFFFF)
val Warning = Color(0xFFF9A825)          // Amber - caution, time warnings
val Info = Color(0xFF2196F3)             // Blue - informational

// Surface Colors (Backgrounds & Cards)
val Surface = Color(0xFFFFFBFE)          // Main background (light mode)
val SurfaceVariant = Color(0xFFE7E0EC)   // Secondary surfaces
val Background = Color(0xFFFFFBFE)       // Background same as surface
val OnSurface = Color(0xFF1C1B1F)        // Main text color
val OnSurfaceVariant = Color(0xFF49454F) // Secondary text

// Additional semantic colors from old system
val LockedLeaf = Color(0xFFB0BEC5)       // Locked elements
val UnlockedLeaf = Color(0xFF66BB6A)     // Unlocked/available elements

// Dark Mode Colors
val DarkPrimary = Color(0xFFD0BCFF)      // Light purple for dark mode
val DarkOnPrimary = Color(0xFF381E72)
val DarkPrimaryContainer = Color(0xFF4F378B)
val DarkOnPrimaryContainer = Color(0xFFEADDFF)

val DarkSecondary = Color(0xFFCCC2DC)
val DarkOnSecondary = Color(0xFF332D41)
val DarkSecondaryContainer = Color(0xFF4A4458)
val DarkOnSecondaryContainer = Color(0xFFE8DEF8)

val DarkTertiary = Color(0xFFEFB8C8)
val DarkOnTertiary = Color(0xFF492532)
val DarkTertiaryContainer = Color(0xFF633B48)
val DarkOnTertiaryContainer = Color(0xFFFFD8E4)

val DarkSurface = Color(0xFF1C1B1F)
val DarkOnSurface = Color(0xFFE6E1E5)
val DarkSurfaceVariant = Color(0xFF49454F)
val DarkOnSurfaceVariant = Color(0xFFCAC4D0)
val DarkBackground = Color(0xFF1C1B1F)
val DarkError = Color(0xFFF2B8B5)
val DarkOnError = Color(0xFF601410)

// ============================================================
// OCEAN Theme - Blues and Teals (Calm, Professional)
// ============================================================

object OceanColors {
    // Light variant
    val primaryLight = Color(0xFF0277BD)          // Deep ocean blue
    val secondaryLight = Color(0xFF00ACC1)        // Cyan teal
    val tertiaryLight = Color(0xFF1976D2)         // Rich blue
    val backgroundLight = Color(0xFFF1F8FB)       // Soft sky blue
    val surfaceLight = Color(0xFFE1F5FE)          // Light blue surface
    val onPrimaryLight = Color(0xFFFFFFFF)        // White text
    val onSurfaceLight = Color(0xFF01579B)        // Deep blue text
    val errorLight = Color(0xFFD32F2F)            // Red

    // Dark variant
    val primaryDark = Color(0xFF4FC3F7)           // Light ocean blue
    val secondaryDark = Color(0xFF26C6DA)         // Bright cyan
    val tertiaryDark = Color(0xFF64B5F6)          // Sky blue
    val backgroundDark = Color(0xFF121212)        // Dark background
    val surfaceDark = Color(0xFF1E1E1E)           // Dark surface
    val onPrimaryDark = Color(0xFF000000)         // Black text
    val onSurfaceDark = Color(0xFFE1F5FE)         // Light blue text
    val errorDark = Color(0xFFEF5350)             // Light red

    // AMOLED variant (true black)
    val backgroundAmoled = Color(0xFF000000)      // Pure black
    val surfaceAmoled = Color(0xFF000000)         // Pure black (fixes #212)
}

// ============================================================
// FOREST Theme - Greens and Earth Tones (Natural, Calming)
// ============================================================

object ForestColors {
    // Light variant
    val primaryLight = Color(0xFF2E7D32)          // Forest green
    val secondaryLight = Color(0xFF558B2F)        // Olive green
    val tertiaryLight = Color(0xFF689F38)         // Light green
    val backgroundLight = Color(0xFFF1F8E9)       // Light lime
    val surfaceLight = Color(0xFFDCEDC8)          // Pale green
    val onPrimaryLight = Color(0xFFFFFFFF)        // White text
    val onSurfaceLight = Color(0xFF1B5E20)        // Dark green text
    val errorLight = Color(0xFFD32F2F)            // Red

    // Dark variant
    val primaryDark = Color(0xFF66BB6A)           // Light green
    val secondaryDark = Color(0xFF9CCC65)         // Yellow green
    val tertiaryDark = Color(0xFF81C784)          // Soft green
    val backgroundDark = Color(0xFF121212)        // Dark background
    val surfaceDark = Color(0xFF1E1E1E)           // Dark surface
    val onPrimaryDark = Color(0xFF000000)         // Black text
    val onSurfaceDark = Color(0xFFC5E1A5)         // Light green text
    val errorDark = Color(0xFFEF5350)             // Light red

    // AMOLED variant
    val backgroundAmoled = Color(0xFF000000)      // Pure black
    val surfaceAmoled = Color(0xFF000000)         // Pure black (fixes #212)
}

// ============================================================
// SUNSET Theme - Oranges and Purples (Warm, Creative)
// ============================================================

object SunsetColors {
    // Light variant
    val primaryLight = Color(0xFFE64A19)          // Deep orange
    val secondaryLight = Color(0xFF7B1FA2)        // Purple
    val tertiaryLight = Color(0xFFFF6F00)         // Bright orange
    val backgroundLight = Color(0xFFFFF3E0)       // Light orange
    val surfaceLight = Color(0xFFFFE0B2)          // Peach
    val onPrimaryLight = Color(0xFFFFFFFF)        // White text
    val onSurfaceLight = Color(0xFFBF360C)        // Dark orange text
    val errorLight = Color(0xFFC62828)            // Red

    // Dark variant
    val primaryDark = Color(0xFFFF7043)           // Light orange
    val secondaryDark = Color(0xFFBA68C8)         // Light purple
    val tertiaryDark = Color(0xFFFFB74D)          // Soft orange
    val backgroundDark = Color(0xFF121212)        // Dark background
    val surfaceDark = Color(0xFF1E1E1E)           // Dark surface
    val onPrimaryDark = Color(0xFF000000)         // Black text
    val onSurfaceDark = Color(0xFFFFCC80)         // Light orange text
    val errorDark = Color(0xFFEF5350)             // Light red

    // AMOLED variant
    val backgroundAmoled = Color(0xFF000000)      // Pure black
    val surfaceAmoled = Color(0xFF000000)         // Pure black (fixes #212)
}

// ============================================================
// LAVENDER Theme - Purples and Soft Pinks (Elegant, Gentle)
// ============================================================

object LavenderColors {
    // Light variant
    val primaryLight = Color(0xFF6A1B9A)          // Deep purple
    val secondaryLight = Color(0xFFAB47BC)        // Medium purple
    val tertiaryLight = Color(0xFFD81B60)         // Pink
    val backgroundLight = Color(0xFFF3E5F5)       // Light lavender
    val surfaceLight = Color(0xFFE1BEE7)          // Pale purple
    val onPrimaryLight = Color(0xFFFFFFFF)        // White text
    val onSurfaceLight = Color(0xFF4A148C)        // Dark purple text
    val errorLight = Color(0xFFC62828)            // Red

    // Dark variant
    val primaryDark = Color(0xFFCE93D8)           // Light purple
    val secondaryDark = Color(0xFFF06292)         // Light pink
    val tertiaryDark = Color(0xFFBA68C8)          // Soft purple
    val backgroundDark = Color(0xFF121212)        // Dark background
    val surfaceDark = Color(0xFF1E1E1E)           // Dark surface
    val onPrimaryDark = Color(0xFF000000)         // Black text
    val onSurfaceDark = Color(0xFFF3E5F5)         // Light lavender text
    val errorDark = Color(0xFFEF5350)             // Light red

    // AMOLED variant
    val backgroundAmoled = Color(0xFF000000)      // Pure black
    val surfaceAmoled = Color(0xFF000000)         // Pure black (fixes #212)
}

// ============================================================
// CRIMSON Theme - Reds and Warm Tones (Bold, Energetic)
// ============================================================

object CrimsonColors {
    // Light variant
    val primaryLight = Color(0xFFC62828)          // Deep red
    val secondaryLight = Color(0xFFD84315)        // Red orange
    val tertiaryLight = Color(0xFFAD1457)         // Pink red
    val backgroundLight = Color(0xFFFCE4EC)       // Light pink
    val surfaceLight = Color(0xFFFFCDD2)          // Pale red
    val onPrimaryLight = Color(0xFFFFFFFF)        // White text
    val onSurfaceLight = Color(0xFFB71C1C)        // Dark red text
    val errorLight = Color(0xFF6A1B9A)            // Purple (error contrast)

    // Dark variant
    val primaryDark = Color(0xFFEF5350)           // Light red
    val secondaryDark = Color(0xFFFF7043)         // Light orange
    val tertiaryDark = Color(0xFFEC407A)          // Pink
    val backgroundDark = Color(0xFF121212)        // Dark background
    val surfaceDark = Color(0xFF1E1E1E)           // Dark surface
    val onPrimaryDark = Color(0xFF000000)         // Black text
    val onSurfaceDark = Color(0xFFFFCDD2)         // Light red text
    val errorDark = Color(0xFFCE93D8)             // Light purple

    // AMOLED variant
    val backgroundAmoled = Color(0xFF000000)      // Pure black
    val surfaceAmoled = Color(0xFF000000)         // Pure black (fixes #212)
}

// ============================================================
// MINT Theme - Mint Greens and Fresh Tones (Fresh, Modern)
// ============================================================

object MintColors {
    // Light variant
    val primaryLight = Color(0xFF00897B)          // Teal
    val secondaryLight = Color(0xFF26A69A)        // Light teal
    val tertiaryLight = Color(0xFF00ACC1)         // Cyan
    val backgroundLight = Color(0xFFE0F2F1)       // Pale mint
    val surfaceLight = Color(0xFFB2DFDB)          // Mint surface
    val onPrimaryLight = Color(0xFFFFFFFF)        // White text
    val onSurfaceLight = Color(0xFF004D40)        // Dark teal text
    val errorLight = Color(0xFFD32F2F)            // Red

    // Dark variant
    val primaryDark = Color(0xFF4DB6AC)           // Light teal
    val secondaryDark = Color(0xFF80CBC4)         // Pale teal
    val tertiaryDark = Color(0xFF4DD0E1)          // Light cyan
    val backgroundDark = Color(0xFF121212)        // Dark background
    val surfaceDark = Color(0xFF1E1E1E)           // Dark surface
    val onPrimaryDark = Color(0xFF000000)         // Black text
    val onSurfaceDark = Color(0xFFB2DFDB)         // Light mint text
    val errorDark = Color(0xFFEF5350)             // Light red

    // AMOLED variant
    val backgroundAmoled = Color(0xFF000000)      // Pure black
    val surfaceAmoled = Color(0xFF000000)         // Pure black (fixes #212)
}

// ============================================================
// CEFR Level Colors (from UI/UX Plan)
// ============================================================

/**
 * CEFR (Common European Framework of Reference for Languages) Level Colors
 * Consistent color coding across the app for language proficiency levels
 */
object CEFRColors {
    val A1 = Color(0xFF81C784)      // Light Green - Beginner
    val A2 = Color(0xFF66BB6A)      // Green - Elementary
    val B1 = Color(0xFF42A5F5)      // Blue - Intermediate
    val B2 = Color(0xFF1E88E5)      // Deep Blue - Upper Intermediate
    val C1 = Color(0xFFFFA726)      // Orange - Advanced
    val C2 = Color(0xFFFF7043)      // Deep Orange - Proficient
}

// ============================================================
// Semantic Colors for Statistics & Features
// ============================================================

/**
 * Statistics Color System
 * Semantic colors for quiz results, progress tracking, and statistics displays.
 * Each color has both light and dark theme variants for proper contrast.
 */
object StatsColors {
    // Correct answers - Green
    val correctLight = Color(0xFF4CAF50)      // Material green
    val correctDark = Color(0xFF81C784)       // Lighter green for dark theme

    // Incorrect answers - Red
    val incorrectLight = Color(0xFFE57373)    // Light red
    val incorrectDark = Color(0xFFEF5350)     // Slightly lighter red for contrast

    // Skipped questions - Gray
    val skippedLight = Color(0xFFB0BEC5)      // Blue gray
    val skippedDark = Color(0xFF78909C)       // Darker gray for dark theme

    // Achievement/Gold - Yellow/Gold
    val goldLight = Color(0xFFFFD600)         // Material yellow
    val goldDark = Color(0xFFFFEB3B)          // Lighter yellow for dark theme

    // Success/Achievement - Light Green
    val achievementLight = Color(0xFF81C784)  // Light green
    val achievementDark = Color(0xFF66BB6A)   // Standard green for dark theme

    // Time tracking - Blue
    val timeLight = Color(0xFF64B5F6)         // Light blue
    val timeDark = Color(0xFF42A5F5)          // Standard blue for dark theme

    // Average/Statistics - Purple
    val averageLight = Color(0xFFBA68C8)      // Light purple
    val averageDark = Color(0xFFAB47BC)       // Standard purple for dark theme

    // Quiz count - Cyan
    val quizLight = Color(0xFF4DD0E1)         // Light cyan
    val quizDark = Color(0xFF26C6DA)          // Standard cyan for dark theme

    // Category/Level - Orange
    val categoryLight = Color(0xFFFFB74D)     // Light orange
    val categoryDark = Color(0xFFFF9800)      // Standard orange for dark theme

    // XP/Progress - Purple gradient
    val xpLight = Color(0xFF9C27B0)           // Purple
    val xpDark = Color(0xFFBA68C8)            // Light purple
}

/**
 * Background Gradient Colors
 * Soft gradient backgrounds for various screens
 */
object GradientColors {
    // Stats screen gradient (blue-purple-white)
    val statsGradientLight = listOf(
        Color(0xFFB3E5FC),  // Light blue
        Color(0xFFE1BEE7),  // Light purple
        Color(0xFFFFFFFF)   // White
    )

    val statsGradientDark = listOf(
        Color(0xFF1A237E),  // Deep blue
        Color(0xFF4A148C),  // Deep purple
        Color(0xFF121212)   // Dark background
    )
}

// ============================================================
// Extension Properties for Theme-Aware Color Access
// ============================================================

/**
 * Theme-aware color extensions for ColorScheme
 * Automatically selects light/dark variant based on current theme
 */

val ColorScheme.statsCorrect: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StatsColors.correctDark else StatsColors.correctLight

val ColorScheme.statsIncorrect: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StatsColors.incorrectDark else StatsColors.incorrectLight

val ColorScheme.statsSkipped: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StatsColors.skippedDark else StatsColors.skippedLight

val ColorScheme.statsGold: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StatsColors.goldDark else StatsColors.goldLight

val ColorScheme.statsAchievement: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StatsColors.achievementDark else StatsColors.achievementLight

val ColorScheme.statsTime: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StatsColors.timeDark else StatsColors.timeLight

val ColorScheme.statsAverage: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StatsColors.averageDark else StatsColors.averageLight

val ColorScheme.statsQuiz: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StatsColors.quizDark else StatsColors.quizLight

val ColorScheme.statsCategory: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) StatsColors.categoryDark else StatsColors.categoryLight

fun statsGradient(isDark: Boolean): List<Color> {
    return if (isDark) GradientColors.statsGradientDark else GradientColors.statsGradientLight
}

// ============================================================
// Achievement Tier Colors (Theme-Aware)
// ============================================================

/**
 * Achievement Tier Color System
 * Provides theme-aware colors for achievement badges (Bronze, Silver, Gold, Platinum, Diamond)
 * Each tier has both light and dark theme variants for proper contrast
 */
object TierColors {
    // Bronze tier - Copper/Brown
    val bronzeLight = Color(0xFFCD7F32)       // Bronze color
    val bronzeDark = Color(0xFFE59C6D)        // Lighter bronze for dark theme

    // Silver tier - Gray/Silver
    val silverLight = Color(0xFFC0C0C0)       // Silver gray
    val silverDark = Color(0xFFD3D3D3)        // Lighter silver for dark theme

    // Gold tier - Yellow/Gold
    val goldLight = Color(0xFFFFD700)         // Gold
    val goldDark = Color(0xFFFFE55C)          // Lighter gold for dark theme

    // Platinum tier - Light gray/white
    val platinumLight = Color(0xFFE5E4E2)     // Platinum
    val platinumDark = Color(0xFFF5F5F5)      // Brighter platinum for dark theme

    // Diamond tier - Cyan/Blue
    val diamondLight = Color(0xFF00BCD4)      // Cyan (more saturated for light theme)
    val diamondDark = Color(0xFFB9F2FF)       // Light cyan for dark theme
}

/**
 * Theme-aware tier color extensions for ColorScheme
 * Automatically selects light/dark variant based on current theme
 */

val ColorScheme.tierBronze: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) TierColors.bronzeDark else TierColors.bronzeLight

val ColorScheme.tierSilver: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) TierColors.silverDark else TierColors.silverLight

val ColorScheme.tierGold: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) TierColors.goldDark else TierColors.goldLight

val ColorScheme.tierPlatinum: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) TierColors.platinumDark else TierColors.platinumLight

val ColorScheme.tierDiamond: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) TierColors.diamondDark else TierColors.diamondLight
