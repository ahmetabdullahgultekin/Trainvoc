package com.gultekinahmetabdullah.trainvoc.ui.tutorial

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.gultekinahmetabdullah.trainvoc.ui.games.GameType

/**
 * Represents a single tutorial step with content and optional UI highlighting.
 */
data class TutorialStep(
    val id: String,
    val title: String,
    val description: String,
    val highlightBounds: HighlightBounds? = null,
    val pointerDirection: PointerDirection = PointerDirection.BOTTOM,
    val action: TutorialAction = TutorialAction.CONTINUE,
    val lottieAnimation: String? = null,
    val showSkip: Boolean = true
)

/**
 * Bounds for UI element highlighting with spotlight effect.
 */
data class HighlightBounds(
    val offset: Offset,
    val size: Size,
    val cornerRadius: Float = 12f
)

/**
 * Direction for tooltip pointer arrow.
 */
enum class PointerDirection {
    TOP,
    BOTTOM,
    LEFT,
    RIGHT
}

/**
 * Action type for tutorial step navigation.
 */
enum class TutorialAction {
    CONTINUE,
    WAIT_FOR_ACTION,
    PRACTICE,
    COMPLETE
}

/**
 * Tutorial state for a specific game.
 */
data class TutorialState(
    val gameType: GameType,
    val steps: List<TutorialStep>,
    val currentStepIndex: Int = 0,
    val isActive: Boolean = false,
    val isPracticeMode: Boolean = false
) {
    val currentStep: TutorialStep?
        get() = steps.getOrNull(currentStepIndex)

    val isComplete: Boolean
        get() = currentStepIndex >= steps.size

    val progress: Float
        get() = if (steps.isEmpty()) 1f else (currentStepIndex + 1f) / steps.size

    val isLastStep: Boolean
        get() = currentStepIndex == steps.lastIndex

    companion object {
        fun empty() = TutorialState(
            gameType = GameType.MULTIPLE_CHOICE,
            steps = emptyList(),
            isActive = false
        )
    }
}
