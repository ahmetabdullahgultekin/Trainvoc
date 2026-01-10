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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gultekinahmetabdullah.trainvoc.classes.word.Word
import com.gultekinahmetabdullah.trainvoc.ui.theme.AnimationDuration
import com.gultekinahmetabdullah.trainvoc.ui.theme.CornerRadius
import com.gultekinahmetabdullah.trainvoc.ui.theme.IconSize
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.ui.theme.UnlockedLeaf

/**
 * Individual answer option card with animations.
 * Extracted from QuizScreen for better organization.
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
    val cardShape = remember { RoundedCornerShape(CornerRadius.large) }

    val backgroundColor by animateColorAsState(
        targetValue = when {
            selectedAnswer == choice && isCorrect == true -> UnlockedLeaf.copy(alpha = 0.7f)
            selectedAnswer == choice && isCorrect == false -> MaterialTheme.colorScheme.error.copy(
                alpha = 0.7f
            )

            choice == correctWord && isCorrect == false -> UnlockedLeaf.copy(alpha = 0.7f)
            isTimeUp && choice == correctWord -> UnlockedLeaf.copy(alpha = 0.9f)
            else -> MaterialTheme.colorScheme.primaryContainer
        },
        animationSpec = tween(AnimationDuration.answerFeedback),
        label = "backgroundColorAnimation"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (selectedAnswer == choice) 1.08f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "scaleAnimation"
    )

    val answerState = when {
        selectedAnswer == choice && isCorrect == true -> "Correct answer"
        selectedAnswer == choice && isCorrect == false -> "Wrong answer"
        choice == correctWord && isCorrect == false -> "This was the correct answer"
        else -> ""
    }
    val semanticDescription = "${choice.meaning}. $answerState".trim()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.small)
            .scale(scaleAnim)
            .clip(cardShape)
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
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 14.dp) // Minimum 48dp touch target
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = choice.meaning,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (selectedAnswer == choice) {
                Box(
                    modifier = Modifier
                        .size(IconSize.large)
                        .clip(CircleShape)
                        .background(
                            if (isCorrect == true) UnlockedLeaf
                            else MaterialTheme.colorScheme.error
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCorrect == true) Icons.Default.CheckCircle else Icons.Default.Close,
                        contentDescription = if (isCorrect == true) "Correct" else "Wrong",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
