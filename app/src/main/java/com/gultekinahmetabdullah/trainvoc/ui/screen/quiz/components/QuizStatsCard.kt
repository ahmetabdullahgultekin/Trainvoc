package com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Question
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.word.Statistic
import com.gultekinahmetabdullah.trainvoc.viewmodel.QuizViewModel

/**
 * Expandable stats card showing quiz progress and word statistics.
 * Extracted from QuizScreen for better organization.
 *
 * @param showStats Whether to show the stats card
 * @param currentStats Current word statistics
 * @param quizParameter Quiz parameter (Level or ExamType)
 * @param question Current question (used as recomposition trigger)
 * @param quizViewModel ViewModel to collect quiz stats
 */
@Composable
fun QuizStatsCard(
    showStats: Boolean,
    currentStats: Statistic?,
    quizParameter: QuizParameter?,
    question: Question?,
    quizViewModel: QuizViewModel
) {
    AnimatedVisibility(
        visible = showStats && currentStats != null,
        enter = expandIn(expandFrom = Alignment.TopEnd) + fadeIn(),
        exit = shrinkOut(shrinkTowards = Alignment.TopEnd) + fadeOut()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Quiz statistics section
                quizParameter?.let { parameter ->
                    val totalWords by quizViewModel.totalWords.collectAsState()
                    val learnedWords by quizViewModel.learnedWords.collectAsState()
                    val progressPercent by quizViewModel.progressPercent.collectAsState()

                    // Trigger stats collection when parameter or question changes
                    DisposableEffect(parameter, question) {
                        quizViewModel.collectQuizStats(parameter)
                        onDispose { }
                    }

                    when (parameter) {
                        is QuizParameter.Level -> {
                            Text(
                                text = stringResource(R.string.story_mode),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(
                                    R.string.level_with_value,
                                    parameter.wordLevel
                                )
                            )
                            Text(
                                text = stringResource(
                                    R.string.total_words_with_value,
                                    totalWords?.toString() ?: "..."
                                )
                            )
                            Text(
                                text = stringResource(
                                    R.string.learned_words_with_value,
                                    learnedWords?.toString() ?: "..."
                                )
                            )
                            Text(
                                text = stringResource(
                                    R.string.progress_with_value,
                                    progressPercent?.toString() ?: "..."
                                )
                            )
                        }

                        is QuizParameter.ExamType -> {
                            Text(
                                text = stringResource(
                                    R.string.custom_mode_with_value,
                                    parameter.exam.exam
                                ),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(
                                    R.string.exam_with_value,
                                    parameter.exam.exam
                                )
                            )
                            Text(
                                text = stringResource(
                                    R.string.total_words_with_value,
                                    totalWords?.toString() ?: "..."
                                )
                            )
                            Text(
                                text = stringResource(
                                    R.string.learned_words_with_value,
                                    learnedWords?.toString() ?: "..."
                                )
                            )
                            Text(
                                text = stringResource(
                                    R.string.exam_type_with_value,
                                    parameter.exam.exam
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )

                // Word statistics section
                Text(
                    text = stringResource(
                        id = R.string.word_stats,
                        question?.correctWord?.word ?: ""
                    ),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.correct) + ": ${currentStats?.correctCount}",
                    fontSize = 14.sp
                )
                Text(
                    text = stringResource(id = R.string.wrong) + ": ${currentStats?.wrongCount}",
                    fontSize = 14.sp
                )
                Text(
                    text = stringResource(id = R.string.skipped) + ": ${currentStats?.skippedCount}",
                    fontSize = 14.sp
                )
                Text(
                    text = if (currentStats?.learned == true) {
                        stringResource(id = R.string.learned)
                    } else {
                        stringResource(id = R.string.not_learned)
                    },
                    fontSize = 14.sp
                )
            }
        }
    }
}
