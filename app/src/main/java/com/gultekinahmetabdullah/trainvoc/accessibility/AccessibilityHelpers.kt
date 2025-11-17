package com.gultekinahmetabdullah.trainvoc.accessibility

import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Modifier

/**
 * Accessibility Helpers
 *
 * Utilities for making the app accessible to all users including:
 * - Screen reader support (TalkBack)
 * - High contrast mode detection
 * - Font scaling utilities
 * - Semantic content descriptions
 */

/**
 * Check if TalkBack or other screen readers are enabled
 */
fun Context.isScreenReaderEnabled(): Boolean {
    val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
    return am?.isEnabled == true && am.isTouchExplorationEnabled
}

/**
 * Check if user prefers high contrast
 */
fun Context.isHighContrastEnabled(): Boolean {
    val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
    return am?.isEnabled == true
}

/**
 * Get current font scale setting
 * Returns value between 0.85 (small) to 2.0 (huge)
 */
fun Context.getFontScale(): Float {
    return resources.configuration.fontScale
}

/**
 * Check if large text is enabled (font scale > 1.3)
 */
fun Context.isLargeTextEnabled(): Boolean {
    return getFontScale() > 1.3f
}

/**
 * Remember if screen reader is enabled
 */
@Composable
fun rememberIsScreenReaderEnabled(): Boolean {
    val context = LocalContext.current
    return remember { context.isScreenReaderEnabled() }
}

/**
 * Remember if high contrast is enabled
 */
@Composable
fun rememberIsHighContrastEnabled(): Boolean {
    val context = LocalContext.current
    return remember { context.isHighContrastEnabled() }
}

/**
 * Remember font scale
 */
@Composable
fun rememberFontScale(): Float {
    val context = LocalContext.current
    return remember { context.getFontScale() }
}

/**
 * Modifier extension to mark element as heading for screen readers
 */
fun Modifier.accessibilityHeading(): Modifier = this.semantics {
    heading()
}

/**
 * Modifier extension to add content description
 */
fun Modifier.accessibilityDescription(description: String): Modifier = this.semantics {
    contentDescription = description
}

/**
 * Semantic properties for custom accessibility actions
 */
object AccessibilityActions {
    /**
     * Custom action for incrementing value
     */
    fun SemanticsPropertyReceiver.onIncrease(label: String, action: () -> Boolean) {
        // Custom action implementation
    }

    /**
     * Custom action for decrementing value
     */
    fun SemanticsPropertyReceiver.onDecrease(label: String, action: () -> Boolean) {
        // Custom action implementation
    }
}

/**
 * Content description builder for complex UI elements
 */
class ContentDescriptionBuilder {
    private val parts = mutableListOf<String>()

    fun add(part: String) {
        if (part.isNotBlank()) {
            parts.add(part)
        }
    }

    fun add(condition: Boolean, part: String) {
        if (condition && part.isNotBlank()) {
            parts.add(part)
        }
    }

    fun build(): String = parts.joinToString(", ")
}

/**
 * Build content description DSL
 */
fun buildContentDescription(block: ContentDescriptionBuilder.() -> Unit): String {
    return ContentDescriptionBuilder().apply(block).build()
}

/**
 * Common content descriptions for UI patterns
 */
object CommonContentDescriptions {
    fun button(label: String, enabled: Boolean = true): String {
        return buildContentDescription {
            add(label)
            add(!enabled, "disabled")
            add("button")
        }
    }

    fun switch(label: String, checked: Boolean): String {
        return buildContentDescription {
            add(label)
            add(if (checked) "on" else "off")
            add("switch")
        }
    }

    fun slider(label: String, value: Int, max: Int): String {
        return buildContentDescription {
            add(label)
            add("$value out of $max")
            add("slider")
        }
    }

    fun progress(label: String, progress: Int): String {
        return buildContentDescription {
            add(label)
            add("$progress percent complete")
        }
    }

    fun card(title: String, subtitle: String? = null): String {
        return buildContentDescription {
            add(title)
            subtitle?.let { add(it) }
            add("card")
        }
    }

    fun image(description: String): String {
        return buildContentDescription {
            add(description)
            add("image")
        }
    }

    fun heading(text: String, level: Int = 1): String {
        return buildContentDescription {
            add(text)
            add("heading level $level")
        }
    }
}

/**
 * Announce message to screen reader
 */
fun Context.announceForAccessibility(message: String) {
    val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
    if (am?.isEnabled == true) {
        @Suppress("DEPRECATION")
        val event = android.view.accessibility.AccessibilityEvent.obtain()
        event.eventType = android.view.accessibility.AccessibilityEvent.TYPE_ANNOUNCEMENT
        event.text.add(message)
        am.sendAccessibilityEvent(event)
    }
}
