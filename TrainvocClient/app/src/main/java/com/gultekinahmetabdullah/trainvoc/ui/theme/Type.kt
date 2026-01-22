package com.gultekinahmetabdullah.trainvoc.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Complete Material Design 3 Typography System
 *
 * Following Material Design 3 specifications with 15 text styles organized into 5 groups:
 * - Display: Largest text, typically for hero/marketing content (3 sizes)
 * - Headline: Page titles and major section headers (3 sizes)
 * - Title: Section headers and card titles (3 sizes)
 * - Body: Main content and paragraphs (3 sizes)
 * - Label: Buttons, tabs, and chips (3 sizes)
 *
 * Each group has Large, Medium, and Small variants.
 *
 * Reference: https://m3.material.io/styles/typography/type-scale-tokens
 */
val Typography = Typography(
    // ============================================================
    // DISPLAY - Largest text for hero sections and marketing
    // ============================================================

    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,        // 400
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),

    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,        // 400
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),

    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,        // 400
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),

    // ============================================================
    // HEADLINE - Page titles and prominent headers
    // ============================================================

    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,        // 400
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),

    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,        // 400
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),

    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,        // 400
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // ============================================================
    // TITLE - Section headers and card titles
    // ============================================================

    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,        // 400
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,        // 500
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),

    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,        // 500
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // ============================================================
    // BODY - Main content, paragraphs, descriptions
    // ============================================================

    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,        // 400
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,        // 400
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),

    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,        // 400
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // ============================================================
    // LABEL - Buttons, tabs, chips, and small UI elements
    // ============================================================

    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,        // 500
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,        // 500
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),

    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,        // 500
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

/**
 * Typography Usage Guide:
 *
 * DISPLAY:
 * - displayLarge: Hero text on landing pages (rarely used)
 * - displayMedium: Large marketing messages
 * - displaySmall: Prominent calls-to-action
 *
 * HEADLINE:
 * - headlineLarge: Main screen titles (e.g., "Statistics", "Quiz Results")
 * - headlineMedium: Section titles (e.g., "Daily Progress")
 * - headlineSmall: Card headers, dialog titles (e.g., "Welcome Back!")
 *
 * TITLE:
 * - titleLarge: List item titles, emphasized content
 * - titleMedium: Card titles, drawer items (MOST COMMON for titles)
 * - titleSmall: Dense list items, small card headers
 *
 * BODY:
 * - bodyLarge: Emphasized paragraphs, important descriptions
 * - bodyMedium: Default body text (MOST COMMON for content)
 * - bodySmall: Captions, helper text, footnotes
 *
 * LABEL:
 * - labelLarge: Button text, prominent tabs (MOST COMMON for buttons)
 * - labelMedium: Chips, small tabs
 * - labelSmall: Overlines, tiny labels
 *
 * Example Usage:
 * ```
 * Text("Screen Title", style = MaterialTheme.typography.headlineMedium)
 * Text("Section Header", style = MaterialTheme.typography.titleMedium)
 * Text("Body content goes here", style = MaterialTheme.typography.bodyMedium)
 * Button { Text("Action", style = MaterialTheme.typography.labelLarge) }
 * ```
 */
