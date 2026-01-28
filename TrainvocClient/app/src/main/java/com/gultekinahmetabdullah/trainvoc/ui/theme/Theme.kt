package com.gultekinahmetabdullah.trainvoc.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.gultekinahmetabdullah.trainvoc.accessibility.HighContrastDarkColorScheme
import com.gultekinahmetabdullah.trainvoc.accessibility.HighContrastLightColorScheme
import com.gultekinahmetabdullah.trainvoc.classes.enums.ColorPalettePreference

// ============================================================
// Default Color Schemes (Material 3 Purple Theme from Plan)
// ============================================================

private val DefaultLightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    error = Error,
    onError = OnError,
    background = Background,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
)

private val DefaultDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
)

// Default AMOLED Color Scheme (fixes #203)
private val DefaultAmoledColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    background = Color(0xFF000000),
    onBackground = DarkOnSurface,
    surface = Color(0xFF000000),
    onSurface = DarkOnSurface,
    surfaceVariant = Color(0xFF1C1B1F),
    onSurfaceVariant = DarkOnSurfaceVariant,
)

// ============================================================
// Ocean Color Schemes
// ============================================================

private val OceanLightColorScheme = lightColorScheme(
    primary = OceanColors.primaryLight,
    secondary = OceanColors.secondaryLight,
    tertiary = OceanColors.tertiaryLight,
    background = OceanColors.backgroundLight,
    surface = OceanColors.surfaceLight,
    onPrimary = OceanColors.onPrimaryLight,
    onSecondary = OceanColors.onPrimaryLight,
    onTertiary = OceanColors.onPrimaryLight,
    onBackground = OceanColors.onSurfaceLight,
    onSurface = OceanColors.onSurfaceLight,
    error = OceanColors.errorLight,
)

private val OceanDarkColorScheme = darkColorScheme(
    primary = OceanColors.primaryDark,
    secondary = OceanColors.secondaryDark,
    tertiary = OceanColors.tertiaryDark,
    background = OceanColors.backgroundDark,
    surface = OceanColors.surfaceDark,
    onPrimary = OceanColors.onPrimaryDark,
    onSecondary = OceanColors.onPrimaryDark,
    onTertiary = OceanColors.onPrimaryDark,
    onBackground = OceanColors.onSurfaceDark,
    onSurface = OceanColors.onSurfaceDark,
    error = OceanColors.errorDark,
)

private val OceanAmoledColorScheme = darkColorScheme(
    primary = OceanColors.primaryDark,
    secondary = OceanColors.secondaryDark,
    tertiary = OceanColors.tertiaryDark,
    background = OceanColors.backgroundAmoled,
    surface = OceanColors.surfaceAmoled,
    onPrimary = OceanColors.onPrimaryDark,
    onSecondary = OceanColors.onPrimaryDark,
    onTertiary = OceanColors.onPrimaryDark,
    onBackground = OceanColors.onSurfaceDark,
    onSurface = OceanColors.onSurfaceDark,
    error = OceanColors.errorDark,
)

// ============================================================
// Forest Color Schemes
// ============================================================

private val ForestLightColorScheme = lightColorScheme(
    primary = ForestColors.primaryLight,
    secondary = ForestColors.secondaryLight,
    tertiary = ForestColors.tertiaryLight,
    background = ForestColors.backgroundLight,
    surface = ForestColors.surfaceLight,
    onPrimary = ForestColors.onPrimaryLight,
    onSecondary = ForestColors.onPrimaryLight,
    onTertiary = ForestColors.onPrimaryLight,
    onBackground = ForestColors.onSurfaceLight,
    onSurface = ForestColors.onSurfaceLight,
    error = ForestColors.errorLight,
)

private val ForestDarkColorScheme = darkColorScheme(
    primary = ForestColors.primaryDark,
    secondary = ForestColors.secondaryDark,
    tertiary = ForestColors.tertiaryDark,
    background = ForestColors.backgroundDark,
    surface = ForestColors.surfaceDark,
    onPrimary = ForestColors.onPrimaryDark,
    onSecondary = ForestColors.onPrimaryDark,
    onTertiary = ForestColors.onPrimaryDark,
    onBackground = ForestColors.onSurfaceDark,
    onSurface = ForestColors.onSurfaceDark,
    error = ForestColors.errorDark,
)

