package com.gultekinahmetabdullah.trainvoc.ui.animations

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * HapticFeedback Utilities
 *
 * Provides tactile feedback for user interactions throughout the app.
 * Enhances user experience with subtle vibrations on key actions.
 */

/**
 * Haptic feedback types
 */
enum class HapticFeedbackType {
    /** Light click - Quick tap feedback */
    CLICK,

    /** Long press - Sustained press feedback */
    LONG_PRESS,

    /** Keyboard tap - Text input feedback */
    KEYBOARD_TAP,

    /** Context click - Menu/context action feedback */
    CONTEXT_CLICK,

    /** Virtual key - Navigation button feedback */
    VIRTUAL_KEY,

    /** Text handle move - Text selection feedback */
    TEXT_HANDLE_MOVE,

    /** Reject - Error/invalid action feedback */
    REJECT,

    /** Confirm - Success/valid action feedback */
    CONFIRM
}

/**
 * Haptic feedback performer
 * Handles all haptic feedback operations
 */
class HapticPerformer(private val view: View) {

    /**
     * Perform haptic feedback
     *
     * @param type The type of haptic feedback to perform
     * @param flags Optional flags for haptic feedback
     */
    fun perform(
        type: HapticFeedbackType,
        flags: Int = HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
    ) {
        val feedbackConstant = when (type) {
            HapticFeedbackType.CLICK -> HapticFeedbackConstants.VIRTUAL_KEY
            HapticFeedbackType.LONG_PRESS -> HapticFeedbackConstants.LONG_PRESS
            HapticFeedbackType.KEYBOARD_TAP -> HapticFeedbackConstants.KEYBOARD_TAP
            HapticFeedbackType.CONTEXT_CLICK -> HapticFeedbackConstants.CONTEXT_CLICK
            HapticFeedbackType.VIRTUAL_KEY -> HapticFeedbackConstants.VIRTUAL_KEY
            HapticFeedbackType.TEXT_HANDLE_MOVE -> HapticFeedbackConstants.TEXT_HANDLE_MOVE
            HapticFeedbackType.REJECT -> {
                // For reject, we use LONG_PRESS as a heavier feedback
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.REJECT
                } else {
                    HapticFeedbackConstants.LONG_PRESS
                }
            }
            HapticFeedbackType.CONFIRM -> {
                // For confirm, we use a lighter feedback
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.CONFIRM
                } else {
                    HapticFeedbackConstants.VIRTUAL_KEY
                }
            }
        }

        view.performHapticFeedback(feedbackConstant, flags)
    }

    /**
     * Quick click feedback
     * Use for buttons, cards, list items
     */
    fun click() = perform(HapticFeedbackType.CLICK)

    /**
     * Long press feedback
     * Use for drag and drop, long press actions
     */
    fun longPress() = perform(HapticFeedbackType.LONG_PRESS)

    /**
     * Success feedback
     * Use for successful actions, correct answers
     */
    fun success() = perform(HapticFeedbackType.CONFIRM)

    /**
     * Error feedback
     * Use for errors, wrong answers, invalid actions
     */
    fun error() = perform(HapticFeedbackType.REJECT)
}

/**
 * Remember haptic performer
 * Creates and remembers a HapticPerformer for the current composition
 *
 * @return HapticPerformer instance tied to current view
 */
@Composable
fun rememberHapticPerformer(): HapticPerformer {
    val view = LocalView.current
    return remember(view) { HapticPerformer(view) }
}

/**
 * Extension function for easy haptic feedback
 * Usage: view.hapticClick()
 */
fun View.hapticClick() {
    performHapticFeedback(
        HapticFeedbackConstants.VIRTUAL_KEY,
        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
    )
}

/**
 * Extension function for long press haptic feedback
 */
fun View.hapticLongPress() {
    performHapticFeedback(
        HapticFeedbackConstants.LONG_PRESS,
        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
    )
}
