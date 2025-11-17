package com.gultekinahmetabdullah.trainvoc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// ============================================================
// Default Theme Colors (Original)
// ============================================================

// Yeni ana palet renkleri
val Primary = Color(0xFFAAD7D9) // Ana vurgu
val Secondary = Color(0xFF92C7CF) // İkincil vurgu
val Background = Color(0xFFFBF9F1) // Arka plan
val Surface = Color(0xFFE5E1DA) // Kartlar, kutular
val OnPrimary = Color(0xFF212121) // Ana metin (koyu)
val OnSurface = Color(0xFF212121) // Yüzey üzeri metin (koyu)
val Error = Color(0xFFE53935) // Hata
val LockedLeaf = Color(0xFFB0BEC5) // Kilitli yaprak rengi
val UnlockedLeaf = Color(0xFF66BB6A) // Kilitsiz yaprak rengi

// Koyu tema renkleri
val DarkPrimary = Color(0xFF3B6E85) // Ana vurgu (koyu)
val DarkSecondary = Color(0xFF2A9D8F) // İkincil vurgu (koyu)
val DarkBackground = Color(0xFF121212) // Arka plan (koyu)
val DarkSurface = Color(0xFF1E1E1E) // Kartlar, kutular (koyu)
val DarkOnPrimary = Color(0xFFFFFFFF) // Ana metin (açık)
val DarkOnSurface = Color(0xFFFFFFFF) // Yüzey üzeri metin (açık)
val DarkError = Color(0xFFCF6679) // Hata (koyu tema için)

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
    val surfaceAmoled = Color(0xFF0D0D0D)         // Almost black
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
    val surfaceAmoled = Color(0xFF0D0D0D)         // Almost black
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
    val surfaceAmoled = Color(0xFF0D0D0D)         // Almost black
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
    val surfaceAmoled = Color(0xFF0D0D0D)         // Almost black
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
    val surfaceAmoled = Color(0xFF0D0D0D)         // Almost black
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
    val surfaceAmoled = Color(0xFF0D0D0D)         // Almost black
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
    // Correct answers - Green (already have UnlockedLeaf)
    val correctLight = Color(0xFF66BB6A)      // Green - same as UnlockedLeaf
    val correctDark = Color(0xFF81C784)       // Lighter green for dark theme

    // Incorrect answers - Red
    val incorrectLight = Color(0xFFE57373)    // Light red
    val incorrectDark = Color(0xFFEF5350)     // Slightly lighter red for contrast

    // Skipped questions - Gray
    val skippedLight = Color(0xFFB0BEC5)      // Blue gray - same as LockedLeaf
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