private val ForestAmoledColorScheme = darkColorScheme(
    primary = ForestColors.primaryDark,
    secondary = ForestColors.secondaryDark,
    tertiary = ForestColors.tertiaryDark,
    background = ForestColors.backgroundAmoled,
    surface = ForestColors.surfaceAmoled,
    onPrimary = ForestColors.onPrimaryDark,
    onSecondary = ForestColors.onPrimaryDark,
    onTertiary = ForestColors.onPrimaryDark,
    onBackground = ForestColors.onSurfaceDark,
    onSurface = ForestColors.onSurfaceDark,
    error = ForestColors.errorDark,
)

// ============================================================
// Sunset Color Schemes
// ============================================================

private val SunsetLightColorScheme = lightColorScheme(
    primary = SunsetColors.primaryLight,
    secondary = SunsetColors.secondaryLight,
    tertiary = SunsetColors.tertiaryLight,
    background = SunsetColors.backgroundLight,
    surface = SunsetColors.surfaceLight,
    onPrimary = SunsetColors.onPrimaryLight,
    onSecondary = SunsetColors.onPrimaryLight,
    onTertiary = SunsetColors.onPrimaryLight,
    onBackground = SunsetColors.onSurfaceLight,
    onSurface = SunsetColors.onSurfaceLight,
    error = SunsetColors.errorLight,
)

private val SunsetDarkColorScheme = darkColorScheme(
    primary = SunsetColors.primaryDark,
    secondary = SunsetColors.secondaryDark,
    tertiary = SunsetColors.tertiaryDark,
    background = SunsetColors.backgroundDark,
    surface = SunsetColors.surfaceDark,
    onPrimary = SunsetColors.onPrimaryDark,
    onSecondary = SunsetColors.onPrimaryDark,
    onTertiary = SunsetColors.onPrimaryDark,
    onBackground = SunsetColors.onSurfaceDark,
    onSurface = SunsetColors.onSurfaceDark,
    error = SunsetColors.errorDark,
)

private val SunsetAmoledColorScheme = darkColorScheme(
    primary = SunsetColors.primaryDark,
    secondary = SunsetColors.secondaryDark,
    tertiary = SunsetColors.tertiaryDark,
    background = SunsetColors.backgroundAmoled,
    surface = SunsetColors.surfaceAmoled,
    onPrimary = SunsetColors.onPrimaryDark,
    onSecondary = SunsetColors.onPrimaryDark,
    onTertiary = SunsetColors.onPrimaryDark,
    onBackground = SunsetColors.onSurfaceDark,
    onSurface = SunsetColors.onSurfaceDark,
    error = SunsetColors.errorDark,
)

// ============================================================
// Lavender Color Schemes
// ============================================================

private val LavenderLightColorScheme = lightColorScheme(
    primary = LavenderColors.primaryLight,
    secondary = LavenderColors.secondaryLight,
    tertiary = LavenderColors.tertiaryLight,
    background = LavenderColors.backgroundLight,
    surface = LavenderColors.surfaceLight,
    onPrimary = LavenderColors.onPrimaryLight,
    onSecondary = LavenderColors.onPrimaryLight,
    onTertiary = LavenderColors.onPrimaryLight,
    onBackground = LavenderColors.onSurfaceLight,
    onSurface = LavenderColors.onSurfaceLight,
    error = LavenderColors.errorLight,
)

private val LavenderDarkColorScheme = darkColorScheme(
    primary = LavenderColors.primaryDark,
    secondary = LavenderColors.secondaryDark,
    tertiary = LavenderColors.tertiaryDark,
    background = LavenderColors.backgroundDark,
    surface = LavenderColors.surfaceDark,
    onPrimary = LavenderColors.onPrimaryDark,
    onSecondary = LavenderColors.onPrimaryDark,
    onTertiary = LavenderColors.onPrimaryDark,
    onBackground = LavenderColors.onSurfaceDark,
    onSurface = LavenderColors.onSurfaceDark,
    error = LavenderColors.errorDark,
)

