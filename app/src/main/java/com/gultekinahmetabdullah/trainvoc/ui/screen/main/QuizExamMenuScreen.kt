package com.gultekinahmetabdullah.trainvoc.ui.screen.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam

@Composable
fun QuizExamMenuScreen(onExamSelected: (QuizParameter) -> Unit) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
            MaterialTheme.colorScheme.background
        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .paint(
                painter = painterResource(id = R.drawable.bg_2),
                contentScale = ContentScale.Crop,
                alpha = 0.15f
            )
    ) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.select_quiz_category),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(WordLevel.entries.size) { index ->
                    val level = WordLevel.entries[index]
                    AnimatedQuizCategoryCard(
                        title = stringResource(
                            id = when (level.name) {
                                "A1" -> R.string.level_a1
                                "A2" -> R.string.level_a2
                                "B1" -> R.string.level_b1
                                "B2" -> R.string.level_b2
                                "C1" -> R.string.level_c1
                                "YDS" -> R.string.level_yds
                                "Mixed" -> R.string.level_mixed
                                else -> R.string.level
                            }
                        ),
                        description = stringResource(
                            id = R.string.test_your_knowledge_with,
                            stringResource(
                                id = when (level.name) {
                                    "A1" -> R.string.level_a1
                                    "A2" -> R.string.level_a2
                                    "B1" -> R.string.level_b1
                                    "B2" -> R.string.level_b2
                                    "C1" -> R.string.level_c1
                                    "YDS" -> R.string.level_yds
                                    "Mixed" -> R.string.level_mixed
                                    else -> R.string.level
                                }
                            )
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        onClick = { onExamSelected(QuizParameter.Level(level)) }
                    )
                }
                items(Exam.examTypes.size) { index ->
                    val exam = Exam.examTypes[index]
                    val color = Exam.examColors.entries.firstOrNull { it.key == exam.exam }?.value
                        ?: MaterialTheme.colorScheme.primary
                    AnimatedQuizCategoryCard(
                        title = stringResource(
                            id = when (exam.exam) {
                                "YDS" -> R.string.exam_yds
                                "Mixed" -> R.string.exam_mixed
                                else -> R.string.exam_generic
                            },
                            exam.exam
                        ),
                        description = stringResource(
                            id = R.string.test_your_knowledge_with,
                            stringResource(
                                id = when (exam.exam) {
                                    "YDS" -> R.string.exam_yds
                                    "Mixed" -> R.string.exam_mixed
                                    else -> R.string.exam_generic
                                },
                                exam.exam
                            )
                        ),
                        color = color,
                        onClick = { onExamSelected(QuizParameter.ExamType(exam)) }
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedQuizCategoryCard(
    title: String,
    description: String,
    color: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.07f else 1f,
        animationSpec = tween(200),
        label = ""
    )
    val animatedElevation by animateFloatAsState(
        targetValue = if (pressed) 16f else 6f,
        animationSpec = tween(200),
        label = ""
    )
    val animatedColor by animateColorAsState(
        targetValue = if (pressed) color.copy(alpha = 0.95f) else color,
        animationSpec = tween(200),
        label = ""
    )
    Surface(
        shape = RoundedCornerShape(24.dp),
        tonalElevation = animatedElevation.dp,
        shadowElevation = animatedElevation.dp,
        color = animatedColor,
        modifier = Modifier
            .size(150.dp)
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Text(
                text = description,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.92f),
                textAlign = TextAlign.Center
            )
        }
    }
}