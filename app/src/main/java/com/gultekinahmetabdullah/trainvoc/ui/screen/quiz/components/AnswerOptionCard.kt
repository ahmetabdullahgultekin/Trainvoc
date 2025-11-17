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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gultekinahmetabdullah.trainvoc.classes.word.Word

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
    val backgroundColor by animateColorAsState(
        targetValue = when {
            selectedAnswer == choice && isCorrect == true -> Color(0xFF66BB6A).copy(alpha = 0.7f)
            selectedAnswer == choice && isCorrect == false -> MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            choice == correctWord && isCorrect == false -> Color(0xFF66BB6A).copy(alpha = 0.7f)
            isTimeUp && choice == correctWord -> Color(0xFF66BB6A).copy(alpha = 0.9f)
            else -> MaterialTheme.colorScheme.primaryContainer
        },
        animationSpec = tween(400),
        label = "backgroundColorAnimation"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (selectedAnswer == choice) 1.08f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "scaleAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .scale(scaleAnim)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                enabled = selectedAnswer == null && isCorrect == null && !isTimeUp
            ) {
                onChoiceClick(choice)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
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
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCorrect == true) Color(0xFF66BB6A)
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
