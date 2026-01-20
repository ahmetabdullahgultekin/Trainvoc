package com.gultekinahmetabdullah.trainvoc.ui.screen.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gultekinahmetabdullah.trainvoc.R
import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam
import com.gultekinahmetabdullah.trainvoc.ui.theme.AppAnimationDuration
import com.gultekinahmetabdullah.trainvoc.ui.theme.CEFRColors
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing
import com.gultekinahmetabdullah.trainvoc.ui.theme.StaggerDelay
import com.gultekinahmetabdullah.trainvoc.viewmodel.QuizViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * QuizExamMenuScreen - Improved UI/UX based on improvement plan
 *
 * Features:
 * - CEFR Level Cards in 2x3 grid with color coding and difficulty stars
 * - Full-width Exam Type Cards with descriptions
 * - Progress indicators for each level/exam
 * - Staggered entrance animations
 * - Selection animations with scale and border
 * - Locked level indicators
 * - Current user level highlighting
 */
@Composable
fun QuizExamMenuScreen(
    onExamSelected: (QuizParameter) -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    var selectedLevel by remember { mutableStateOf<WordLevel?>(null) }
    val scope = rememberCoroutineScope()

    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
            MaterialTheme.colorScheme.background
        )
    )

    // Get exam types before LazyColumn since it's a Composable function
    val examTypes = getExamTypes()

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.mediumLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            item {
                Text(
                    text = stringResource(id = R.string.select_level_and_exam),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = Spacing.large)
                )
            }

            // CEFR Level Cards Grid (2x3)
            item {
                CEFRLevelGrid(
                    selectedLevel = selectedLevel,
                    onLevelSelected = { level ->
                        selectedLevel = level
                        // Navigate to quiz with selected level after a short animation delay
                        scope.launch {
                            delay(200)
                            onExamSelected(QuizParameter.Level(level))
                        }
                    },
                    viewModel = viewModel
                )
                Spacer(modifier = Modifier.height(Spacing.large))
            }

            // Exam Type Cards (Full-width list)
            item {
                Text(
                    text = "Or Practice by Exam",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = Spacing.medium)
                )
            }

            itemsIndexed(examTypes) { index, examType ->
                AnimatedExamCard(
                    examType = examType,
                    index = index,
                    onClick = { onExamSelected(QuizParameter.ExamType(examType.exam)) },
                    viewModel = viewModel
                )
                if (index < examTypes.size - 1) {
                    Spacer(modifier = Modifier.height(Spacing.medium))
                }
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.large))
            }
        }
    }
}

/**
 * CEFR Level Grid - 2x3 grid of level cards (A1-C2)
 */
@Composable
fun CEFRLevelGrid(
    selectedLevel: WordLevel?,
    onLevelSelected: (WordLevel) -> Unit,
    viewModel: QuizViewModel
) {
    // Get level progress data
    val levels = WordLevel.entries

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(480.dp), // 3 rows * ~160dp each
        userScrollEnabled = false // Disable scrolling within the grid
    ) {
        itemsIndexed(levels) { index, level ->
            AnimatedCEFRLevelCard(
                level = level,
                index = index,
                isSelected = selectedLevel == level,
                onClick = { onLevelSelected(level) },
                viewModel = viewModel
            )
        }
    }
}

/**
 * CEFR Level Card - Individual level card with animation
 */
@Composable
fun AnimatedCEFRLevelCard(
    level: WordLevel,
    index: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    viewModel: QuizViewModel
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    // Staggered entrance animation
    LaunchedEffect(Unit) {
        delay((index * StaggerDelay.extraShort).toLong())
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = AppAnimationDuration.medium)
            )
        }
    }

    // Get progress data for this level
    var learnedWords by remember { mutableStateOf(0) }
    var totalWords by remember { mutableStateOf(0) }

    LaunchedEffect(level) {
        viewModel.collectQuizStats(QuizParameter.Level(level))
    }

    // Collect from viewModel
    val totalWordsState by viewModel.totalWords.collectAsStateWithLifecycle()
    val learnedWordsState by viewModel.learnedWords.collectAsStateWithLifecycle()

    LaunchedEffect(totalWordsState, learnedWordsState) {
        totalWordsState?.let { totalWords = it }
        learnedWordsState?.let { learnedWords = it }
    }

    val progressPercent = if (totalWords > 0) learnedWords.toFloat() / totalWords else 0f
    val isLocked = false // Can be configured based on app logic
    val isCurrentLevel = false // Can be configured based on user's current level

    CEFRLevelCard(
        level = level,
        learnedWords = learnedWords,
        totalWords = totalWords,
        progressPercent = progressPercent,
        isLocked = isLocked,
        isCurrentLevel = isCurrentLevel,
        isSelected = isSelected,
        onClick = onClick,
        modifier = Modifier
            .scale(scale.value)
            .alpha(alpha.value)
    )
}