private val LavenderAmoledColorScheme = darkColorScheme(
    primary = LavenderColors.primaryDark,
    secondary = LavenderColors.secondaryDark,
    tertiary = LavenderColors.tertiaryDark,
    background = LavenderColors.backgroundAmoled,
    surface = LavenderColors.surfaceAmoled,
    onPrimary = LavenderColors.onPrimaryDark,
    onSecondary = LavenderColors.onPrimaryDark,
    onTertiary = LavenderColors.onPrimaryDark,
    onBackground = LavenderColors.onSurfaceDark,
    onSurface = LavenderColors.onSurfaceDark,
    error = LavenderColors.errorDark,
)

// ============================================================
// Crimson Color Schemes
// ============================================================

private val CrimsonLightColorScheme = lightColorScheme(
    primary = CrimsonColors.primaryLight,
    secondary = CrimsonColors.secondaryLight,
    tertiary = CrimsonColors.tertiaryLight,
    background = CrimsonColors.backgroundLight,
    surface = CrimsonColors.surfaceLight,
    onPrimary = CrimsonColors.onPrimaryLight,
    onSecondary = CrimsonColors.onPrimaryLight,
    onTertiary = CrimsonColors.onPrimaryLight,
    onBackground = CrimsonColors.onSurfaceLight,
    onSurface = CrimsonColors.onSurfaceLight,
    error = CrimsonColors.errorLight,
)

private val CrimsonDarkColorScheme = darkColorScheme(
    primary = CrimsonColors.primaryDark,
    secondary = CrimsonColors.secondaryDark,
    tertiary = CrimsonColors.tertiaryDark,
    background = CrimsonColors.backgroundDark,
    surface = CrimsonColors.surfaceDark,
    onPrimary = CrimsonColors.onPrimaryDark,
    onSecondary = CrimsonColors.onPrimaryDark,
    onTertiary = CrimsonColors.onPrimaryDark,
    onBackground = CrimsonColors.onSurfaceDark,
    onSurface = CrimsonColors.onSurfaceDark,
    error = CrimsonColors.errorDark,
)

private val CrimsonAmoledColorScheme = darkColorScheme(
    primary = CrimsonColors.primaryDark,
    secondary = CrimsonColors.secondaryDark,
    tertiary = CrimsonColors.tertiaryDark,
    background = CrimsonColors.backgroundAmoled,
    surface = CrimsonColors.surfaceAmoled,
    onPrimary = CrimsonColors.onPrimaryDark,
    onSecondary = CrimsonColors.onPrimaryDark,
    onTertiary = CrimsonColors.onPrimaryDark,
    onBackground = CrimsonColors.onSurfaceDark,
    onSurface = CrimsonColors.onSurfaceDark,
    error = CrimsonColors.errorDark,
)

// ============================================================
// Mint Color Schemes
// ============================================================

private val MintLightColorScheme = lightColorScheme(
    primary = MintColors.primaryLight,
    secondary = MintColors.secondaryLight,
    tertiary = MintColors.tertiaryLight,
    background = MintColors.backgroundLight,
    surface = MintColors.surfaceLight,
    onPrimary = MintColors.onPrimaryLight,
    onSecondary = MintColors.onPrimaryLight,
    onTertiary = MintColors.onPrimaryLight,
    onBackground = MintColors.onSurfaceLight,
    onSurface = MintColors.onSurfaceLight,
    error = MintColors.errorLight,
)

private val MintDarkColorScheme = darkColorScheme(
    primary = MintColors.primaryDark,
    secondary = MintColors.secondaryDark,
    tertiary = MintColors.tertiaryDark,
    background = MintColors.backgroundDark,
    surface = MintColors.surfaceDark,
    onPrimary = MintColors.onPrimaryDark,
    onSecondary = MintColors.onPrimaryDark,
    onTertiary = MintColors.onPrimaryDark,
    onBackground = MintColors.onSurfaceDark,
    onSurface = MintColors.onSurfaceDark,
    error = MintColors.errorDark,
)

private val MintAmoledColorScheme = darkColorScheme(
    primary = MintColors.primaryDark,
    secondary = MintColors.secondaryDark,
    tertiary = MintColors.tertiaryDark,
    background = MintColors.backgroundAmoled,
    surface = MintColors.surfaceAmoled,
    onPrimary = MintColors.onPrimaryDark,
    onSecondary = MintColors.onPrimaryDark,
    onTertiary = MintColors.onPrimaryDark,
    onBackground = MintColors.onSurfaceDark,
    onSurface = MintColors.onSurfaceDark,
    error = MintColors.errorDark,
)

