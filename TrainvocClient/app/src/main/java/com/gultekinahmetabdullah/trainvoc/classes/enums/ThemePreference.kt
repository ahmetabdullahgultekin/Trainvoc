package com.gultekinahmetabdullah.trainvoc.classes.enums

/**
 * Theme Preference Enum
 *
 * Defines available theme modes and color palettes for the application.
 *
 * Theme Modes:
 * - SYSTEM: Follow system dark/light mode setting
 * - LIGHT: Always use light theme
 * - DARK: Always use dark theme
 * - AMOLED: Dark theme with true black background (OLED optimized)
 *
 * Color Palettes:
 * - DEFAULT: Original app colors (soft teal/cyan)
 * - OCEAN: Blues and teals (calm, professional)
 * - FOREST: Greens and earth tones (natural, calming)
 * - SUNSET: Oranges and purples (warm, creative)
 * - LAVENDER: Purples and soft pinks (elegant, gentle)
 * - CRIMSON: Reds and warm tones (bold, energetic)
 * - MINT: Mint greens and fresh tones (fresh, modern)
 * - DYNAMIC: Material You dynamic colors (Android 12+)
 */
enum class ThemePreference(val key: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark"),
    AMOLED("amoled");
}

/**
 * Color Palette Preference Enum
 *
 * Defines available color palettes that can be combined with any theme mode.
 */
enum class ColorPalettePreference(val key: String, val displayName: String) {
    DEFAULT("default", "Default"),
    OCEAN("ocean", "Ocean"),
    FOREST("forest", "Forest"),
    SUNSET("sunset", "Sunset"),
    LAVENDER("lavender", "Lavender"),
    CRIMSON("crimson", "Crimson"),
    MINT("mint", "Mint"),
    DYNAMIC("dynamic", "Dynamic"); // Material You (Android 12+)

    companion object {
        fun fromKey(key: String): ColorPalettePreference {
            return entries.find { it.key == key } ?: DEFAULT
        }
    }
}