/**
 * CEFR Level Card Component
 */
@Composable
fun CEFRLevelCard(
    level: WordLevel,
    learnedWords: Int,
    totalWords: Int,
    progressPercent: Float,
    isLocked: Boolean,
    isCurrentLevel: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    // Press animation
    val scale by animateFloatAsState(
        targetValue = if (pressed && !isLocked) 1.05f else 1f,
        animationSpec = tween(200),
        label = "scale"
    )

    // Selection animation
    val borderWidth by animateFloatAsState(
        targetValue = if (isSelected) 3f else 0f,
        animationSpec = tween(200),
        label = "border"
    )

    val cardColor = getCEFRColor(level)
    val difficulty = getDifficultyStars(level)

    Card(
        onClick = {
            if (!isLocked) onClick()
            // TODO: Add shake animation for locked levels
        },
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) cardColor.copy(alpha = 0.5f) else cardColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        ),
        border = if (borderWidth > 0) BorderStroke(borderWidth.dp, Color.White) else null,
        enabled = !isLocked,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Level name and difficulty
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = level.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (isLocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    } else if (isCurrentLevel) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = stringResource(R.string.your_current_level),
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Difficulty stars
                Text(
                    text = difficulty,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )

                // Level description
                Text(
                    text = level.longName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )

                // Progress bar
                if (!isLocked && totalWords > 0) {
                    Column {
                        LinearProgressIndicator(
                            progress = { progressPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.words_mastered, learnedWords),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                } else if (isLocked) {
                    Text(
                        text = stringResource(R.string.coming_soon),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Exam Card - Full-width exam type card with animation
 */
@Composable
fun AnimatedExamCard(
    examType: ExamTypeInfo,
    index: Int,
    onClick: () -> Unit,
    viewModel: QuizViewModel
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay((index * 100L) + 300L) // Start after level cards
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(300)
        )
    ) {
        ExamCard(
            examType = examType,
            onClick = onClick,
            viewModel = viewModel
        )
    }
}

/**
 * Exam Card Component
 */
@Composable
fun ExamCard(
    examType: ExamTypeInfo,
    onClick: () -> Unit,
    viewModel: QuizViewModel
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed && examType.enabled) 1.02f else 1f,
        animationSpec = tween(200),
        label = "scale"
    )

    // Get progress data for this exam
    var availableWords by remember { mutableStateOf(0) }

    LaunchedEffect(examType.exam) {
        viewModel.collectQuizStats(QuizParameter.ExamType(examType.exam))
    }

    val totalWordsState by viewModel.totalWords.collectAsStateWithLifecycle()

    LaunchedEffect(totalWordsState) {
        totalWordsState?.let { availableWords = it }
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (examType.enabled) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 4.dp
        ),
        enabled = examType.enabled,
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Text(
                text = examType.icon,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = examType.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = examType.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (examType.enabled && availableWords > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.words_available, availableWords),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                } else if (!examType.enabled) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.coming_soon),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Arrow
            if (examType.enabled) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Select",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// Helper Functions

/**
 * Get CEFR color for level
 */
fun getCEFRColor(level: WordLevel): Color {
    return when (level) {
        WordLevel.A1 -> CEFRColors.A1
        WordLevel.A2 -> CEFRColors.A2
        WordLevel.B1 -> CEFRColors.B1
        WordLevel.B2 -> CEFRColors.B2
        WordLevel.C1 -> CEFRColors.C1
        WordLevel.C2 -> CEFRColors.C2
    }
}

/**
 * Get difficulty stars for level
 */
fun getDifficultyStars(level: WordLevel): String {
    return when (level) {
        WordLevel.A1 -> "★☆☆"
        WordLevel.A2 -> "★★☆"
        WordLevel.B1 -> "★★★"
        WordLevel.B2 -> "★★★★"
        WordLevel.C1 -> "★★★★★"
        WordLevel.C2 -> "★★★★★★"
    }
}

/**
 * Data class for exam type information
 */
data class ExamTypeInfo(
    val exam: Exam,
    val name: String,
    val description: String,
    val icon: String,
    val enabled: Boolean
)

/**
 * Get list of exam types
 */
@Composable
fun getExamTypes(): List<ExamTypeInfo> {
    return listOf(
        ExamTypeInfo(
            exam = Exam("TOEFL"),
            name = stringResource(R.string.exam_toefl),
            description = stringResource(R.string.toefl_description),
            icon = "📝",
            enabled = false
        ),
        ExamTypeInfo(
            exam = Exam("IELTS"),
            name = stringResource(R.string.exam_ielts),
            description = stringResource(R.string.ielts_description),
            icon = "📚",
            enabled = false
        ),
        ExamTypeInfo(
            exam = Exam("Mixed"),
            name = stringResource(R.string.general_vocab_title),
            description = stringResource(R.string.general_vocab_description),
            icon = "🌍",
            enabled = true
        )
    )
}
