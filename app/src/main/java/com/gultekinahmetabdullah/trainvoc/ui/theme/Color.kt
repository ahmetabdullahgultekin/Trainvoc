package com.gultekinahmetabdullah.trainvoc.ui.theme

import androidx.compose.ui.graphics.Color

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
