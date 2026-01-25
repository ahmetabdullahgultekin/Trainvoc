package com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.ui.animations.shake
import com.gultekinahmetabdullah.trainvoc.ui.theme.AnimationDuration
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.Elevation
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.ui.theme.Success
import kotlinx.coroutines.delay

/**
 * Individual answer option card with animations.
 * Updated to match UI/UX Improvement Plan specifications:
 * - Elevation 1 (default)
 * - Padding: 16dp vertical, 20dp horizontal
 * - Corner radius: 12dp
 * - Border: 2dp (primary when selected)
 * - Spacing between options: 12dp
 * - Answer States & Animations:
 *   - On Selection: Scale 0.95x → 1.0x (spring), Border color primary
 *   - On Correct: Background flash green (200ms), Checkmark bounces in
 *   - On Wrong: Shake animation, Background flash red (200ms), Show correct answer in green
 *
 * @param choice The word choice to display
 * @param correctWord The correct answer word
 * @param selectedAnswer Currently selected answer (null if none selected)
 * @param isCorrect Whether the selected answer is correct (null if not yet checked)
 * @param isTimeUp Whether time has run out
 * @param onChoiceClick Callback when this choice is clicked
 */
@Composable
fun AnswerOptionCard(
    choice: Word,
    correctWord: Word,
    selectedAnswer: Word?,
    isCorrect: Boolean?,
    isTimeUp: Boolean,
    onChoiceClick: (Word) -> Unit
) {
    val cardShape = remember { RoundedCornerShape(CornerRadius.medium) }

    // Trigger shake animation when wrong answer is selected
    var shouldShake by remember { mutableStateOf(false) }

    LaunchedEffect(selectedAnswer, isCorrect) {
        if (selectedAnswer == choice && isCorrect == false) {
            shouldShake = true
            delay(400)
            shouldShake = false
        }
    }

    // Background color animation with flash effect
    val backgroundColor by animateColorAsState(
        targetValue = when {
            selectedAnswer == choice && isCorrect == true -> Success.copy(alpha = 0.2f)
            selectedAnswer == choice && isCorrect == false -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
            choice == correctWord && isCorrect == false -> Success.copy(alpha = 0.2f)
            isTimeUp && choice == correctWord -> Success.copy(alpha = 0.3f)
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(durationMillis = AnimationDuration.quick),
        label = "backgroundColorAnimation"
    )

    // Border color animation
    val borderColor by animateColorAsState(
        targetValue = when {
            selectedAnswer == choice && isCorrect == true -> Success
            selectedAnswer == choice && isCorrect == false -> MaterialTheme.colorScheme.error
            choice == correctWord && isCorrect == false -> Success
            isTimeUp && choice == correctWord -> Success
            selectedAnswer == choice -> MaterialTheme.colorScheme.primary
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = AnimationDuration.quick),
        label = "borderColorAnimation"
    )

    // Scale animation: 0.95x → 1.0x on selection
    val scaleAnim by animateFloatAsState(
        targetValue = when {
            selectedAnswer == choice && isCorrect == null -> 0.95f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scaleAnimation"
    )

    val answerState = when {
        selectedAnswer == choice && isCorrect == true -> "Correct answer"
        selectedAnswer == choice && isCorrect == false -> "Wrong answer"
        choice == correctWord && isCorrect == false -> "This was the correct answer"
        else -> ""
    }
    val semanticDescription = "${choice.meaning}. $answerState".trim()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs) // 12dp spacing between options (6dp top + 6dp bottom)
            .scale(scaleAnim)
            .shake(shouldShake)
            .semantics(mergeDescendants = true) {
                contentDescription = semanticDescription
            }
            .clickable(
                enabled = selectedAnswer == null && isCorrect == null && !isTimeUp,
                onClickLabel = "Select ${choice.meaning}"
            ) {
                onChoiceClick(choice)
            },
        shape = cardShape,
        color = backgroundColor,
        shadowElevation = Elevation.level1,
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = choice.meaning,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Show checkmark or X icon when answer is checked
            if (selectedAnswer == choice && isCorrect != null) {
                Spacer(modifier = Modifier.width(Spacing.sm))

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCorrect) Success
                            else MaterialTheme.colorScheme.error
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = if (isCorrect) "Correct" else "Wrong",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Show checkmark for correct answer when user selected wrong
            if (choice == correctWord && isCorrect == false && selectedAnswer != choice) {
                Spacer(modifier = Modifier.width(Spacing.sm))

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Success),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Correct answer",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