// ============================================================
// Color Scheme Selector Functions
// ============================================================

/**
 * Get the appropriate ColorScheme based on palette, dark mode, and accessibility options
 */
@Composable
private fun getColorScheme(
    palette: ColorPalettePreference,
    darkTheme: Boolean,
    amoledMode: Boolean = false,
    highContrastMode: Boolean = false
): ColorScheme {
    val context = LocalContext.current

    // High contrast mode for accessibility (fixes #211 - AMOLED can combine with high contrast)
    if (highContrastMode) {
        val baseScheme = if (darkTheme) HighContrastDarkColorScheme else HighContrastLightColorScheme
        if (amoledMode && darkTheme) {
            return baseScheme.copy(
                background = Color(0xFF000000),
                surface = Color(0xFF000000)
            )
        }
        return baseScheme
    }

    return when (palette) {
        ColorPalettePreference.DYNAMIC -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                // Fallback to default if dynamic colors not available
                if (darkTheme) DefaultDarkColorScheme else DefaultLightColorScheme
            }
        }

        ColorPalettePreference.OCEAN -> {
            when {
                amoledMode -> OceanAmoledColorScheme
                darkTheme -> OceanDarkColorScheme
                else -> OceanLightColorScheme
            }
        }

        ColorPalettePreference.FOREST -> {
            when {
                amoledMode -> ForestAmoledColorScheme
                darkTheme -> ForestDarkColorScheme
                else -> ForestLightColorScheme
            }
        }

        ColorPalettePreference.SUNSET -> {
            when {
                amoledMode -> SunsetAmoledColorScheme
                darkTheme -> SunsetDarkColorScheme
                else -> SunsetLightColorScheme
            }
        }

        ColorPalettePreference.LAVENDER -> {
            when {
                amoledMode -> LavenderAmoledColorScheme
                darkTheme -> LavenderDarkColorScheme
                else -> LavenderLightColorScheme
            }
        }

        ColorPalettePreference.CRIMSON -> {
            when {
                amoledMode -> CrimsonAmoledColorScheme
                darkTheme -> CrimsonDarkColorScheme
                else -> CrimsonLightColorScheme
            }
        }

        ColorPalettePreference.MINT -> {
            when {
                amoledMode -> MintAmoledColorScheme
                darkTheme -> MintDarkColorScheme
                else -> MintLightColorScheme
            }
        }

        ColorPalettePreference.DEFAULT -> {
            when {
                amoledMode -> DefaultAmoledColorScheme
                darkTheme -> DefaultDarkColorScheme
                else -> DefaultLightColorScheme
            }
        }
    }
}

/**
 * Material Design 3 Shape System
 * Centralized shape definitions using CornerRadius tokens from Dimensions.kt
 * Provides consistent rounded corners throughout the application.
 */
private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(CornerRadius.extraSmall),  // 4dp
    small = RoundedCornerShape(CornerRadius.small),            // 8dp
    medium = RoundedCornerShape(CornerRadius.medium),          // 12dp
    large = RoundedCornerShape(CornerRadius.large),            // 16dp
    extraLarge = RoundedCornerShape(CornerRadius.extraLarge)   // 24dp
)

/**
 * Trainvoc Theme
 *
 * Main theme composable that applies Material Design 3 theming to the app.
 *
 * @param darkTheme Whether to use dark theme (default: follows system)
 * @param amoledMode Whether to use AMOLED true black mode (only applies in dark theme)
 * @param colorPalette Color palette to use (default: DEFAULT)
 * @param highContrastMode Whether to use WCAG AAA high contrast colors for accessibility
 * @param content The composable content to wrap with the theme
 */
@Composable
fun TrainvocTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    amoledMode: Boolean = false,
    colorPalette: ColorPalettePreference = ColorPalettePreference.DEFAULT,
    highContrastMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(
        palette = colorPalette,
        darkTheme = darkTheme,
        amoledMode = amoledMode && darkTheme, // AMOLED only applies in dark mode
        highContrastMode = highContrastMode
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}