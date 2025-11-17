package com.gultekinahmetabdullah.trainvoc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkOnPrimary,
    onSecondary = DarkOnPrimary,
    onBackground = DarkOnPrimary,
    onSurface = DarkOnSurface,
    error = DarkError,

    )

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    background = Background,
    surface = Surface,
    onPrimary = OnPrimary,
    onSecondary = OnPrimary,
    onBackground = OnPrimary,
    onSurface = OnSurface,
    error = Error,
)

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

@Composable
fun TrainvocTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        /*dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }*/

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}